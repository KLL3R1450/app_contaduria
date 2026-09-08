package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Utilidad para la obtención e identificación única del hardware del equipo (HWID).
 * Consulta el UUID del producto del sistema / tarjeta madre en Windows.
 *
 * @author Osmar & Antigravity
 */
public class HardwareUtils {

    /**
     * Obtiene el UUID único de la placa madre/sistema del equipo.
     * Retorna una cadena limpia en mayúsculas estandarizada.
     */
    public static String getMotherboardUUID() {
        // 1. Intento principal: PowerShell Get-CimInstance
        String uuid = ejecutarComando(new String[]{
                "powershell", "-NoProfile", "-NonInteractive", "-Command",
                "(Get-CimInstance Win32_ComputerSystemProduct).UUID"
        });

        // 2. Fallback secundario: WMIC
        if (uuid == null || uuid.isBlank() || uuid.equalsIgnoreCase("none") || uuid.contains("UNKNOWN")) {
            String wmicOutput = ejecutarComando(new String[]{"wmic", "csproduct", "get", "uuid"});
            if (wmicOutput != null) {
                String[] lineas = wmicOutput.split("\\r?\\n");
                for (String l : lineas) {
                    String limpia = l.trim();
                    if (!limpia.isEmpty() && !limpia.equalsIgnoreCase("UUID")) {
                        uuid = limpia;
                        break;
                    }
                }
            }
        }

        // 3. Fallback terciario: MachineGuid del Registro de Windows
        if (uuid == null || uuid.isBlank() || uuid.equalsIgnoreCase("none")) {
            String regOutput = ejecutarComando(new String[]{
                    "reg", "query", "HKLM\\SOFTWARE\\Microsoft\\Cryptography", "/v", "MachineGuid"
            });
            if (regOutput != null && regOutput.contains("MachineGuid")) {
                String[] partes = regOutput.split("\\s+");
                if (partes.length >= 3) {
                    uuid = partes[partes.length - 1].trim();
                }
            }
        }

        if (uuid == null || uuid.isBlank()) {
            return "UNKNOWN-HWID-DEVICE";
        }

        // Sanitización final: mayúsculas, solo caracteres alfanuméricos y guiones
        return uuid.trim().toUpperCase().replaceAll("[^A-Z0-9-]", "");
    }

    private static String ejecutarComando(String[] comando) {
        try {
            Process process = new ProcessBuilder(comando)
                    .redirectErrorStream(true)
                    .start();

            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty()) {
                        sb.append(trimmed).append("\n");
                    }
                }
            }
            process.waitFor();
            String resultado = sb.toString().trim();
            return resultado.isEmpty() ? null : resultado;
        } catch (Exception e) {
            return null;
        }
    }
}
