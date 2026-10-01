package utils;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Generador de recibos de pago en PDF basado en plantillas interactivas AcroForm.
 * Soporta la plantilla oficial RECIBO_FINAL.pdf y recibo.pdf, auto-ajuste de tipografía
 * por longitud de texto y mapeo de los 14 campos estándar en mayúsculas.
 *
 * @author Osmar & Antigravity
 */
public class GeneradorRecibo {

    public static final String PLANTILLA_PREDETERMINADA = "RECIBO_FINAL.pdf";
    public static final String PLANTILLA_LEGACY = "recibo.pdf";

    public static void generarPDF(String clienteNombre, String fecha, String periodos, int totalMonto) {
        generarPDF(clienteNombre, fecha, periodos, totalMonto, "SR ANDRES CONTRERAS VEGA");
    }

    public static void generarPDF(String clienteNombre, String fecha, String periodos, int totalMonto, String personaAprobo) {
        File template = new File(PLANTILLA_PREDETERMINADA);
        if (!template.exists()) {
            template = new File(PLANTILLA_LEGACY);
        }
        if (!template.exists()) {
            throw new RuntimeException("No se encontró la plantilla de recibo ('" + PLANTILLA_PREDETERMINADA + "' o '" + PLANTILLA_LEGACY + "') en la raíz del proyecto.");
        }

        try (PDDocument document = Loader.loadPDF(template)) {
            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
            if (acroForm != null) {
                // Preparar valores normalizados estrictamente en MAYÚSCULAS
                String valFecha = (fecha != null ? fecha.trim() : "").toUpperCase(Locale.ROOT);
                String valMontoNum = (""+totalMonto).toUpperCase(Locale.ROOT);
                String valCliente = (clienteNombre != null ? clienteNombre.trim() : "").toUpperCase(Locale.ROOT);
                String valMontoLetra = NumeroALetras.convertir(totalMonto).toUpperCase(Locale.ROOT);
                String valPeriodos = (periodos != null ? periodos.trim() : "").toUpperCase(Locale.ROOT);
                String valAprobo = (personaAprobo != null ? personaAprobo.trim() : "SR ANDRES CONTRERAS VEGA").toUpperCase(Locale.ROOT);

                // 1. Fecha (1 y 2)
                setFieldAutoFit(acroForm, new String[]{"fecha"}, valFecha, 11.5f);
                setFieldAutoFit(acroForm, new String[]{"fecha_2", "fecha2"}, valFecha, 11.5f);

                // 2. Montos numéricos ($XXX)
                setFieldAutoFit(acroForm, new String[]{"monto1", "monto"}, valMontoNum, 14.0f);
                setFieldAutoFit(acroForm, new String[]{"monto2"}, valMontoNum, 13.0f);
                setFieldAutoFit(acroForm, new String[]{"monto1_2", "monto12"}, valMontoNum, 14.0f);
                setFieldAutoFit(acroForm, new String[]{"monto2_2", "monto22"}, valMontoNum, 13.0f);

                // 3. Cliente (1 y 2)
                setFieldAutoFit(acroForm, new String[]{"cliente"}, valCliente, 14.0f);
                setFieldAutoFit(acroForm, new String[]{"cliente_2", "cliente2"}, valCliente, 14.0f);

                // 4. Monto en letra (1 y 2)
                setFieldAutoFit(acroForm, new String[]{"monto_letra", "montoletra"}, valMontoLetra, 13.5f);
                setFieldAutoFit(acroForm, new String[]{"monto_letra_2", "monto_letra2", "montoletra2"}, valMontoLetra, 13.5f);

                // 5. Periodos y conceptos extras (1 y 2)
                setFieldAutoFit(acroForm, new String[]{"periodos"}, valPeriodos, 13.5f);
                setFieldAutoFit(acroForm, new String[]{"periodos_2", "periodos2"}, valPeriodos, 13.5f);

                // 6. Aprobador (1 y 2)
                setFieldAutoFit(acroForm, new String[]{"aprobo", "aprobo_1", "aprobo1"}, valAprobo, 14.0f);
                setFieldAutoFit(acroForm, new String[]{"aprobo_2", "aprobo2"}, valAprobo, 14.0f);
            }

            File outputDir = GestorCarpetas.getRutaRecibos();
            String safeName = GestorCarpetas.sanitizar(clienteNombre);
            File outputFile = new File(outputDir, "recibo_" + safeName + "_" + System.currentTimeMillis() + ".pdf");
            document.save(outputFile);

            // Abrir el archivo en el visor de PDF predeterminado del sistema
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(outputFile);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al procesar el archivo PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Asigna el valor a un campo AcroForm intentando múltiples nombres posibles y
     * ajusta dinámicamente el tamaño de la tipografía para asegurar que el texto
     * encaje perfectamente sin desbordarse.
     */
    private static void setFieldAutoFit(PDAcroForm acroForm, String[] possibleNames, String value, float defaultSize) throws IOException {
        PDField field = null;
        for (String name : possibleNames) {
            field = acroForm.getField(name);
            if (field != null) {
                break;
            }
        }

        if (field == null) {
            return;
        }

        if (field instanceof PDTextField) {
            PDTextField tf = (PDTextField) field;
            float targetSize = defaultSize;

            if (value != null && !value.isEmpty()) {
                for (PDAnnotationWidget w : tf.getWidgets()) {
                    PDRectangle rect = w.getRectangle();
                    if (rect != null) {
                        float boxWidth = rect.getWidth();
                        // Estimación de ancho promedio en mayúsculas (~0.58 * font size)
                        float estimatedTextWidth = value.length() * (defaultSize * 0.58f);
                        if (estimatedTextWidth > (boxWidth - 4)) {
                            targetSize = Math.max(6.0f, (boxWidth - 4) / (value.length() * 0.58f));
                        }
                    }
                }
            }

            String da = tf.getDefaultAppearance();
            if (da != null) {
                String[] parts = da.trim().split("\\s+");
                if (parts.length >= 3 && parts[2].equalsIgnoreCase("Tf")) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(parts[0]).append(" ").append(String.format(Locale.US, "%.1f", targetSize)).append(" Tf");
                    for (int i = 3; i < parts.length; i++) {
                        sb.append(" ").append(parts[i]);
                    }
                    tf.setDefaultAppearance(sb.toString());
                }
            }
            tf.setValue(value != null ? value : "");
        } else {
            field.setValue(value != null ? value : "");
        }
    }
}
