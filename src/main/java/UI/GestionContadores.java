package UI;

import controlador.Controlador;
import entidades.Cliente;
import entidades.Contadores;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * Modern dialog for Managing Contadores and their Client Portfolio
 * @author Osmar & Antigravity
 */
public class GestionContadores extends JDialog {

    private final Controlador controlador;
    
    // Contadores
    private JTable tablaContadores;
    private DefaultTableModel modelContadores;
    private JTextField txtBuscarContador;
    private ArrayList<Contadores> listaContadores;
    private Contadores contadorSeleccionado = null;

    // Clientes Asignados
    private JLabel lblTituloAsignados;
    private JTable tablaAsignados;
    private DefaultTableModel modelAsignados;
    private JTextField txtBuscarAsignados;
    private ArrayList<Cliente> listaAsignados;

    // Clientes Sin Contador
    private JTable tablaSinContador;
    private DefaultTableModel modelSinContador;
    private JTextField txtBuscarSinContador;
    private ArrayList<Cliente> listaSinContador;

    private JButton btnNuevoContador;
    private JButton btnEditarContador;
    private JButton btnBajaContador;
    private JButton btnQuitarCliente;
    private JButton btnAsignarCliente;

    public GestionContadores(Frame parent, boolean modal, Controlador controlador) {
        super(parent, modal);
        this.controlador = controlador;
        this.listaContadores = new ArrayList<>();
        this.listaAsignados = new ArrayList<>();
        this.listaSinContador = new ArrayList<>();
        
        initComponents();
        cargarContadores();
        cargarClientesSinContador();
        
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setTitle("Gestión de Contadores y Cartera de Clientes");
        setMinimumSize(new Dimension(1050, 650));
        setPreferredSize(new Dimension(1100, 700));
        setLayout(new BorderLayout(15, 15));

        // Header Superior
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(15, 20, 5, 20));
        
        JPanel textHeader = new JPanel(new GridLayout(2, 1, 0, 2));
        JLabel lblTitulo = new JLabel("Gestión de Contadores");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JLabel lblSub = new JLabel("Administra los contadores del despacho y asigna o libera clientes");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(UIManager.getColor("Label.disabledForeground"));
        textHeader.add(lblTitulo);
        textHeader.add(lblSub);
        
        JButton btnRefrescar = new JButton("Refrescar Todo");
        btnRefrescar.putClientProperty("JButton.buttonType", "roundRect");
        btnRefrescar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefrescar.addActionListener(e -> {
            cargarContadores();
            cargarClientesSinContador();
        });

