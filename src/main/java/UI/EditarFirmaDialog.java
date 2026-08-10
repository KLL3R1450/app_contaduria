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
        setMinimumSize(new Dimension(400, 250));
        setResizable(false);
        setLayout(new BorderLayout(15, 15));

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 15));
        formPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        formPanel.add(new JLabel("Fecha Expiración (YYYY-MM-DD):"));
        txtFechaExpiracion = new JTextField();
        txtFechaExpiracion.putClientProperty("JTextField.roundRect", true);
        formPanel.add(txtFechaExpiracion);

        formPanel.add(new JLabel("Fecha Renovación (YYYY-MM-DD):"));
        txtFechaRenovacion = new JTextField();
        txtFechaRenovacion.putClientProperty("JTextField.roundRect", true);
        formPanel.add(txtFechaRenovacion);

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

    private void setValues() {
        if (firma != null) {
            txtFechaExpiracion.setText(firma.fecha_expiracion);
            txtFechaRenovacion.setText(firma.fecha_renovacion);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String hoy = sdf.format(new Date());
            txtFechaExpiracion.setText(hoy);
            txtFechaRenovacion.setText(hoy);
        }
    }

    private void guardar() {
        String fExp = txtFechaExpiracion.getText().trim();
        String fRen = txtFechaRenovacion.getText().trim();

        if (!validarFormato(fExp) || !validarFormato(fRen)) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Utilice AAAA-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String res;
        if (firma == null) {
            // El DAO de e_firmas no tiene insertar firma directa, pero renovacion funciona si existe.
            // Para asegurar la lógica de negocio, usaremos el endpoint habitual de renovación.
            // Si la firma no existe en BD, insertamos un registro nuevo en SQLite mediante EFirmasDAO si tuviéramos método,
            // pero podemos ejecutar un INSERT directo en la base de datos o bien delegar. 
            // Vamos a verificar si se puede guardar con renovarFirma:
            res = registrarONovacionFirma(fExp, fRen, cliente.id_persona);
        } else {
            res = controlador.renovarFirma(fExp, fRen, cliente.id_persona);
        }

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

    private String registrarONovacionFirma(String fExp, String fRen, int idCliente) {
        // Ejecutar inserción directa de EFirma en SQLite ya que no existía antes
        String sqlCheck = "SELECT COUNT(*) FROM e_firmas WHERE id_cliente = ?";
        String sqlInsert = "INSERT INTO e_firmas(fecha_expiracion, fecha_renovacion, id_cliente) VALUES (?, ?, ?)";
        try (java.sql.Connection conn = persistencia.ConectorBD.getConexion()) {
            try (java.sql.PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
                psCheck.setInt(1, idCliente);
                try (java.sql.ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return controlador.renovarFirma(fExp, fRen, idCliente);
                    }
                }
            }
            try (java.sql.PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
                psInsert.setString(1, fExp);
                psInsert.setString(2, fRen);
                psInsert.setInt(3, idCliente);
                psInsert.executeUpdate();
                
                // Actualizar caché del controlador
                controlador.getAllFirmas().put(idCliente, new EFirmas(fExp, fRen, idCliente));
                return "correcto";
            }
        } catch (java.sql.SQLException ex) {
            return ex.getMessage();
        }
    }
}
