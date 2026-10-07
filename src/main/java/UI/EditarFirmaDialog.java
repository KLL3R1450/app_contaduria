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
    
    private DatePickerField txtFechaExpiracion;
    private DatePickerField txtFechaRenovacion;
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
        setMinimumSize(new Dimension(580, 360));
        setResizable(false);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(AppTheme.BACKGROUND);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(AppTheme.BACKGROUND);
        formPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fecha Expiracion
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblExp = new JLabel("Fecha Expiración (DD/MM/YYYY):");
        lblExp.setForeground(AppTheme.TEXT_PRIMARY);
        formPanel.add(lblExp, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtFechaExpiracion = new DatePickerField();
        formPanel.add(txtFechaExpiracion, gbc);

        // Fecha Renovacion
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblRen = new JLabel("Fecha Renovación (DD/MM/YYYY):");
        lblRen.setForeground(AppTheme.TEXT_PRIMARY);
        formPanel.add(lblRen, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtFechaRenovacion = new DatePickerField();
        formPanel.add(txtFechaRenovacion, gbc);

        // Ruta Certificado
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel lblCert = new JLabel("Certificado (.cer):");
        lblCert.setForeground(AppTheme.TEXT_PRIMARY);
        formPanel.add(lblCert, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JPanel certPanel = new JPanel(new BorderLayout(5, 0));
        certPanel.setBackground(AppTheme.BACKGROUND);
        txtRutaCertificado = new JTextField();
        txtRutaCertificado.putClientProperty("JTextField.roundRect", true);
        certPanel.add(txtRutaCertificado, BorderLayout.CENTER);
        JButton btnCert = new JButton("Examinar...");
        AppTheme.styleSecondaryButton(btnCert);
        btnCert.addActionListener(e -> examinarArchivo(txtRutaCertificado, "Archivos Certificado (*.cer)", "cer"));
        certPanel.add(btnCert, BorderLayout.EAST);
        formPanel.add(certPanel, gbc);

        // Ruta Key
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        JLabel lblKey = new JLabel("Llave privada (.key):");
        lblKey.setForeground(AppTheme.TEXT_PRIMARY);
        formPanel.add(lblKey, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JPanel keyPanel = new JPanel(new BorderLayout(5, 0));
        keyPanel.setBackground(AppTheme.BACKGROUND);
        txtRutaKey = new JTextField();
        txtRutaKey.putClientProperty("JTextField.roundRect", true);
        keyPanel.add(txtRutaKey, BorderLayout.CENTER);
        JButton btnKey = new JButton("Examinar...");
        AppTheme.styleSecondaryButton(btnKey);
        btnKey.addActionListener(e -> examinarArchivo(txtRutaKey, "Archivos Key (*.key)", "key"));
        keyPanel.add(btnKey, BorderLayout.EAST);
        formPanel.add(keyPanel, gbc);

        // Ruta Contraseña (.txt)
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        JLabel lblPass = new JLabel("Contraseña (.txt):");
        lblPass.setForeground(AppTheme.TEXT_PRIMARY);
        formPanel.add(lblPass, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JPanel passPanel = new JPanel(new BorderLayout(5, 0));
        passPanel.setBackground(AppTheme.BACKGROUND);
        txtRutaContrasena = new JTextField();
        txtRutaContrasena.putClientProperty("JTextField.roundRect", true);
        passPanel.add(txtRutaContrasena, BorderLayout.CENTER);
        JButton btnPass = new JButton("Examinar...");
        AppTheme.styleSecondaryButton(btnPass);
        btnPass.addActionListener(e -> examinarArchivo(txtRutaContrasena, "Archivos de texto (*.txt)", "txt"));
        passPanel.add(btnPass, BorderLayout.EAST);
        formPanel.add(passPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        btnPanel.setBackground(AppTheme.PANEL_BACKGROUND);

        JButton btnCancelar = new JButton("Cancelar");
        AppTheme.styleSecondaryButton(btnCancelar);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = new JButton("Guardar");
        AppTheme.stylePrimaryButton(btnGuardar);
        btnGuardar.addActionListener(e -> guardar());

        btnPanel.add(btnCancelar);
        btnPanel.add(btnGuardar);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void examinarArchivo(JTextField textField, String desc, String ext) {
        JFileChooser fileChooser = new JFileChooser(utils.GestorCarpetas.getRutaFirmas());
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(desc, ext));
        int selection = fileChooser.showOpenDialog(this);
        if (selection == JFileChooser.APPROVE_OPTION) {
            textField.setText(utils.GestorCarpetas.aRutaRelativa(fileChooser.getSelectedFile()));
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
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
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
        String rCert = utils.GestorCarpetas.aRutaRelativa(txtRutaCertificado.getText().trim());
        String rKey = utils.GestorCarpetas.aRutaRelativa(txtRutaKey.getText().trim());
        String rPass = utils.GestorCarpetas.aRutaRelativa(txtRutaContrasena.getText().trim());

        if (!validarFormato(fExp) || !validarFormato(fRen)) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Utilice DD/MM/YYYY.", "Error", JOptionPane.ERROR_MESSAGE);
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
        return utils.Validator.validarFecha(fecha);
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
