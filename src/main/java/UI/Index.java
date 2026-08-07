package UI;

import controlador.Controlador;
import entidades.Cliente;
import entidades.EFirmas;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

/**
 * Modern Dashboard Principal utilizing FlatLaf
 * @author Osmar & Antigravity
 */
public class Index extends javax.swing.JFrame {

    private static Controlador c;
    private boolean isDarkMode = true;

    private JLabel lblTotalClientes;
    private JLabel lblTotalTerceros;
    private JLabel lblTotalContadores;
    private JTable tblFirmas;
    private DefaultTableModel tableModel;

    public Index(Controlador controler) {
        c = controler;
        initComponentsCustom();
        cargarDatosDashboard();
    }

    private void initComponentsCustom() {
        setTitle("Despacho Contable - Dashboard");
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                confirmarCierre();
            }
        });

        // Contenedor principal con BorderLayout
        JPanel mainContainer = new JPanel(new BorderLayout());
        setContentPane(mainContainer);

        // ================= SIDEBAR (Panel Lateral Izquierdo) =================
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(240, 600));
        sidebar.setBorder(new EmptyBorder(25, 20, 25, 20));
        sidebar.setBackground(UIManager.getColor("Panel.background")); // FlatLaf color

        // Título Logo
        JLabel lblLogo = new JLabel("CONTABILIDAD");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubLogo = new JLabel("Dashboard Principal");
        lblSubLogo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubLogo.setForeground(UIManager.getColor("Label.disabledForeground"));
        lblSubLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(lblLogo);
        sidebar.add(lblSubLogo);
        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        // Botones de Acceso Rápido en la Barra Lateral
        JButton btnDashboard = crearBotonSidebar("📊 Dashboard");
        btnDashboard.addActionListener(e -> cargarDatosDashboard());
        sidebar.add(btnDashboard);
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));

        JButton btnClientes = crearBotonSidebar("👥 Clientes");
        btnClientes.addActionListener(e -> abrirBuscarClientes());
        sidebar.add(btnClientes);
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));

        JButton btnTerceros = crearBotonSidebar("🤝 Terceros");
        btnTerceros.addActionListener(e -> abrirBuscarTerceros());
        sidebar.add(btnTerceros);
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));

        JButton btnFirmas = crearBotonSidebar("🔑 E-Firmas");
        btnFirmas.addActionListener(e -> abrirBuscarFirmas());
        sidebar.add(btnFirmas);

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
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header superior
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        JLabel lblHeaderTitle = new JLabel("Bienvenido de nuevo");
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        JLabel lblHeaderSub = new JLabel("Resumen general del estado de tus clientes");
        lblHeaderSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblHeaderSub.setForeground(UIManager.getColor("Label.disabledForeground"));
        
        headerPanel.add(lblHeaderTitle, BorderLayout.NORTH);
        headerPanel.add(lblHeaderSub, BorderLayout.SOUTH);
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        // Área Central: Métrica + Tabla
        JPanel centerPanel = new JPanel(new BorderLayout(0, 25));

        // 1. Fila de Tarjetas de Métricas (KPI Cards)
        JPanel cardsContainer = new JPanel(new GridLayout(1, 3, 20, 0));
        
        JPanel cardClientes = crearCardMetrica("Total Clientes", "0", new Color(41, 128, 185));
        lblTotalClientes = (JLabel) cardClientes.getClientProperty("valLabel");
        
        JPanel cardTerceros = crearCardMetrica("Total Terceros", "0", new Color(39, 174, 96));
        lblTotalTerceros = (JLabel) cardTerceros.getClientProperty("valLabel");
        
        JPanel cardContadores = crearCardMetrica("Contadores Activos", "0", new Color(142, 68, 173));
        lblTotalContadores = (JLabel) cardContadores.getClientProperty("valLabel");

        cardsContainer.add(cardClientes);
        cardsContainer.add(cardTerceros);
        cardsContainer.add(cardContadores);
        centerPanel.add(cardsContainer, BorderLayout.NORTH);

        // 2. Tabla de E-Firmas por vencer
        JPanel tableSection = new JPanel(new BorderLayout());
        tableSection.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor"), 1, true),
                " 🔑 E-Firmas Registradas / Expiraciones ",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                UIManager.getColor("Label.foreground")
        ));

        tableModel = new DefaultTableModel(
            new Object[][]{},
            new String[]{"Cliente", "RFC", "Fecha Expiración", "Fecha Renovación"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblFirmas = new JTable(tableModel);
        tblFirmas.setRowHeight(30);
        tblFirmas.setShowHorizontalLines(true);
        tblFirmas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblFirmas.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(tblFirmas);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tableSection.add(scrollPane, BorderLayout.CENTER);

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
        return btn;
    }

    private JPanel crearCardMetrica(String titulo, String valor, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor"), 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel lblTitle = new JLabel(titulo);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(UIManager.getColor("Label.disabledForeground"));
        
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

        // Cargar contadores rápidos
        lblTotalClientes.setText(String.valueOf(c.getAllClientes().size()));
        lblTotalTerceros.setText(String.valueOf(c.getTerceros().size()));
        lblTotalContadores.setText(String.valueOf(c.getAllContadores().size()));

        // Poblar tabla de E-Firmas
        tableModel.setRowCount(0);
        Map<Integer, EFirmas> firmas = c.getAllFirmas();
        for (EFirmas f : firmas.values()) {
            Cliente cli = c.getClienteById(f.getIdCliente());
            String nombreCliente = (cli != null) ? cli.nombre : "Cliente #" + f.getIdCliente();
            String rfcCliente = (cli != null) ? cli.rfc : "N/A";
            tableModel.addRow(new Object[]{
                nombreCliente,
                rfcCliente,
                f.fecha_expiracion,
                f.fecha_renovacion
            });
        }
    }

    private void alternarTema() {
        FlatAnimatedLafChange.showSnapshot();
        try {
            if (isDarkMode) {
                UIManager.setLookAndFeel(new FlatLightLaf());
                isDarkMode = false;
            } else {
                UIManager.setLookAndFeel(new FlatDarkLaf());
                isDarkMode = true;
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
        cargarDatosDashboard(); // refrescar
    }

    private void abrirAgregarTercero() {
        añadirTercero at = new añadirTercero(this, rootPaneCheckingEnabled, c);
        at.setVisible(true);
        cargarDatosDashboard(); // refrescar
    }

    private void abrirBuscarClientes() {
        BuscarPersonas bc = new BuscarPersonas(this, rootPaneCheckingEnabled, c, "clientes");
        bc.setVisible(true);
        cargarDatosDashboard(); // refrescar
    }

    private void abrirBuscarTerceros() {
        BuscarPersonas bc = new BuscarPersonas(this, rootPaneCheckingEnabled, c, "terceros");
        bc.setVisible(true);
        cargarDatosDashboard(); // refrescar
    }

    private void abrirBuscarFirmas() {
        BuscarPersonas bc = new BuscarPersonas(this, rootPaneCheckingEnabled, c, "firmas");
        bc.setVisible(true);
        cargarDatosDashboard(); // refrescar
    }

    private void abrirEliminarPersonas() {
        EliminarPersonas ec = new EliminarPersonas(this, rootPaneCheckingEnabled, c, "clientes");
        ec.setVisible(true);
        cargarDatosDashboard(); // refrescar
    }

    private void confirmarCierre() {
        int response = JOptionPane.showConfirmDialog(this, "¿Deseas cerrar el programa?", "Confirmar Salida", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
