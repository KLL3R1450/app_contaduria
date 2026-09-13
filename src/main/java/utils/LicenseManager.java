package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestor de licenciamiento fuera de línea (Offline Asymmetric Licensing).
 * Valida la firma digital RSA 2048 con SHA256 y verifica el amarre al Hardware ID (HWID).
 *
 * @author Osmar & Antigravity
 */
public class LicenseManager {

    public static final String ARCHIVO_LICENCIA = "license.lic";
    public static final String SEPARADOR_FIRMA = "---SIGNATURE---";

    // Clave pública RSA 2048 bits embebida (formato X.509 SPKI en Base64)
    public static final String PUBLIC_KEY_BASE64 =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAheVc0Z7SyROiXsMUg5uhMlyvdpUXB00hHPawYjdG6atj3H3eEL04/SX0Q5MxlK6UyZjzAT8XhpEvBwJaVDw+rDQr/ILKvz4M8LhZb4oOay6rnAjg8HcZgWtvYj8vr5pGbUsMb3qXs/XRmGV+GwatC2O8XZtyVzSBFvgv9DRsNfrUmb3IFMb2bv+ug/7uPEYcvc7C+8rFoRB5h9zbRxjJi9SdJephVdBGQ/UGwlI5tsu2XslF1/MOqyrK9QY7FmkjSHhSzdKh9dGfxn3UpKg/h5R2SUYx+F+6CDwjhXI+sF9OqK3qO+U9qUoff+H6ARy/Of+ccALVwfoeHotT+61QCwIDAQAB";

    public static class ResultadoValidacion {
        public final boolean valida;
        public final String mensaje;
        public final String cliente;
        public final String hwid;

        public ResultadoValidacion(boolean valida, String mensaje, String cliente, String hwid) {
            this.valida = valida;
            this.mensaje = mensaje;
            this.cliente = cliente;
            this.hwid = hwid;
        }
    }

    /**
     * Valida la existencia, firma digital y amarre de hardware del archivo de licencia predeterminado.
     */
    public static ResultadoValidacion validar() {
        return validar(ARCHIVO_LICENCIA);
    }

    /**
     * Valida la existencia, firma digital y amarre de hardware de un archivo de licencia específico.
     */
    public static ResultadoValidacion validar(String rutaArchivo) {
        File file = new File(rutaArchivo);
        if (!file.exists() || !file.isFile()) {
            return new ResultadoValidacion(
                    false,
                    "No se encontró el archivo de licencia '" + rutaArchivo + "' en la carpeta principal.",
                    null,
                    null
            );
        }

        try {
            String contenido = Files.readString(file.toPath(), StandardCharsets.UTF_8).trim();
            if (!contenido.contains(SEPARADOR_FIRMA)) {
                return new ResultadoValidacion(false, "El formato del archivo de licencia es inválido o está dañado.", null, null);
            }

            String[] partes = contenido.split(SEPARADOR_FIRMA);
            if (partes.length < 2) {
                return new ResultadoValidacion(false, "Falta la firma digital en el archivo de licencia.", null, null);
            }

            String payload = partes[0].trim();
            String firmaBase64 = partes[1].trim();

            // 1. Verificación criptográfica de la firma digital con la clave pública embebida
            if (!verificarFirma(payload, firmaBase64)) {
                return new ResultadoValidacion(false, "La firma digital no es auténtica o el contenido de la licencia fue modificado.", null, null);
            }

            // 2. Extracción de propiedades del payload
            Map<String, String> props = parsearPropiedades(payload);
            String hwidLicencia = props.get("hwid");
            String hwidsLicencia = props.get("hwids");
            String cliente = props.getOrDefault("cliente", "Desconocido");

            // Recopilar todos los HWIDs autorizados (soporta 'hwid' individual y 'hwids' múltiple separado por comas o punto y coma)
            java.util.Set<String> hwidsValidos = new java.util.HashSet<>();
            if (hwidLicencia != null && !hwidLicencia.isBlank()) {
                for (String h : hwidLicencia.split("[,;\\s]+")) {
                    if (!h.isBlank()) {
                        hwidsValidos.add(h.trim().toUpperCase());
                    }
                }
            }
            if (hwidsLicencia != null && !hwidsLicencia.isBlank()) {
                for (String h : hwidsLicencia.split("[,;\\s]+")) {
                    if (!h.isBlank()) {
                        hwidsValidos.add(h.trim().toUpperCase());
                    }
                }
            }

            if (hwidsValidos.isEmpty()) {
                return new ResultadoValidacion(false, "La licencia no contiene un Hardware ID válido.", cliente, null);
            }

            // 3. Verificación de amarre por Hardware ID
            String hwidActual = HardwareUtils.getMotherboardUUID();
            String hwidActualNorm = (hwidActual != null) ? hwidActual.trim().toUpperCase() : "";

            if (!hwidsValidos.contains(hwidActualNorm)) {
                return new ResultadoValidacion(
                        false,
                        "La licencia no corresponde a este equipo.\nHWID Actual: " + hwidActual + "\nEquipos autorizados: " + hwidsValidos.size(),
                        cliente,
                        String.join(", ", hwidsValidos)
                );
            }

            return new ResultadoValidacion(true, "Licencia activa y válida.", cliente, hwidActual);

        } catch (Exception e) {
            return new ResultadoValidacion(false, "Error al procesar la licencia: " + e.getMessage(), null, null);
        }
    }

