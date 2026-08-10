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

            Declaracion dec = controlador.obtenerOCrearDeclaracion(idCliente, anio, mes);
            if (dec == null) {
                JOptionPane.showMessageDialog(this, "No se pudo sincronizar la declaración en la Base de Datos.");
                return;
            }

            String res = "";
            if (col == 2) {
                res = controlador.toggleGastos(dec.getIdDeclaracion(), valor);
            } else if (col == 3) {
                res = controlador.toggleIngresos(dec.getIdDeclaracion(), valor);
            } else if (col == 4) {
                res = controlador.toggleDeclarado(dec.getIdDeclaracion(), valor);
            }

            if (!"correcto".equals(res)) {
                JOptionPane.showMessageDialog(this, "Error al guardar el estado: " + res);
            }
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
        clientesActuales = controlador.getClientesByContador(selected.getId());
        int mes = comboMeses.getSelectedIndex() + 1;
        int anio = (Integer) comboAnio.getSelectedItem();

        for (Cliente cli : clientesActuales) {
            if (cli == null) continue;

            // Buscar si ya existe declaración para este mes/año
            boolean gastos = false;
            boolean ingresos = false;
            boolean declarado = false;

            for (Declaracion d : controlador.declaraciones.values()) {
                if (d.getIdCliente() == cli.id_persona && d.anio == anio && d.mes == mes) {
                    gastos = (d.gastos == 1);
                    ingresos = (d.ingresos == 1);
                    declarado = (d.declarado == 1);
                    break;
                }
            }

            modelDeclaraciones.addRow(new Object[]{
                    cli.id_persona,
                    cli.nombre,
                    gastos,
                    ingresos,
                    declarado
            });
        }
        cargando = false;
    }
}
