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

public class DeclaracionesContadores extends JDialog {

    private final Controlador controlador;
    private JComboBox<String> comboContadores;
    private JComboBox<String> comboMeses;
    private JComboBox<Integer> comboAnio;
    private JTable tablaDeclaraciones;
    private DefaultTableModel modelDeclaraciones;
    private ArrayList<Contadores> listaContadores;
    private ArrayList<Cliente> clientesActuales;
    private boolean cargando = false;

    public DeclaracionesContadores(Frame parent, boolean modal, Controlador controlador) {
        super(parent, modal);
        this.controlador = controlador;
        initComponents();
        cargarCombos();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setTitle("Control Mensual de Declaraciones");
        setMinimumSize(new Dimension(800, 500));
        setLayout(new BorderLayout(15, 15));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        
        topPanel.add(new JLabel("Contador:"));
        comboContadores = new JComboBox<>();
        comboContadores.setPreferredSize(new Dimension(200, 30));
        comboContadores.addActionListener(e -> refrescarTabla());
        topPanel.add(comboContadores);

        topPanel.add(new JLabel("Mes:"));
        comboMeses = new JComboBox<>(new String[]{
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        });
        comboMeses.setPreferredSize(new Dimension(120, 30));
        comboMeses.addActionListener(e -> refrescarTabla());
        topPanel.add(comboMeses);

        topPanel.add(new JLabel("Año:"));
        comboAnio = new JComboBox<>(new Integer[]{2024, 2025, 2026, 2027, 2028});
        comboAnio.setPreferredSize(new Dimension(100, 30));
        comboAnio.addActionListener(e -> refrescarTabla());
        topPanel.add(comboAnio);

        JButton btnRefrescar = new JButton("🔄 Refrescar");
        btnRefrescar.putClientProperty("JButton.buttonType", "roundRect");
        btnRefrescar.addActionListener(e -> refrescarTabla());
        topPanel.add(btnRefrescar);

        add(topPanel, BorderLayout.NORTH);

        modelDeclaraciones = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Cliente", "Nombre Cliente", "Gastos", "Ingresos", "Estado Declarado"}
        ) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2 || columnIndex == 3 || columnIndex == 4) {
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 2;
            }
        };

        tablaDeclaraciones = new JTable(modelDeclaraciones);
        tablaDeclaraciones.setRowHeight(30);
        // Ocultar ID
        tablaDeclaraciones.getColumnModel().getColumn(0).setMinWidth(0);
        tablaDeclaraciones.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaDeclaraciones.getColumnModel().getColumn(0).setPreferredWidth(0);

        // Detectar cambios en los checkboxes
        modelDeclaraciones.addTableModelListener(e -> {
            if (cargando) return;
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (row < 0 || col < 2) return;

            int idCliente = (Integer) modelDeclaraciones.getValueAt(row, 0);
            boolean valor = (Boolean) modelDeclaraciones.getValueAt(row, col);

            int mes = comboMeses.getSelectedIndex() + 1;
            int anio = (Integer) comboAnio.getSelectedItem();

            cargando = true;
            setEnabled(false);
            SwingWorker<String, Void> saveWorker = new SwingWorker<>() {
                @Override
                protected String doInBackground() throws Exception {
                    Declaracion dec = controlador.obtenerOCrearDeclaracion(idCliente, anio, mes);
                    if (dec == null) {
                        return "error_create";
                    }
                    if (col == 2) {
                        return controlador.toggleGastos(dec.getIdDeclaracion(), valor);
                    } else if (col == 3) {
                        return controlador.toggleIngresos(dec.getIdDeclaracion(), valor);
                    } else {
                        return controlador.toggleDeclarado(dec.getIdDeclaracion(), valor);
                    }
                }

                @Override
                protected void done() {
                    try {
                        String res = get();
                        if ("error_create".equals(res)) {
                            JOptionPane.showMessageDialog(DeclaracionesContadores.this, "No se pudo sincronizar la declaración en la Base de Datos.");
                            refrescarTabla();
                        } else if (!"correcto".equals(res)) {
                            JOptionPane.showMessageDialog(DeclaracionesContadores.this, "Error al guardar el estado: " + res);
                            refrescarTabla();
                        }
                    } catch(Exception ex) {
                        JOptionPane.showMessageDialog(DeclaracionesContadores.this, "Error de Red al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        refrescarTabla();
                    } finally {
                        cargando = false;
                        setEnabled(true);
                    }
                }
            };
            saveWorker.execute();
        });

        JScrollPane scrollPane = new JScrollPane(tablaDeclaraciones);
        scrollPane.setBorder(new EmptyBorder(10, 15, 10, 15));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void cargarCombos() {
        cargando = true;
        listaContadores = new ArrayList<>(controlador.getAllContadores().values());
        comboContadores.removeAllItems();
        for (Contadores c : listaContadores) {
            comboContadores.addItem(c.nombre);
        }
        
        java.util.Calendar cal = java.util.Calendar.getInstance();
        comboMeses.setSelectedIndex(cal.get(java.util.Calendar.MONTH));
        comboAnio.setSelectedItem(cal.get(java.util.Calendar.YEAR));

        cargando = false;
        if (!listaContadores.isEmpty()) {
            comboContadores.setSelectedIndex(0);
            refrescarTabla();
        }
    }

    private void refrescarTabla() {
        if (cargando) return;
        cargando = true;
        modelDeclaraciones.setRowCount(0);

        int idx = comboContadores.getSelectedIndex();
        if (idx == -1) {
            cargando = false;
            return;
        }

        Contadores selected = listaContadores.get(idx);
        int mes = comboMeses.getSelectedIndex() + 1;
        int anio = (Integer) comboAnio.getSelectedItem();

        setEnabled(false);
        SwingWorker<java.util.List<Object[]>, Void> worker = new SwingWorker<>() {
            @Override
            protected java.util.List<Object[]> doInBackground() throws Exception {
                return controlador.getDeclaracionesMensualesContador(selected.getId(), anio, mes);
            }

            @Override
            protected void done() {
                try {
                    java.util.List<Object[]> filas = get();
                    for (Object[] fila : filas) {
                        int idCliente = (Integer) fila[0];
                        String nombreCliente = (String) fila[1];
                        Integer idDeclaracion = (Integer) fila[2];
                        boolean gastos = ((Integer) fila[3] == 1);
                        boolean ingresos = ((Integer) fila[4] == 1);
                        boolean declarado = ((Integer) fila[5] == 1);

                        modelDeclaraciones.addRow(new Object[]{
                                idCliente,
                                nombreCliente,
                                gastos,
                                ingresos,
                                declarado
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DeclaracionesContadores.this, "Error al obtener declaraciones mensuales:\n" + ex.getMessage(), "Error de Red", JOptionPane.ERROR_MESSAGE);
                } finally {
                    cargando = false;
                    setEnabled(true);
                }
            }
        };
        worker.execute();
    }
}
