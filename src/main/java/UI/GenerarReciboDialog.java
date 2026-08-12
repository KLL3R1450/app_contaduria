package UI;

import controlador.Controlador;
import entidades.Cliente;
import entidades.Pago;
import utils.GeneradorRecibo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class GenerarReciboDialog extends javax.swing.JDialog {
    private final Controlador controlador;
    private final Cliente cliente;

    // Componentes
    private JComboBox<Integer> comboAnio;
    private JPanel panelMeses;
    private JCheckBox[] checkMeses;
    private JLabel lblUltimoPago;
    private JLabel lblMontoBase;
    
    // Extras
    private JTextField txtConceptoExtra;
    private JTextField txtMontoExtra;
    private DefaultListModel<String> modelExtras;
    private JList<String> listExtras;
    private List<ExtraItem> listaExtras;
    private JButton btnAgregarExtra;
    private JButton btnEliminarExtra;
    
    // Totales y Acciones
    private JLabel lblTotal;
    private JButton btnGenerar;
    private JButton btnCancelar;

    private static class ExtraItem {
        String concepto;
        int monto;
        ExtraItem(String concepto, int monto) {
            this.concepto = concepto;
            this.monto = monto;
        }
        @Override
        public String toString() {
            return concepto + " - $" + monto;
        }
    }

    private static final String[] NOMBRES_MESES = {
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    };

    public GenerarReciboDialog(Window parent, Controlador controlador, Cliente cliente) {
        super(parent, "Generar Recibo - " + cliente.nombre, ModalityType.APPLICATION_MODAL);
        this.controlador = controlador;
        this.cliente = cliente;
        this.listaExtras = new ArrayList<>();
        
        initComponents();
        cargarEstadoPagos();
        actualizarTotal();
        
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel contentPane = new JPanel(new BorderLayout(15, 15));
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(contentPane);

        // --- Panel Superior: Información del Cliente ---
        JPanel panelInfo = new JPanel(new GridLayout(3, 2, 10, 5));
        panelInfo.setBorder(BorderFactory.createTitledBorder("Información del Cliente"));
        
        panelInfo.add(new JLabel("Cliente:"));
        JLabel lblNombre = new JLabel(cliente.nombre);
        lblNombre.setFont(lblNombre.getFont().deriveFont(Font.BOLD));
        panelInfo.add(lblNombre);

        panelInfo.add(new JLabel("Honorario Base Mensual:"));
        lblMontoBase = new JLabel("$" + cliente.honorarios);
        lblMontoBase.setFont(lblMontoBase.getFont().deriveFont(Font.BOLD));
        panelInfo.add(lblMontoBase);

        panelInfo.add(new JLabel("Último Periodo Pagado:"));
        lblUltimoPago = new JLabel("Cargando...");
        lblUltimoPago.setForeground(new Color(46, 125, 50));
        lblUltimoPago.setFont(lblUltimoPago.getFont().deriveFont(Font.BOLD));
        panelInfo.add(lblUltimoPago);

        contentPane.add(panelInfo, BorderLayout.NORTH);

        // --- Panel Central: Selección de Periodos y Conceptos Extras ---
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 15, 0));
        
        // 1. Selector de Meses
        JPanel panelPeriodos = new JPanel(new BorderLayout(10, 10));
        panelPeriodos.setBorder(BorderFactory.createTitledBorder("Periodos a Pagar"));

        JPanel panelAnio = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAnio.add(new JLabel("Año: "));
        comboAnio = new JComboBox<>();
        int anioActual = Calendar.getInstance().get(Calendar.YEAR);
        for (int a = anioActual - 2; a <= anioActual + 2; a++) {
            comboAnio.addItem(a);
        }
        comboAnio.setSelectedItem(anioActual);
        comboAnio.addActionListener(e -> cargarEstadoPagos());
        panelAnio.add(comboAnio);
        panelPeriodos.add(panelAnio, BorderLayout.NORTH);

        panelMeses = new JPanel(new GridLayout(6, 2, 10, 5));
        checkMeses = new JCheckBox[12];
        for (int i = 0; i < 12; i++) {
            checkMeses[i] = new JCheckBox(NOMBRES_MESES[i]);
            checkMeses[i].addActionListener(e -> actualizarTotal());
            panelMeses.add(checkMeses[i]);
        }
        panelPeriodos.add(panelMeses, BorderLayout.CENTER);
        panelCentral.add(panelPeriodos);

        // 2. Panel Extras
        JPanel panelExtras = new JPanel(new BorderLayout(10, 10));
        panelExtras.setBorder(BorderFactory.createTitledBorder("Conceptos Extras (Declaraciones, trámites, etc.)"));

        JPanel panelInputsExtra = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panelInputsExtra.add(new JLabel("Concepto:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtConceptoExtra = new JTextField();
        panelInputsExtra.add(txtConceptoExtra, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        panelInputsExtra.add(new JLabel("Monto $:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        txtMontoExtra = new JTextField();
        panelInputsExtra.add(txtMontoExtra, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST;
        btnAgregarExtra = new JButton("Agregar Extra");
        btnAgregarExtra.putClientProperty("JButton.buttonType", "roundRect");
        btnAgregarExtra.addActionListener(e -> agregarExtra());
        panelInputsExtra.add(btnAgregarExtra, gbc);

        panelExtras.add(panelInputsExtra, BorderLayout.NORTH);

        modelExtras = new DefaultListModel<>();
        listExtras = new JList<>(modelExtras);
        JScrollPane scrollExtras = new JScrollPane(listExtras);
        panelExtras.add(scrollExtras, BorderLayout.CENTER);

        btnEliminarExtra = new JButton("Eliminar Seleccionado");
        btnEliminarExtra.putClientProperty("JButton.buttonType", "roundRect");
        btnEliminarExtra.addActionListener(e -> eliminarExtra());
        panelExtras.add(btnEliminarExtra, BorderLayout.SOUTH);

        panelCentral.add(panelExtras);
        contentPane.add(panelCentral, BorderLayout.CENTER);

        // --- Panel Inferior: Totales y Acciones ---
        JPanel panelInferior = new JPanel(new BorderLayout());
        
        lblTotal = new JLabel("Total a Cobrar: $0");
        lblTotal.setFont(lblTotal.getFont().deriveFont(18f).deriveFont(Font.BOLD));
        lblTotal.setBorder(new EmptyBorder(10, 0, 10, 0));
        panelInferior.add(lblTotal, BorderLayout.WEST);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnCancelar = new JButton("Cancelar");
        btnCancelar.putClientProperty("JButton.buttonType", "roundRect");
        btnCancelar.addActionListener(e -> dispose());
        panelBotones.add(btnCancelar);

        btnGenerar = new JButton("Confirmar y Generar Recibo");
        btnGenerar.putClientProperty("JButton.buttonType", "roundRect");
        btnGenerar.addActionListener(e -> confirmarYGenerar());
        panelBotones.add(btnGenerar);

        panelInferior.add(panelBotones, BorderLayout.EAST);
        contentPane.add(panelInferior, BorderLayout.SOUTH);

        pack();
        setSize(750, 520);
    }

    private void cargarEstadoPagos() {
        int anioSeleccionado = (Integer) comboAnio.getSelectedItem();
        List<Pago> pagos = controlador.obtenerPagosPorCliente(cliente.id_persona);
        
        // Determinar el último periodo pagado
        if (pagos.isEmpty()) {
            lblUltimoPago.setText("Ninguno");
        } else {
            Pago ultimo = pagos.get(pagos.size() - 1);
            lblUltimoPago.setText(NOMBRES_MESES[ultimo.mes - 1] + " " + ultimo.anio);
        }

        // Marcar y deshabilitar los meses ya pagados
        Set<Integer> mesesPagados = new HashSet<>();
        for (Pago p : pagos) {
            if (p.anio == anioSeleccionado) {
                mesesPagados.add(p.mes);
            }
        }

        for (int i = 0; i < 12; i++) {
            int numeroMes = i + 1;
            if (mesesPagados.contains(numeroMes)) {
                checkMeses[i].setSelected(true);
                checkMeses[i].setEnabled(false);
                checkMeses[i].setText(NOMBRES_MESES[i] + " (Pagado)");
            } else {
                checkMeses[i].setSelected(false);
                checkMeses[i].setEnabled(true);
                checkMeses[i].setText(NOMBRES_MESES[i]);
            }
        }
        actualizarTotal();
    }

    private void agregarExtra() {
        String concepto = txtConceptoExtra.getText().trim();
        String montoStr = txtMontoExtra.getText().trim();
        if (concepto.isEmpty() || montoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe llenar el concepto y el monto.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int monto = Integer.parseInt(montoStr);
            if (monto <= 0) {
                JOptionPane.showMessageDialog(this, "El monto debe ser mayor a cero.", "Monto inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            ExtraItem item = new ExtraItem(concepto, monto);
            listaExtras.add(item);
            modelExtras.addElement(item.toString());
            txtConceptoExtra.setText("");
            txtMontoExtra.setText("");
            actualizarTotal();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El monto debe ser un valor numérico entero.", "Monto inválido", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminarExtra() {
        int index = listExtras.getSelectedIndex();
        if (index >= 0) {
            listaExtras.remove(index);
            modelExtras.remove(index);
            actualizarTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un elemento de la lista para eliminar.", "Sin selección", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizarTotal() {
        int total = 0;
        // Sumar meses seleccionados que no estaban ya pagados (los habilitados)
        for (int i = 0; i < 12; i++) {
            if (checkMeses[i].isSelected() && checkMeses[i].isEnabled()) {
                total += cliente.honorarios;
            }
        }
        // Sumar extras
        for (ExtraItem item : listaExtras) {
            total += item.monto;
        }
        lblTotal.setText("Total a Cobrar: $" + total);
    }

    private void confirmarYGenerar() {
        int anioSeleccionado = (Integer) comboAnio.getSelectedItem();
        List<Integer> mesesAPagar = new ArrayList<>();
        StringBuilder sbPeriodos = new StringBuilder();

        for (int i = 0; i < 12; i++) {
            if (checkMeses[i].isSelected() && checkMeses[i].isEnabled()) {
                mesesAPagar.add(i + 1);
                if (sbPeriodos.length() > 0) sbPeriodos.append(", ");
                sbPeriodos.append(NOMBRES_MESES[i]);
            }
        }

        if (mesesAPagar.isEmpty() && listaExtras.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar al menos un periodo o agregar un concepto extra para generar el recibo.", "Sin conceptos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (sbPeriodos.length() > 0) {
            sbPeriodos.append(" ").append(anioSeleccionado);
        }

        // Agregar conceptos extras a la cadena de periodos
        for (ExtraItem item : listaExtras) {
            if (sbPeriodos.length() > 0) sbPeriodos.append(" + ");
            sbPeriodos.append(item.concepto);
        }

        int totalMonto = 0;
        for (int i : mesesAPagar) {
            totalMonto += cliente.honorarios;
        }
        for (ExtraItem item : listaExtras) {
            totalMonto += item.monto;
        }

        // 23. Mostrar confirmación antes de proceder a guardar un recibo
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de registrar este pago y generar el recibo?\nTotal: $" + totalMonto,
                "Confirmar Pago",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            String fechaHoy = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

            // 1. Guardar registros en base de datos (uno por periodo)
            for (int mes : mesesAPagar) {
                Pago nuevoPago = new Pago(cliente.id_persona, anioSeleccionado, mes, cliente.honorarios, fechaHoy);
                controlador.registrarPago(nuevoPago);
            }

            // 2. Generar el PDF y abrirlo
            GeneradorRecibo.generarPDF(cliente.nombre, fechaHoy, sbPeriodos.toString(), totalMonto);

            JOptionPane.showMessageDialog(this, "Pago registrado y recibo generado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al procesar el pago: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
