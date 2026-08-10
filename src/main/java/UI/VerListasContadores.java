package UI;

import controlador.Controlador;
import entidades.Cliente;
import entidades.Contadores;
import entidades.Declaracion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class VerListasContadores extends JDialog {

    private final Controlador controlador;
    private JComboBox<String> comboContadores;
    private JTable tablaClientes;
    private DefaultTableModel modelClientes;
    private ArrayList<Contadores> listaContadores;

    public VerListasContadores(Frame parent, boolean modal, Controlador controlador) {
        super(parent, modal);
        this.controlador = controlador;
        initComponents();
        cargarContadores();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setTitle("Listas de Clientes por Contador");
        setMinimumSize(new Dimension(700, 450));
        setLayout(new BorderLayout(15, 15));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.add(new JLabel("Seleccionar Contador:"));
        comboContadores = new JComboBox<>();
        comboContadores.setPreferredSize(new Dimension(250, 30));
        comboContadores.addActionListener(e -> cargarClientesContador());
        topPanel.add(comboContadores);
        add(topPanel, BorderLayout.NORTH);

        modelClientes = new DefaultTableModel(new Object[][]{}, new String[]{"ID Cliente", "Nombre", "RFC", "Honorarios"}) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tablaClientes = new JTable(modelClientes);
        tablaClientes.setRowHeight(25);
        // Ocultar ID Cliente
        tablaClientes.getColumnModel().getColumn(0).setMinWidth(0);
        tablaClientes.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaClientes.getColumnModel().getColumn(0).setPreferredWidth(0);

        JScrollPane scrollPane = new JScrollPane(tablaClientes);
        scrollPane.setBorder(new EmptyBorder(10, 15, 10, 15));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        JButton btnVerDeclaraciones = new JButton("Ver Declaraciones");
        btnVerDeclaraciones.putClientProperty("JButton.buttonType", "roundRect");
        btnVerDeclaraciones.addActionListener(e -> abrirHistorialDeclaraciones());
        bottomPanel.add(btnVerDeclaraciones);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void cargarContadores() {
        listaContadores = new ArrayList<>(controlador.getAllContadores().values());
        comboContadores.removeAllItems();
        for (Contadores c : listaContadores) {
            comboContadores.addItem(c.nombre);
        }
        if (!listaContadores.isEmpty()) {
            comboContadores.setSelectedIndex(0);
            cargarClientesContador();
        }
    }

    private void cargarClientesContador() {
        modelClientes.setRowCount(0);
        int idx = comboContadores.getSelectedIndex();
        if (idx == -1) return;

        Contadores selected = listaContadores.get(idx);
        ArrayList<Cliente> clientes = controlador.getClientesByContador(selected.getId());
        for (Cliente cli : clientes) {
            if (cli != null) {
                modelClientes.addRow(new Object[]{cli.id_persona, cli.nombre, cli.rfc, cli.honorarios});
            }
        }
    }

    private void abrirHistorialDeclaraciones() {
        int selectedRow = tablaClientes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente.");
            return;
        }

        int modelRow = tablaClientes.convertRowIndexToModel(selectedRow);
        int idCliente = (Integer) modelClientes.getValueAt(modelRow, 0);
        Cliente cliente = controlador.getClienteById(idCliente);

        if (cliente == null) return;

        JDialog diag = new JDialog(this, "Historial de Declaraciones - " + cliente.nombre, true);
        diag.setMinimumSize(new Dimension(600, 400));
        diag.setLayout(new BorderLayout(10, 10));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.add(new JLabel("Filtrar Mes:"));
        JComboBox<String> comboMes = new JComboBox<>(new String[]{
                "Todos", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        });
        filterPanel.add(comboMes);
        diag.add(filterPanel, BorderLayout.NORTH);

        DefaultTableModel modelDec = new DefaultTableModel(new Object[][]{}, new String[]{"Año", "Mes", "Gastos", "Ingresos", "Declarado"}) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tableDec = new JTable(modelDec);
        diag.add(new JScrollPane(tableDec), BorderLayout.CENTER);

        Runnable cargarTabla = () -> {
            modelDec.setRowCount(0);
            int selectedMonthIdx = comboMes.getSelectedIndex();
            for (Declaracion dec : controlador.declaraciones.values()) {
                if (dec.getIdCliente() == idCliente) {
                    if (selectedMonthIdx == 0 || dec.mes == selectedMonthIdx) {
                        modelDec.addRow(new Object[]{
                                dec.anio,
                                obtenerNombreMes(dec.mes),
                                dec.gastos == 1 ? "✓ Completado" : "❌ Pendiente",
                                dec.ingresos == 1 ? "✓ Completado" : "❌ Pendiente",
                                dec.declarado == 1 ? "✓ Presentada" : "❌ No Presentada"
                        });
                    }
                }
            }
        };

        comboMes.addActionListener(e -> cargarTabla.run());
        cargarTabla.run();

        diag.setLocationRelativeTo(this);
        diag.setVisible(true);
    }

    private String obtenerNombreMes(int m) {
        String[] meses = {"", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        if (m >= 1 && m <= 12) return meses[m];
        return String.valueOf(m);
    }
}