        headerPanel.add(textHeader, BorderLayout.WEST);
        headerPanel.add(btnRefrescar, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Panel Principal Split (Izquierda: Contadores, Derecha: Clientes)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(380);
        splitPane.setBorder(new EmptyBorder(5, 15, 15, 15));

        // ================= PANEL IZQUIERDO: CONTADORES =================
        JPanel panelIzquierdo = new JPanel(new BorderLayout(0, 10));
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor"), 1, true),
                " Lista de Contadores ",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 13)
        ));

        // Barra de búsqueda de contador
        JPanel searchContaPanel = new JPanel(new BorderLayout(5, 5));
        searchContaPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        searchContaPanel.add(new JLabel("Filtrar:"), BorderLayout.WEST);
        txtBuscarContador = new JTextField();
        txtBuscarContador.putClientProperty("JTextField.placeholderText", "Buscar por nombre...");
        txtBuscarContador.putClientProperty("JTextField.roundRect", true);
        txtBuscarContador.getDocument().addDocumentListener(new SimpleDocumentListener(this::filtrarContadores));
        searchContaPanel.add(txtBuscarContador, BorderLayout.CENTER);
        panelIzquierdo.add(searchContaPanel, BorderLayout.NORTH);

        // Tabla de Contadores
        modelContadores = new DefaultTableModel(new Object[][]{}, new String[]{"ID", "Nombre del Contador"}) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaContadores = new JTable(modelContadores);
        tablaContadores.setRowHeight(28);
        tablaContadores.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaContadores.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        // Ocultar ID
        tablaContadores.getColumnModel().getColumn(0).setMinWidth(0);
        tablaContadores.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaContadores.getColumnModel().getColumn(0).setPreferredWidth(0);

        tablaContadores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onContadorSeleccionado();
            }
        });

        JScrollPane scrollContadores = new JScrollPane(tablaContadores);
        scrollContadores.setBorder(new EmptyBorder(5, 10, 5, 10));
        panelIzquierdo.add(scrollContadores, BorderLayout.CENTER);

        // Botones de acción para Contadores
        JPanel botonesConta = new JPanel(new GridLayout(1, 3, 6, 0));
        botonesConta.setBorder(new EmptyBorder(5, 10, 10, 10));

        btnNuevoContador = new JButton("Nuevo");
        btnNuevoContador.putClientProperty("JButton.buttonType", "roundRect");
        btnNuevoContador.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnNuevoContador.addActionListener(e -> accionNuevoContador());

        btnEditarContador = new JButton("Renombrar");
        btnEditarContador.putClientProperty("JButton.buttonType", "roundRect");
        btnEditarContador.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnEditarContador.setEnabled(false);
        btnEditarContador.addActionListener(e -> accionRenombrarContador());

        btnBajaContador = new JButton("Dar de Baja");
        btnBajaContador.putClientProperty("JButton.buttonType", "roundRect");
        btnBajaContador.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnBajaContador.setForeground(new Color(231, 76, 60));
        btnBajaContador.setEnabled(false);
        btnBajaContador.addActionListener(e -> accionDarDeBajaContador());

        botonesConta.add(btnNuevoContador);
        botonesConta.add(btnEditarContador);
        botonesConta.add(btnBajaContador);
        panelIzquierdo.add(botonesConta, BorderLayout.SOUTH);

        splitPane.setLeftComponent(panelIzquierdo);

        // ================= PANEL DERECHO: CLIENTES DEL CONTADOR =================
        JPanel panelDerecho = new JPanel(new GridLayout(2, 1, 0, 12));

        // 1. Sección Clientes Asignados
        JPanel panelAsignados = new JPanel(new BorderLayout(0, 8));
        panelAsignados.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor"), 1, true),
                " Clientes Asignados al Contador Seleccionado ",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 13)
        ));

        JPanel topAsignados = new JPanel(new BorderLayout(5, 5));
        topAsignados.setBorder(new EmptyBorder(5, 10, 5, 10));
        topAsignados.add(new JLabel("Filtrar:"), BorderLayout.WEST);
        txtBuscarAsignados = new JTextField();
        txtBuscarAsignados.putClientProperty("JTextField.placeholderText", "Buscar en asignados...");
        txtBuscarAsignados.putClientProperty("JTextField.roundRect", true);
        txtBuscarAsignados.getDocument().addDocumentListener(new SimpleDocumentListener(this::filtrarAsignados));
        topAsignados.add(txtBuscarAsignados, BorderLayout.CENTER);

        btnQuitarCliente = new JButton("➖ Quitar del Contador");
        btnQuitarCliente.putClientProperty("JButton.buttonType", "roundRect");
        btnQuitarCliente.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnQuitarCliente.setEnabled(false);
        btnQuitarCliente.addActionListener(e -> accionQuitarCliente());
        topAsignados.add(btnQuitarCliente, BorderLayout.EAST);
        panelAsignados.add(topAsignados, BorderLayout.NORTH);

        modelAsignados = new DefaultTableModel(new Object[][]{}, new String[]{"ID", "Nombre Cliente", "RFC", "Honorarios"}) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaAsignados = new JTable(modelAsignados);
        tablaAsignados.setRowHeight(25);
        tablaAsignados.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaAsignados.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaAsignados.getColumnModel().getColumn(0).setMinWidth(0);
        tablaAsignados.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaAsignados.getColumnModel().getColumn(0).setPreferredWidth(0);
        tablaAsignados.getSelectionModel().addListSelectionListener(e -> {
            btnQuitarCliente.setEnabled(tablaAsignados.getSelectedRow() != -1 && contadorSeleccionado != null);
        });

        JScrollPane scrollAsignados = new JScrollPane(tablaAsignados);
        scrollAsignados.setBorder(new EmptyBorder(0, 10, 10, 10));
        panelAsignados.add(scrollAsignados, BorderLayout.CENTER);

        // 2. Sección Clientes Sin Contador (Disponibles)
        JPanel panelDisponibles = new JPanel(new BorderLayout(0, 8));
        panelDisponibles.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor"), 1, true),
                " Clientes Disponibles (Sin Contador Asignado) ",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 13)
        ));

        JPanel topDisponibles = new JPanel(new BorderLayout(5, 5));
        topDisponibles.setBorder(new EmptyBorder(5, 10, 5, 10));
        topDisponibles.add(new JLabel("Filtrar:"), BorderLayout.WEST);
        txtBuscarSinContador = new JTextField();
        txtBuscarSinContador.putClientProperty("JTextField.placeholderText", "Buscar sin contador...");
        txtBuscarSinContador.putClientProperty("JTextField.roundRect", true);
        txtBuscarSinContador.getDocument().addDocumentListener(new SimpleDocumentListener(this::filtrarSinContador));
        topDisponibles.add(txtBuscarSinContador, BorderLayout.CENTER);

        btnAsignarCliente = new JButton("➕ Asignar a este Contador");
        btnAsignarCliente.putClientProperty("JButton.buttonType", "roundRect");
        btnAsignarCliente.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnAsignarCliente.setEnabled(false);
        btnAsignarCliente.addActionListener(e -> accionAsignarCliente());
        topDisponibles.add(btnAsignarCliente, BorderLayout.EAST);
        panelDisponibles.add(topDisponibles, BorderLayout.NORTH);

        modelSinContador = new DefaultTableModel(new Object[][]{}, new String[]{"ID", "Nombre Cliente", "RFC", "Honorarios"}) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaSinContador = new JTable(modelSinContador);
        tablaSinContador.setRowHeight(25);
        tablaSinContador.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaSinContador.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaSinContador.getColumnModel().getColumn(0).setMinWidth(0);
        tablaSinContador.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaSinContador.getColumnModel().getColumn(0).setPreferredWidth(0);
        tablaSinContador.getSelectionModel().addListSelectionListener(e -> {
            btnAsignarCliente.setEnabled(tablaSinContador.getSelectedRow() != -1 && contadorSeleccionado != null);
        });

        JScrollPane scrollDisponibles = new JScrollPane(tablaSinContador);
        scrollDisponibles.setBorder(new EmptyBorder(0, 10, 10, 10));
        panelDisponibles.add(scrollDisponibles, BorderLayout.CENTER);

        panelDerecho.add(panelAsignados);
        panelDerecho.add(panelDisponibles);

        splitPane.setRightComponent(panelDerecho);
        add(splitPane, BorderLayout.CENTER);
    }

    // ================= MÉTODOS DE CARGA Y FILTRADO =================

    private void cargarContadores() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<ArrayList<Contadores>, Void> worker = new SwingWorker<>() {
            @Override
            protected ArrayList<Contadores> doInBackground() {
                Map<Integer, Contadores> map = controlador.getAllContadores();
                return new ArrayList<>(map.values());
            }

            @Override
            protected void done() {
                try {
                    listaContadores = get();
                    filtrarContadores();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(GestionContadores.this, "Error al cargar contadores:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        worker.execute();
    }

    private void filtrarContadores() {
        String filtro = txtBuscarContador.getText().trim().toLowerCase();
        modelContadores.setRowCount(0);
        for (Contadores c : listaContadores) {
            if (filtro.isEmpty() || c.nombre.toLowerCase().contains(filtro)) {
                modelContadores.addRow(new Object[]{c.getId(), c.nombre});
            }
        }
        btnEditarContador.setEnabled(false);
        btnBajaContador.setEnabled(false);
        contadorSeleccionado = null;
        modelAsignados.setRowCount(0);
        btnQuitarCliente.setEnabled(false);
        btnAsignarCliente.setEnabled(false);
    }

    private void onContadorSeleccionado() {
        int row = tablaContadores.getSelectedRow();
        if (row == -1) {
            contadorSeleccionado = null;
            btnEditarContador.setEnabled(false);
            btnBajaContador.setEnabled(false);
            btnQuitarCliente.setEnabled(false);
            btnAsignarCliente.setEnabled(false);
            modelAsignados.setRowCount(0);
            return;
        }

        int id = (Integer) modelContadores.getValueAt(row, 0);
        for (Contadores c : listaContadores) {
            if (c.getId() == id) {
                contadorSeleccionado = c;
                break;
            }
        }

        if (contadorSeleccionado != null) {
            btnEditarContador.setEnabled(true);
            btnBajaContador.setEnabled(true);
            cargarClientesAsignados(contadorSeleccionado.getId());
            btnAsignarCliente.setEnabled(tablaSinContador.getSelectedRow() != -1);
        }
    }

    private void cargarClientesAsignados(int idContador) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<ArrayList<Cliente>, Void> worker = new SwingWorker<>() {
            @Override
            protected ArrayList<Cliente> doInBackground() {
                return controlador.getClientesByContador(idContador);
            }

            @Override
            protected void done() {
                try {
                    listaAsignados = get();
                    filtrarAsignados();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(GestionContadores.this, "Error al cargar clientes asignados:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        worker.execute();
    }

    private void filtrarAsignados() {
        String filtro = txtBuscarAsignados.getText().trim().toLowerCase();
        modelAsignados.setRowCount(0);
        for (Cliente c : listaAsignados) {
            if (filtro.isEmpty() || c.nombre.toLowerCase().contains(filtro) || c.rfc.toLowerCase().contains(filtro)) {
                modelAsignados.addRow(new Object[]{c.id_persona, c.nombre, c.rfc, "$" + c.honorarios});
            }
        }
        btnQuitarCliente.setEnabled(false);
    }

    private void cargarClientesSinContador() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<ArrayList<Cliente>, Void> worker = new SwingWorker<>() {
            @Override
            protected ArrayList<Cliente> doInBackground() {
                return controlador.getClientesSinContador();
            }

            @Override
            protected void done() {
                try {
                    listaSinContador = get();
                    filtrarSinContador();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(GestionContadores.this, "Error al cargar clientes sin contador:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        worker.execute();
    }

    private void filtrarSinContador() {
        String filtro = txtBuscarSinContador.getText().trim().toLowerCase();
        modelSinContador.setRowCount(0);
        for (Cliente c : listaSinContador) {
            if (filtro.isEmpty() || c.nombre.toLowerCase().contains(filtro) || c.rfc.toLowerCase().contains(filtro)) {
                modelSinContador.addRow(new Object[]{c.id_persona, c.nombre, c.rfc, "$" + c.honorarios});
            }
        }
        btnAsignarCliente.setEnabled(tablaSinContador.getSelectedRow() != -1 && contadorSeleccionado != null);
    }

    // ================= ACCIONES DE BOTONES =================

    private void accionNuevoContador() {
        String nombre = JOptionPane.showInputDialog(this, "Ingrese el nombre del nuevo contador:", "Nuevo Contador", JOptionPane.PLAIN_MESSAGE);
        if (nombre == null) return;
        nombre = nombre.trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del contador no puede estar vacío.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String finalNombre = nombre;
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        btnNuevoContador.setEnabled(false);
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                return controlador.insertContador(finalNombre);
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                btnNuevoContador.setEnabled(true);
                try {
                    String res = get();
                    if ("correcto".equals(res)) {
                        JOptionPane.showMessageDialog(GestionContadores.this, "Contador registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        cargarContadores();
                    } else {
                        JOptionPane.showMessageDialog(GestionContadores.this, "No se pudo registrar el contador:\n" + res, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(GestionContadores.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    GestionContadores.this.toFront();
                    GestionContadores.this.requestFocus();
                }
            }
        };
        worker.execute();
    }

    private void accionRenombrarContador() {
        if (contadorSeleccionado == null) return;
        String nuevoNombre = (String) JOptionPane.showInputDialog(
                this,
                "Modifique el nombre del contador:",
                "Renombrar Contador",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                contadorSeleccionado.nombre
        );
        if (nuevoNombre == null) return;
        nuevoNombre = nuevoNombre.trim();
        if (nuevoNombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String finalNombre = nuevoNombre;
        int idConta = contadorSeleccionado.getId();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        btnEditarContador.setEnabled(false);
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                return controlador.updateNombreContador(idConta, finalNombre);
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                btnEditarContador.setEnabled(true);
                try {
                    String res = get();
                    if ("correcto".equals(res)) {
                        JOptionPane.showMessageDialog(GestionContadores.this, "Nombre actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        cargarContadores();
                    } else {
                        JOptionPane.showMessageDialog(GestionContadores.this, "No se pudo actualizar el nombre:\n" + res, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(GestionContadores.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    GestionContadores.this.toFront();
                    GestionContadores.this.requestFocus();
                }
            }
        };
        worker.execute();
    }

    private void accionDarDeBajaContador() {
        if (contadorSeleccionado == null) return;
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de dar de baja al contador '" + contadorSeleccionado.nombre + "'?\n\n" +
                "⚠️ Todos los clientes asociados a este contador serán liberados y quedarán\n" +
                "en la lista 'Sin Contador' para ser reasignados.",
                "Confirmar Baja de Contador",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        int idConta = contadorSeleccionado.getId();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        btnBajaContador.setEnabled(false);
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                return controlador.deleteContador(idConta);
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                btnBajaContador.setEnabled(true);
                try {
                    String res = get();
                    if ("correcto".equals(res)) {
                        JOptionPane.showMessageDialog(GestionContadores.this, "Contador dado de baja y clientes liberados correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        cargarContadores();
                        cargarClientesSinContador();
                    } else {
                        JOptionPane.showMessageDialog(GestionContadores.this, "No se pudo dar de baja el contador:\n" + res, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(GestionContadores.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    GestionContadores.this.toFront();
                    GestionContadores.this.requestFocus();
                }
            }
        };
        worker.execute();
    }

    private void accionQuitarCliente() {
        if (contadorSeleccionado == null) return;
        int row = tablaAsignados.getSelectedRow();
        if (row == -1) return;

        int idCliente = (Integer) modelAsignados.getValueAt(row, 0);
        String nombreCliente = (String) modelAsignados.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Desea remover a '" + nombreCliente + "' de la cartera del contador '" + contadorSeleccionado.nombre + "'?\n" +
                "El cliente quedará libre (Sin Contador).",
                "Quitar Cliente de Cartera",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        btnQuitarCliente.setEnabled(false);
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                return controlador.desasignarCliente(idCliente);
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                btnQuitarCliente.setEnabled(true);
                try {
                    String res = get();
                    if ("correcto".equals(res)) {
                        cargarClientesAsignados(contadorSeleccionado.getId());
                        cargarClientesSinContador();
                    } else {
                        JOptionPane.showMessageDialog(GestionContadores.this, "Error al remover cliente:\n" + res, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(GestionContadores.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    GestionContadores.this.toFront();
                    GestionContadores.this.requestFocus();
                }
            }
        };
        worker.execute();
    }

    private void accionAsignarCliente() {
        if (contadorSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un contador primero en la tabla izquierda.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = tablaSinContador.getSelectedRow();
        if (row == -1) return;

        int idCliente = (Integer) modelSinContador.getValueAt(row, 0);
        int idConta = contadorSeleccionado.getId();

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        btnAsignarCliente.setEnabled(false);
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                return controlador.asignarClienteAContador(idCliente, idConta);
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                btnAsignarCliente.setEnabled(true);
                try {
                    String res = get();
                    if ("correcto".equals(res)) {
                        cargarClientesAsignados(idConta);
                        cargarClientesSinContador();
                    } else {
                        JOptionPane.showMessageDialog(GestionContadores.this, "Error al asignar cliente:\n" + res, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(GestionContadores.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    GestionContadores.this.toFront();
                    GestionContadores.this.requestFocus();
                }
            }
        };
        worker.execute();
    }

    // Listener auxiliar para cambios en texto
    private static class SimpleDocumentListener implements DocumentListener {
        private final Runnable callback;
        public SimpleDocumentListener(Runnable callback) { this.callback = callback; }
        @Override public void insertUpdate(DocumentEvent e) { callback.run(); }
        @Override public void removeUpdate(DocumentEvent e) { callback.run(); }
        @Override public void changedUpdate(DocumentEvent e) { callback.run(); }
    }
}