    /**
     * Valida la licencia en el arranque del sistema.
     * Si no es válida, muestra el diálogo de activación y finaliza la aplicación con System.exit(0).
     */
    public static void validarLicenciaOExit() {
        ResultadoValidacion res = validar();
        if (res.valida) {
            return;
        }

        String hwidActual = HardwareUtils.getMotherboardUUID();
        mostrarDialogoBloqueo(res.mensaje, hwidActual);
        System.exit(0);
    }

    private static boolean verificarFirma(String payload, String firmaBase64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(PUBLIC_KEY_BASE64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(spec);

        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        // Normalizar saltos de línea a \n para consistencia multiplataforma
        String payloadNormalizado = payload.replace("\r\n", "\n").replace("\r", "\n");
        sig.update(payloadNormalizado.getBytes(StandardCharsets.UTF_8));

        // Limpiar posibles espacios o saltos en la firma Base64
        String firmaLimpia = firmaBase64.replaceAll("\\s+", "");
        byte[] signatureBytes = Base64.getDecoder().decode(firmaLimpia);
        return sig.verify(signatureBytes);
    }

    private static Map<String, String> parsearPropiedades(String payload) {
        Map<String, String> map = new HashMap<>();
        for (String linea : payload.split("\\r?\\n")) {
            int idx = linea.indexOf('=');
            if (idx > 0) {
                map.put(linea.substring(0, idx).trim().toLowerCase(), linea.substring(idx + 1).trim());
            }
        }
        return map;
    }

    private static void mostrarDialogoBloqueo(String mensajeError, String hwidActual) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblMsg = new JLabel("<html><b style='color:#c0392b; font-size:12px;'>⚠️ Activación de Licencia Requerida</b><br><br>"
                + mensajeError.replace("\n", "<br>") + "<br><br>"
                + "Para activar el sistema en este equipo, proporcione su <b>Hardware ID</b> al administrador:</html>");
        panel.add(lblMsg, BorderLayout.NORTH);

        JPanel panelHwid = new JPanel(new BorderLayout(8, 8));
        JTextField txtHwid = new JTextField(hwidActual);
        txtHwid.setEditable(false);
        txtHwid.setFont(new Font("Monospaced", Font.BOLD, 13));
        txtHwid.setBackground(new Color(245, 245, 245));

        JButton btnCopiar = new JButton("Copiar Hardware ID");
        btnCopiar.setFocusPainted(false);
        btnCopiar.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(hwidActual), null);
            JOptionPane.showMessageDialog(panel, "Hardware ID copiado al portapapeles con éxito.", "Copiado", JOptionPane.INFORMATION_MESSAGE);
        });

        panelHwid.add(txtHwid, BorderLayout.CENTER);
        panelHwid.add(btnCopiar, BorderLayout.EAST);
        panel.add(panelHwid, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(
                null,
                panel,
                "Sistema de Contaduría - Sin Licencia",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {
        String archivo = (args != null && args.length > 0) ? args[0] : ARCHIVO_LICENCIA;
        System.out.println("HWID detectado en Java: " + HardwareUtils.getMotherboardUUID());
        ResultadoValidacion res = validar(archivo);
        System.out.println("Archivo evaluado: " + archivo);
        System.out.println("Validación de licencia: " + (res.valida ? "VÁLIDA (Cliente: " + res.cliente + ")" : "INVÁLIDA (" + res.mensaje + ")"));
    }
}
