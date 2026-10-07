package UI;

import controlador.Controlador;
import entidades.Cliente;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import utils.ExeLauncher;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Modern Dashboard Principal utilizing FlatLaf y Paleta Corporativa
 * @author Osmar & Antigravity
 */
public class Index extends javax.swing.JFrame {

    private static Controlador c;
    private boolean isDarkMode = false;

    private JLabel lblTotalClientes;
    private JLabel lblTotalTerceros;
    private JLabel lblTotalContadores;
    private JTable tblFirmas;
    private DefaultTableModel tableModel;

    // Control de Paginación para Semáforo de E-Firmas
    private int paginaActual = 0;
    private static final int TAMANO_PAGINA = 15;
    private int totalFirmas = 0;
    private JButton btnVerMenos;
    private JButton btnVerMas;
    private JLabel lblPaginacion;

    public Index(Controlador controler) {
        c = controler;
        initComponentsCustom();
        cargarDatosDashboard();
    }

    private void initComponentsCustom() {
        setTitle("Despacho Contable - Dashboard");
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(980, 640));
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                confirmarCierre();
            }
        });

        // Contenedor principal con BorderLayout
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(AppTheme.BACKGROUND);
        setContentPane(mainContainer);

        // ================= SIDEBAR (Panel Lateral Izquierdo) =================
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(240, 600));
        sidebar.setBorder(new EmptyBorder(25, 20, 25, 20));
        sidebar.setBackground(AppTheme.PANEL_BACKGROUND);

        // Título Logo
        JLabel lblLogo = new JLabel("CONTABILIDAD");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setForeground(AppTheme.PRIMARY);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubLogo = new JLabel("Dashboard Principal");
        lblSubLogo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubLogo.setForeground(AppTheme.TEXT_SECONDARY);
        lblSubLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(lblLogo);
        sidebar.add(lblSubLogo);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton btnClientes = crearBotonSidebar(" Clientes");
        btnClientes.addActionListener(e -> abrirBuscarClientes());
        sidebar.add(btnClientes);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton btnTerceros = crearBotonSidebar(" Terceros");
        btnTerceros.addActionListener(e -> abrirBuscarTerceros());
        sidebar.add(btnTerceros);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton btnFirmas = crearBotonSidebar(" E-Firmas");
        btnFirmas.addActionListener(e -> abrirBuscarFirmas());
        sidebar.add(btnFirmas);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton btnContadores = crearBotonSidebar(" Contadores");
        btnContadores.addActionListener(e -> abrirGestionContadores());
        sidebar.add(btnContadores);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton btnListasContadores = crearBotonSidebar(" Listas Contadores");
        btnListasContadores.addActionListener(e -> abrirListasContadores());
        sidebar.add(btnListasContadores);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton btnDeclaraciones = crearBotonSidebar(" Declaraciones");
        btnDeclaraciones.addActionListener(e -> abrirDeclaracionesContadores());
        sidebar.add(btnDeclaraciones);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton btnDescargaMasiva = crearBotonSidebar(" Descarga Masiva");
        btnDescargaMasiva.addActionListener(e -> ExeLauncher.lanzarDescargaMasiva(this));
        sidebar.add(btnDescargaMasiva);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton btnLectorXml = crearBotonSidebar(" Lector XML");
        btnLectorXml.addActionListener(e -> ExeLauncher.lanzarLectorXml(this));
        sidebar.add(btnLectorXml);

        // Separador flexible hacia el fondo
        sidebar.add(Box.createVerticalGlue());

        // Botón de cambio de tema
        JButton btnTema = new JButton("Cambiar Tema");
        btnTema.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnTema.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTema.putClientProperty("JButton.buttonType", "roundRect");
        btnTema.addActionListener(e -> alternarTema());
        sidebar.add(btnTema);

        mainContainer.add(sidebar, BorderLayout.WEST);

        // ================= PANEL DE CONTENIDO (Derecha) =================
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(AppTheme.BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header superior
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(AppTheme.BACKGROUND);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel textHeader = new JPanel(new BorderLayout());
        textHeader.setBackground(AppTheme.BACKGROUND);
        JLabel lblHeaderTitle = new JLabel("Bienvenido de nuevo");
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHeaderTitle.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel lblHeaderSub = new JLabel("Resumen general del estado de tus clientes y e-firmas");
        lblHeaderSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblHeaderSub.setForeground(AppTheme.TEXT_SECONDARY);
        textHeader.add(lblHeaderTitle, BorderLayout.NORTH);
        textHeader.add(lblHeaderSub, BorderLayout.SOUTH);

        JButton btnRefrescar = new JButton("🔄 Refrescar");
        AppTheme.stylePrimaryButton(btnRefrescar);
        btnRefrescar.addActionListener(e -> cargarDatosDashboard());

        headerPanel.add(textHeader, BorderLayout.WEST);
        headerPanel.add(btnRefrescar, BorderLayout.EAST);
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        // Área Central: Métrica + Tabla
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(AppTheme.BACKGROUND);

        // 1. Fila de Tarjetas de Métricas (KPI Cards)
        JPanel cardsContainer = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsContainer.setBackground(AppTheme.BACKGROUND);

        JPanel cardClientes = crearCardMetrica("Total Clientes", "0", AppTheme.PRIMARY);
        lblTotalClientes = (JLabel) cardClientes.getClientProperty("valLabel");

        JPanel cardTerceros = crearCardMetrica("Total Terceros", "0", new Color(39, 174, 96));
        lblTotalTerceros = (JLabel) cardTerceros.getClientProperty("valLabel");

        JPanel cardContadores = crearCardMetrica("Contadores Activos", "0", new Color(142, 68, 173));
        lblTotalContadores = (JLabel) cardContadores.getClientProperty("valLabel");

        cardsContainer.add(cardClientes);
        cardsContainer.add(cardTerceros);
        cardsContainer.add(cardContadores);
        centerPanel.add(cardsContainer, BorderLayout.NORTH);

        // 2. Tabla de E-Firmas por vencer con Paginación
        JPanel tableSection = new JPanel(new BorderLayout());
        tableSection.setBackground(AppTheme.SURFACE);
        tableSection.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1, true),
                " Semáforo de E-Firmas (Ordenado por Vencimiento Más Cercano) ",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                AppTheme.TEXT_PRIMARY
        ));

        tableModel = new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID Cliente", "Cliente", "RFC", "Fecha Expiración", "Estado"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblFirmas = new JTable(tableModel);
        tblFirmas.setRowHeight(30);
        tblFirmas.setShowHorizontalLines(true);
        tblFirmas.setGridColor(AppTheme.BORDER);
        tblFirmas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblFirmas.getTableHeader().setBackground(AppTheme.PANEL_BACKGROUND);
        tblFirmas.getTableHeader().setForeground(AppTheme.TEXT_PRIMARY);
        tblFirmas.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Ocultar ID Cliente
        tblFirmas.getColumnModel().getColumn(0).setMinWidth(0);
        tblFirmas.getColumnModel().getColumn(0).setMaxWidth(0);
        tblFirmas.getColumnModel().getColumn(0).setPreferredWidth(0);

        // Renderizador de Semáforo
        tblFirmas.getColumnModel().getColumn(4).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cComp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    String val = value.toString();
                    if (val.contains("Vigente")) {
                        cComp.setBackground(AppTheme.COLOR_VIGENTE_BG);
                        cComp.setForeground(AppTheme.TEXT_PRIMARY);
                    } else if (val.contains("Próximo")) {
                        cComp.setBackground(AppTheme.COLOR_PROXIMO_BG);
                        cComp.setForeground(AppTheme.TEXT_PRIMARY);
                    } else if (val.contains("Vencido")) {
                        cComp.setBackground(AppTheme.COLOR_VENCIDO_BG);
                        cComp.setForeground(AppTheme.TEXT_PRIMARY);
                    } else {
                        cComp.setBackground(AppTheme.SURFACE);
                        cComp.setForeground(AppTheme.TEXT_PRIMARY);
                    }
                }
                if (isSelected) {
                    cComp.setBackground(AppTheme.PRIMARY);
                    cComp.setForeground(AppTheme.SURFACE);
                }
                return cComp;
            }
        });

        // Doble click para editar firma directamente
        tblFirmas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tblFirmas.getSelectedRow();
                    if (row != -1) {
                        int modelRow = tblFirmas.convertRowIndexToModel(row);
                        int idCliente = (Integer) tblFirmas.getModel().getValueAt(modelRow, 0);
                        Cliente cli = c.getClienteById(idCliente);
                        if (cli != null) {
                            new EditarFirmaDialog(Index.this, c, cli).setVisible(true);
                            cargarFirmasPaginadas();
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblFirmas);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getViewport().setBackground(AppTheme.SURFACE);
        tableSection.add(scrollPane, BorderLayout.CENTER);

        // ================= BARRA DE PAGINACIÓN (15 en 15) =================
        JPanel paginationPanel = new JPanel(new BorderLayout(10, 0));
        paginationPanel.setBackground(AppTheme.PANEL_BACKGROUND);
        paginationPanel.setBorder(new EmptyBorder(8, 15, 8, 15));

        btnVerMenos = new JButton("⬅ Ver menos");
        AppTheme.styleSecondaryButton(btnVerMenos);
        btnVerMenos.addActionListener(e -> {
            if (paginaActual > 0) {
                paginaActual--;
                cargarFirmasPaginadas();
            }
        });

        lblPaginacion = new JLabel("Cargando...", SwingConstants.CENTER);
        lblPaginacion.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPaginacion.setForeground(AppTheme.TEXT_PRIMARY);

        btnVerMas = new JButton("Ver más ➡");
        AppTheme.styleSecondaryButton(btnVerMas);
        btnVerMas.addActionListener(e -> {
            if ((paginaActual + 1) * TAMANO_PAGINA < totalFirmas) {
                paginaActual++;
                cargarFirmasPaginadas();
            }
        });

        paginationPanel.add(btnVerMenos, BorderLayout.WEST);
        paginationPanel.add(lblPaginacion, BorderLayout.CENTER);
        paginationPanel.add(btnVerMas, BorderLayout.EAST);

        tableSection.add(paginationPanel, BorderLayout.SOUTH);

        centerPanel.add(tableSection, BorderLayout.CENTER);
        contentPanel.add(centerPanel, BorderLayout.CENTER);

        mainContainer.add(contentPanel, BorderLayout.CENTER);
        pack();
    }

    private JButton crearBotonSidebar(String texto) {
        JButton btn = new JButton(texto);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setBackground(AppTheme.SURFACE);
        btn.setForeground(AppTheme.TEXT_PRIMARY);
        btn.setBorder(new LineBorder(AppTheme.BORDER, 1, true));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel crearCardMetrica(String titulo, String valor, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(AppTheme.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER, 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel(titulo);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(AppTheme.TEXT_SECONDARY);

        JLabel lblVal = new JLabel(valor);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblVal.setForeground(accentColor);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblVal, BorderLayout.CENTER);

        card.putClientProperty("valLabel", lblVal);
        return card;
    }

    private void cargarDatosDashboard() {
        if (c == null) return;

        setEnabled(false);
        paginaActual = 0;

        SwingWorker<int[], Void> worker = new SwingWorker<>() {
            @Override
            protected int[] doInBackground() {
                int totalClientes = c.getClientesLigeros().size();
                int totalTerceros = c.getTercerosLigeros().size();
                int totalContadores = c.getAllContadores().size();
                return new int[]{totalClientes, totalTerceros, totalContadores};
            }

            @Override
            protected void done() {
                try {
                    int[] totals = get();
                    lblTotalClientes.setText(String.valueOf(totals[0]));
                    lblTotalTerceros.setText(String.valueOf(totals[1]));
                    lblTotalContadores.setText(String.valueOf(totals[2]));
                    cargarFirmasPaginadas();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(Index.this,
                        "Error al conectar con la base de datos:\n" + ex.getMessage(),
                        "Error de Red",
                        JOptionPane.ERROR_MESSAGE);
                } finally {
                    setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void cargarFirmasPaginadas() {
        if (c == null) return;

        btnVerMenos.setEnabled(false);
        btnVerMas.setEnabled(false);
        lblPaginacion.setText("Cargando firmas...");

        SwingWorker<List<Object[]>, Void> worker = new SwingWorker<>() {
            private int conteoTotal = 0;

            @Override
            protected List<Object[]> doInBackground() {
                conteoTotal = c.contarTotalSemaforo();
                return c.obtenerSemaforoPaginado(TAMANO_PAGINA, paginaActual * TAMANO_PAGINA);
            }

            @Override
            protected void done() {
                try {
                    totalFirmas = conteoTotal;
                    List<Object[]> firmas = get();

                    tableModel.setRowCount(0);
                    for (Object[] f : firmas) {
                        int idCliente = (Integer) f[0];
                        String nombreCliente = (String) f[1];
                        String rfcCliente = (String) f[2];
                        String fechaExp = (String) f[3];
                        String estadoAlerta = (String) f[4];
                        int diasRestantes = (Integer) f[5];

                        String estado = "Sin Datos";
                        if ("VIGENTE".equals(estadoAlerta)) {
                            estado = "Vigente (" + (diasRestantes / 365) + " año/s)";
                        } else if ("PROXIMA".equals(estadoAlerta)) {
                            estado = "Próximo a vencer (" + (diasRestantes / 30) + " mes/es)";
                        } else if ("URGENTE".equals(estadoAlerta) || "VENCIDA".equals(estadoAlerta)) {
                            estado = "Vencido / < 1 mes (" + diasRestantes + " días)";
                        }

                        tableModel.addRow(new Object[]{
                            idCliente,
                            nombreCliente,
                            rfcCliente,
                            fechaExp,
                            estado
                        });
                    }

                    int totalPaginas = (int) Math.ceil((double) totalFirmas / TAMANO_PAGINA);
                    if (totalPaginas == 0) totalPaginas = 1;

                    int desde = totalFirmas == 0 ? 0 : (paginaActual * TAMANO_PAGINA) + 1;
                    int hasta = Math.min((paginaActual + 1) * TAMANO_PAGINA, totalFirmas);

                    lblPaginacion.setText("Página " + (paginaActual + 1) + " de " + totalPaginas +
                                          "  •  Mostrando " + desde + "-" + hasta + " de " + totalFirmas + " firmas");

                    btnVerMenos.setEnabled(paginaActual > 0);
                    btnVerMas.setEnabled((paginaActual + 1) < totalPaginas);

                } catch (Exception ex) {
                    lblPaginacion.setText("Error al cargar firmas.");
                    System.err.println("Error al cargar semáforo paginado: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void alternarTema() {
        FlatAnimatedLafChange.showSnapshot();
        try {
            if (!isDarkMode) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
                isDarkMode = true;
            } else {
                AppTheme.setupTheme();
                isDarkMode = false;
            }
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            System.err.println("Error al alternar tema: " + ex.getMessage());
        }
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    private void abrirAgregarCliente() {
        AñadirCliente ac = new AñadirCliente(this, rootPaneCheckingEnabled, c);
        ac.setVisible(true);
        cargarDatosDashboard();
    }

    private void abrirAgregarTercero() {
        añadirTercero at = new añadirTercero(this, rootPaneCheckingEnabled, c);
        at.setVisible(true);
        cargarDatosDashboard();
    }

    private void abrirBuscarClientes() {
        BuscarPersonas bc = new BuscarPersonas(this, rootPaneCheckingEnabled, c, "clientes");
        bc.setVisible(true);
        cargarDatosDashboard();
    }

    private void abrirBuscarTerceros() {
        BuscarPersonas bc = new BuscarPersonas(this, rootPaneCheckingEnabled, c, "terceros");
        bc.setVisible(true);
        cargarDatosDashboard();
    }

    private void abrirBuscarFirmas() {
        BuscarPersonas bc = new BuscarPersonas(this, rootPaneCheckingEnabled, c, "firmas");
        bc.setVisible(true);
        cargarDatosDashboard();
    }

    private void abrirEliminarPersonas() {
        EliminarPersonas ec = new EliminarPersonas(this, rootPaneCheckingEnabled, c, "clientes");
        ec.setVisible(true);
        cargarDatosDashboard();
    }

    private void abrirGestionContadores() {
        GestionContadores gc = new GestionContadores(this, rootPaneCheckingEnabled, c);
        gc.setVisible(true);
        cargarDatosDashboard();
    }

    private void abrirListasContadores() {
        VerListasContadores vlc = new VerListasContadores(this, rootPaneCheckingEnabled, c);
        vlc.setVisible(true);
        cargarDatosDashboard();
    }

    private void abrirDeclaracionesContadores() {
        DeclaracionesContadores dc = new DeclaracionesContadores(this, rootPaneCheckingEnabled, c);
        dc.setVisible(true);
        cargarDatosDashboard();
    }

    private void confirmarCierre() {
        int response = JOptionPane.showConfirmDialog(this, "¿Deseas cerrar el programa?", "Confirmar Salida", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
