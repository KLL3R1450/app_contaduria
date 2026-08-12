package utils;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import java.io.File;
import java.io.IOException;

public class GeneradorRecibo {
    public static void generarPDF(String clienteNombre, String fecha, String periodos, int totalMonto) {
        File template = new File("recibo.pdf");
        if (!template.exists()) {
            throw new RuntimeException("No se encontró la plantilla 'recibo.pdf' en la raíz del proyecto.");
        }

        try (PDDocument document = Loader.loadPDF(template)) {
            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
            if (acroForm != null) {
                // Los nombres de los campos del pdf del recibo son: fecha, cliente, periodos y monto
                setField(acroForm, "fecha", fecha);
                setField(acroForm, "cliente", clienteNombre);
                setField(acroForm, "periodos", periodos);
                setField(acroForm, "monto", "$" + totalMonto);
            }

            File outputDir = new File("recibos_generados");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            String safeName = clienteNombre.replaceAll("[^a-zA-Z0-9_-]", "_");
            File outputFile = new File(outputDir, "recibo_" + safeName + "_" + System.currentTimeMillis() + ".pdf");
            document.save(outputFile);
            
            // Abrir el archivo en el visor de PDF predeterminado del sistema
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(outputFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al procesar el archivo PDF: " + e.getMessage(), e);
        }
    }

    private static void setField(PDAcroForm acroForm, String fieldName, String value) throws IOException {
        PDField field = acroForm.getField(fieldName);
        if (field != null) {
            field.setValue(value);
        } else {
            System.out.println("Campo no encontrado en PDF: " + fieldName);
        }
    }
}
