package UI;

import controlador.Controlador;
import entidades.Cliente;
import entidades.EFirmas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditarFirmaDialog extends JDialog {

    private final Controlador controlador;
    private final Cliente cliente;
    private final EFirmas firma;
    
    private JTextField txtFechaExpiracion;
    private JTextField txtFechaRenovacion;
    private JTextField txtRutaCertificado;
    private JTextField txtRutaKey;
    private JTextField txtRutaContrasena;

    public EditarFirmaDialog(Window parent, Controlador controlador, Cliente cliente) {
        super(parent, "Editar E-Firma: " + cliente.nombre, ModalityType.APPLICATION_MODAL);
        this.controlador = controlador;
        this.cliente = cliente;
        this.firma = controlador.getFirmaDe(cliente.id_persona);
        initComponents();
        setValues();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setMinimumSize(new Dimension(550, 350));
        setResizable(false);
        setLayout(new BorderLayout(15, 15));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fecha Expiracion
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Fecha Expiración (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtFechaExpiracion = new JTextField();
        txtFechaExpiracion.putClientProperty("JTextField.roundRect", true);
        formPanel.add(txtFechaExpiracion, gbc);

        // Fecha Renovacion
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Fecha Renovación (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtFechaRenovacion = new JTextField();
        txtFechaRenovacion.putClientProperty("JTextField.roundRect", true);
        formPanel.add(txtFechaRenovacion, gbc);

        // Ruta Certificado
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Certificado (.cer):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JPanel certPanel = new JPanel(new BorderLayout(5, 0));
        txtRutaCertificado = new JTextField();
        txtRutaCertificado.putClientProperty("JTextField.roundRect", true);
        certPanel.add(txtRutaCertificado, BorderLayout.CENTER);
        JButton btnCert = new JButton("Examinar...");
        btnCert.putClientProperty("JButton.buttonType", "roundRect");
        btnCert.addActionListener(e -> examinarArchivo(txtRutaCertificado, "Archivos Certificado (*.cer)", "cer"));
        certPanel.add(btnCert, BorderLayout.EAST);
        formPanel.add(certPanel, gbc);

        // Ruta Key
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Llave privada (.key):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JPanel keyPanel = new JPanel(new BorderLayout(5, 0));
        txtRutaKey = new JTextField();
        txtRutaKey.putClientProperty("JTextField.roundRect", true);
        keyPanel.add(txtRutaKey, BorderLayout.CENTER);
        JButton btnKey = new JButton("Examinar...");
        btnKey.putClientProperty("JButton.buttonType", "roundRect");
        btnKey.addActionListener(e -> examinarArchivo(txtRutaKey, "Archivos Key (*.key)", "key"));
        keyPanel.add(btnKey, BorderLayout.EAST);
        formPanel.add(keyPanel, gbc);

        // Ruta Contraseña (.txt)
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Contraseña (.txt):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JPanel passPanel = new JPanel(new BorderLayout(5, 0));
        txtRutaContrasena = new JTextField();
        txtRutaContrasena.putClientProperty("JTextField.roundRect", true);
        passPanel.add(txtRutaContrasena, BorderLayout.CENTER);
        JButton btnPass = new JButton("Examinar...");
        btnPass.putClientProperty("JButton.buttonType", "roundRect");
        btnPass.addActionListener(e -> examinarArchivo(txtRutaContrasena, "Archivos de texto (*.txt)", "txt"));
        passPanel.add(btnPass, BorderLayout.EAST);
        formPanel.add(passPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.putClientProperty("JButton.buttonType", "roundRect");
        btnGuardar.addActionListener(e -> guardar());

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.putClientProperty("JButton.buttonType", "roundRect");
        btnCancelar.addActionListener(e -> dispose());

        btnPanel.add(btnCancelar);
        btnPanel.add(btnGuardar);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void examinarArchivo(JTextField textField, String desc, String ext) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(desc, ext));
        int selection = fileChooser.showOpenDialog(this);
        if (selection == JFileChooser.APPROVE_OPTION) {
            textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void setValues() {
        if (firma != null) {
            txtFechaExpiracion.setText(firma.fecha_expiracion);
            txtFechaRenovacion.setText(firma.fecha_renovacion);
            txtRutaCertificado.setText(firma.ruta_certificado != null ? firma.ruta_certificado : "");
            txtRutaKey.setText(firma.ruta_key != null ? firma.ruta_key : "");
            txtRutaContrasena.setText(firma.contrasena != null ? firma.contrasena : "");
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String hoy = sdf.format(new Date());
            txtFechaExpiracion.setText(hoy);
            txtFechaRenovacion.setText(hoy);
            txtRutaCertificado.setText("");
            txtRutaKey.setText("");
            txtRutaContrasena.setText("");
        }
    }

    private void guardar() {
        String fExp = txtFechaExpiracion.getText().trim();
        String fRen = txtFechaRenovacion.getText().trim();
        String rCert = txtRutaCertificado.getText().trim();
        String rKey = txtRutaKey.getText().trim();
        String rPass = txtRutaContrasena.getText().trim();

        if (!validarFormato(fExp) || !validarFormato(fRen)) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Utilice AAAA-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String res = registrarONovacionFirma(fExp, fRen, rCert, rKey, rPass, cliente.id_persona);

        if ("correcto".equals(res)) {
            JOptionPane.showMessageDialog(this, "E-Firma guardada correctamente.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar la E-Firma: " + res, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarFormato(String fecha) {
        return fecha.matches("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$");
    }

    private String registrarONovacionFirma(String fExp, String fRen, String rCert, String rKey, String rPass, int idCliente) {
        String sqlCheck = "SELECT COUNT(*) FROM e_firmas WHERE id_cliente = ?";
        String sqlInsert = "INSERT INTO e_firmas(fecha_expiracion, fecha_renovacion, id_cliente, ruta_certificado, ruta_key, contrasena) VALUES (?, ?, ?, ?, ?, ?)";
        try (java.sql.Connection conn = persistencia.ConectorBD.getConexion()) {
            try (java.sql.PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
                psCheck.setInt(1, idCliente);
                try (java.sql.ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return controlador.renovarFirma(fExp, fRen, rCert, rKey, rPass, idCliente);
                    }
                }
            }
            try (java.sql.PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
                psInsert.setString(1, fExp);
                psInsert.setString(2, fRen);
                psInsert.setInt(3, idCliente);
                psInsert.setString(4, rCert);
                psInsert.setString(5, rKey);
                psInsert.setString(6, rPass);
                psInsert.executeUpdate();
                
                return "correcto";
            }
        } catch (java.sql.SQLException ex) {
            return ex.getMessage();
        }
    }
}
