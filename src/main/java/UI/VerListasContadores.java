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
    private JTextField txtFiltrarCliente;
    private JTable tablaClientes;
    private DefaultTableModel modelClientes;
    private ArrayList<Contadores> listaContadores;
    private boolean cargando = false;

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
        setMinimumSize(new Dimension(850, 450));
        setLayout(new BorderLayout(15, 15));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.add(new JLabel("Seleccionar Contador:"));
        comboContadores = new JComboBox<>();
        comboContadores.setPreferredSize(new Dimension(220, 30));
        comboContadores.addActionListener(e -> cargarClientesContador());
        topPanel.add(comboContadores);

        topPanel.add(new JLabel("Filtrar por Cliente:"));
        txtFiltrarCliente = new JTextField();
        txtFiltrarCliente.setPreferredSize(new Dimension(200, 30));
        txtFiltrarCliente.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { cargarClientesContador(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { cargarClientesContador(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { cargarClientesContador(); }
        });
        topPanel.add(txtFiltrarCliente);

        JButton btnRefrescar = new JButton("🔄 Refrescar");
        btnRefrescar.putClientProperty("JButton.buttonType", "roundRect");
        btnRefrescar.addActionListener(e -> cargarClientesContador());
        topPanel.add(btnRefrescar);

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
        cargando = true;
        listaContadores = new ArrayList<>(controlador.getAllContadores().values());
        comboContadores.removeAllItems();
        for (Contadores c : listaContadores) {
            comboContadores.addItem(c.nombre);
        }
        if (!listaContadores.isEmpty()) {
            comboContadores.setSelectedIndex(0);
        }
        cargando = false;
        cargarClientesContador();
    }

    private void cargarClientesContador() {
        if (cargando) return;
        int idx = comboContadores.getSelectedIndex();
        if (idx == -1) {
            modelClientes.setRowCount(0);
            return;
        }

        Contadores selected = listaContadores.get(idx);
        String filtro = txtFiltrarCliente.getText().trim().toLowerCase();

        setEnabled(false);
        SwingWorker<ArrayList<Cliente>, Void> worker = new SwingWorker<>() {
            @Override
            protected ArrayList<Cliente> doInBackground() throws Exception {
                return controlador.getClientesByContador(selected.getId());
            }

            @Override
            protected void done() {
                try {
                    ArrayList<Cliente> clientes = get();
                    modelClientes.setRowCount(0); // Limpiar justo antes de agregar en el EDT
                    for (Cliente cli : clientes) {
                        if (cli != null) {
                            if (filtro.isEmpty() || cli.nombre.toLowerCase().contains(filtro)) {
                                modelClientes.addRow(new Object[]{cli.id_persona, cli.nombre, cli.rfc, cli.honorarios});
                            }
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(VerListasContadores.this, "Error al obtener clientes del contador:\n" + ex.getMessage(), "Error de Red", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void abrirHistorialDeclaraciones() {
        int selectedRow = tablaClientes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente.");
            return;
        }

        int modelRow = tablaClientes.convertRowIndexToModel(selectedRow);
        int idCliente = (Integer) modelClientes.getValueAt(modelRow, 0);

        setEnabled(false);
        SwingWorker<Cliente, Void> workerCli = new SwingWorker<>() {
            @Override
            protected Cliente doInBackground() throws Exception {
                return controlador.getClienteById(idCliente);
            }

            @Override
            protected void done() {
                try {
                    Cliente cliente = get();
                    setEnabled(true);
                    if (cliente == null) {
                        return;
                    }

                    JDialog diag = new JDialog(VerListasContadores.this, "Historial de Declaraciones - " + cliente.nombre, true);
                    diag.setMinimumSize(new Dimension(600, 400));
                    diag.setLayout(new BorderLayout(10, 10));

                    JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
                    filterPanel.add(new JLabel("Filtrar Mes:"));
                    JComboBox<String> comboMes = new JComboBox<>(new String[]{
                            "Todos", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
                    });
                    filterPanel.add(comboMes);

                    filterPanel.add(new JLabel("Filtrar Año:"));
                    JComboBox<String> comboAnio = new JComboBox<>(new String[]{
                            "Todos", "2024", "2025", "2026", "2027", "2028"
                    });
                    filterPanel.add(comboAnio);

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
                        int selectedYearIdx = comboAnio.getSelectedIndex();
                        
                        diag.setEnabled(false);
                        SwingWorker<java.util.List<Declaracion>, Void> workerDec = new SwingWorker<>() {
                            @Override
                            protected java.util.List<Declaracion> doInBackground() throws Exception {
                                return controlador.getDeclaracionesPorCliente(idCliente);
                            }

                            @Override
                            protected void done() {
                                try {
                                    java.util.List<Declaracion> decs = get();
                                    for (Declaracion dec : decs) {
                                        boolean matchesMonth = (selectedMonthIdx == 0 || dec.mes == selectedMonthIdx);
                                        boolean matchesYear = (selectedYearIdx == 0 || dec.anio == Integer.parseInt((String) comboAnio.getSelectedItem()));
                                        if (matchesMonth && matchesYear) {
                                            modelDec.addRow(new Object[]{
                                                    dec.anio,
                                                    obtenerNombreMes(dec.mes),
                                                    dec.gastos == 1 ? "✓ Completado" : "❌ Pendiente",
                                                    dec.ingresos == 1 ? "✓ Completado" : "❌ Pendiente",
                                                    dec.declarado == 1 ? "✓ Presentada" : "❌ No Presentada"
                                            });
                                        }
                                    }
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(diag, "Error al cargar declaraciones:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                } finally {
                                    diag.setEnabled(true);
                                }
                            }
                        };
                        workerDec.execute();
                    };

                    comboMes.addActionListener(e -> cargarTabla.run());
                    comboAnio.addActionListener(e -> cargarTabla.run());
                    cargarTabla.run();

                    diag.setLocationRelativeTo(VerListasContadores.this);
                    diag.setVisible(true);
                } catch (Exception ex) {
                    setEnabled(true);
                    JOptionPane.showMessageDialog(VerListasContadores.this, "Error al obtener cliente:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        workerCli.execute();
    }

    private String obtenerNombreMes(int m) {
        String[] meses = {"", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        if (m >= 1 && m <= 12) return meses[m];
        return String.valueOf(m);
    }
}
