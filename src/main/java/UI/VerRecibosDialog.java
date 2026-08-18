package UI;

import controlador.Controlador;
import entidades.Cliente;
import entidades.Pago;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Calendar;
import java.util.List;

public class VerRecibosDialog extends JDialog {

    private final Controlador controlador;
    private final Cliente cliente;
    private JComboBox<Integer> comboAnio;
    private JTable tablaPagos;
    private DefaultTableModel modelPagos;
    private JLabel lblTotalPagado;
    private JLabel lblTotalDeuda;

    private static final String[] NOMBRES_MESES = {
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    };

    public VerRecibosDialog(Window parent, Controlador controlador, Cliente cliente) {
        super(parent, "Historial de Pagos y Deudas - " + cliente.nombre, ModalityType.APPLICATION_MODAL);
        this.controlador = controlador;
        this.cliente = cliente;
        initComponents();
        cargarDatos();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setMinimumSize(new Dimension(650, 480));
        setLayout(new BorderLayout(15, 15));

        // --- Panel Superior: Info del Cliente y Filtro de Año ---
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(new EmptyBorder(15, 15, 5, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 15);

        // Fila 0
        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Cliente:"), gbc);
        gbc.gridx = 1;
        JLabel lblNombre = new JLabel(cliente.nombre);
        lblNombre.setFont(lblNombre.getFont().deriveFont(Font.BOLD, 14f));
        topPanel.add(lblNombre, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("RFC:"), gbc);
        gbc.gridx = 3;
        JLabel lblRfc = new JLabel(cliente.rfc);
        lblRfc.setFont(lblRfc.getFont().deriveFont(Font.BOLD));
        topPanel.add(lblRfc, gbc);

        // Fila 1
        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("Honorario Mensual:"), gbc);
        gbc.gridx = 1;
        JLabel lblHonorarios = new JLabel("$" + cliente.honorarios);
        lblHonorarios.setFont(lblHonorarios.getFont().deriveFont(Font.BOLD));
        topPanel.add(lblHonorarios, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("Seleccionar Año:"), gbc);
        gbc.gridx = 3;
        comboAnio = new JComboBox<>();
        int anioActual = Calendar.getInstance().get(Calendar.YEAR);
        for (int a = anioActual - 3; a <= anioActual + 2; a++) {
            comboAnio.addItem(a);
        }
        comboAnio.setSelectedItem(anioActual);
        comboAnio.addActionListener(e -> cargarDatos());
        topPanel.add(comboAnio, gbc);

        add(topPanel, BorderLayout.NORTH);

        // --- Panel Central: Tabla ---
        modelPagos = new DefaultTableModel(new Object[][]{}, new String[]{"Mes", "Estado", "Monto", "Fecha de Pago"}) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tablaPagos = new JTable(modelPagos);
        tablaPagos.setRowHeight(25);

        // Renderizador de colores para la columna Estado
        tablaPagos.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    String val = value.toString();
                    if (val.contains("Pagado")) {
                        setForeground(new Color(46, 125, 50)); // Verde
                    } else if (val.contains("Pendiente")) {
                        setForeground(new Color(198, 40, 40)); // Rojo
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaPagos);
        scrollPane.setBorder(new EmptyBorder(5, 15, 5, 15));
        add(scrollPane, BorderLayout.CENTER);

        // --- Panel Inferior: Totales y Botón de Salida ---
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(new EmptyBorder(5, 15, 15, 15));

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        lblTotalPagado = new JLabel("Total Pagado en el Año: $0");
        lblTotalPagado.setFont(lblTotalPagado.getFont().deriveFont(Font.BOLD, 13f));
        lblTotalPagado.setForeground(new Color(46, 125, 50));
        summaryPanel.add(lblTotalPagado);

        lblTotalDeuda = new JLabel("Adeudo Total en el Año: $0");
        lblTotalDeuda.setFont(lblTotalDeuda.getFont().deriveFont(Font.BOLD, 13f));
        lblTotalDeuda.setForeground(new Color(198, 40, 40));
        summaryPanel.add(lblTotalDeuda);

        bottomPanel.add(summaryPanel, BorderLayout.WEST);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.putClientProperty("JButton.buttonType", "roundRect");
        btnCerrar.addActionListener(e -> dispose());
        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonWrapper.add(btnCerrar);
        bottomPanel.add(buttonWrapper, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        modelPagos.setRowCount(0);
        Integer selectedYear = (Integer) comboAnio.getSelectedItem();
        if (selectedYear == null) return;

        List<Pago> todosPagos = controlador.obtenerPagosPorCliente(cliente.id_persona);

        // Mapear pagos del año seleccionado por mes (1-12)
        Pago[] pagosDelAnio = new Pago[13]; // Índices 1 a 12
        for (Pago p : todosPagos) {
            if (p.anio == selectedYear && p.mes >= 1 && p.mes <= 12) {
                pagosDelAnio[p.mes] = p;
            }
        }

        int totalPagado = 0;
        int totalDeuda = 0;

        for (int mes = 1; mes <= 12; mes++) {
            Pago pago = pagosDelAnio[mes];
            String mesNombre = NOMBRES_MESES[mes - 1];
            String estado;
            String montoStr;
            String fechaPago;

            if (pago != null) {
                estado = "✓ Pagado";
                montoStr = "$" + pago.monto;
                fechaPago = pago.fecha_pago;
                totalPagado += pago.monto;
            } else {
                estado = "❌ Pendiente / Adeudo";
                montoStr = "$" + cliente.honorarios;
                fechaPago = "-";
                totalDeuda += cliente.honorarios;
            }

            modelPagos.addRow(new Object[]{mesNombre, estado, montoStr, fechaPago});
        }

        lblTotalPagado.setText("Total Pagado en el Año: $" + totalPagado);
        lblTotalDeuda.setText("Adeudo Total en el Año: $" + totalDeuda);
    }
}
