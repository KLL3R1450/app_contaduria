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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Modern Dashboard Principal utilizing FlatLaf
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

    public Index(Controlador controler) {
        c = controler;
        initComponentsCustom();
        cargarDatosDashboard();
    }

    private void initComponentsCustom() {
        setTitle("Despacho Contable - Dashboard");
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(950, 600));
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


        JButton btnClientes = crearBotonSidebar(" Clientes");
        btnClientes.addActionListener(e -> abrirBuscarClientes());
        sidebar.add(btnClientes);
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));

        JButton btnTerceros = crearBotonSidebar(" Terceros");
        btnTerceros.addActionListener(e -> abrirBuscarTerceros());
        sidebar.add(btnTerceros);
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));

        JButton btnFirmas = crearBotonSidebar(" E-Firmas");
        btnFirmas.addActionListener(e -> abrirBuscarFirmas());
        sidebar.add(btnFirmas);
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));

        JButton btnListasContadores = crearBotonSidebar(" Listas Contadores");
        btnListasContadores.addActionListener(e -> abrirListasContadores());
        sidebar.add(btnListasContadores);
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));

        JButton btnDeclaraciones = crearBotonSidebar(" Declaraciones");
        btnDeclaraciones.addActionListener(e -> abrirDeclaracionesContadores());
        sidebar.add(btnDeclaraciones);

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
                " E-Firmas Registradas / Expiraciones ",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                UIManager.getColor("Label.foreground")
        ));

        tableModel = new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID Cliente", "Cliente", "RFC", "Fecha Expiración", "Fecha Renovación", "Estado"}
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

        // Ocultar ID Cliente
        tblFirmas.getColumnModel().getColumn(0).setMinWidth(0);
        tblFirmas.getColumnModel().getColumn(0).setMaxWidth(0);
        tblFirmas.getColumnModel().getColumn(0).setPreferredWidth(0);

        // Renderizador de Semáforo
        tblFirmas.getColumnModel().getColumn(5).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cComp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    String val = value.toString();
                    if (val.contains("Vigente")) {
                        cComp.setBackground(new Color(46, 204, 113, 80)); // Verde pastel
                        cComp.setForeground(table.getForeground());
                    } else if (val.contains("Próximo")) {
                        cComp.setBackground(new Color(241, 196, 15, 80)); // Amarillo pastel
                        cComp.setForeground(table.getForeground());
                    } else if (val.contains("Vencido")) {
                        cComp.setBackground(new Color(231, 76, 60, 80)); // Rojo pastel
                        cComp.setForeground(table.getForeground());
                    }
                }
                if (isSelected) {
                    cComp.setBackground(table.getSelectionBackground());
                    cComp.setForeground(table.getSelectionForeground());
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
                            cargarDatosDashboard();
                        }
                    }
                }
            }
        });

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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date hoy = new Date();

        for (EFirmas f : firmas.values()) {
            Cliente cli = c.getClienteById(f.getIdCliente());
            String nombreCliente = (cli != null) ? cli.nombre : "Cliente #" + f.getIdCliente();
            String rfcCliente = (cli != null) ? cli.rfc : "N/A";
            
            String estado = "Sin Datos";
            try {
                Date fExp = sdf.parse(f.fecha_expiracion);
                long diffMs = fExp.getTime() - hoy.getTime();
                long diffDays = diffMs / (1000 * 60 * 60 * 24);

                if (diffDays >= 365) {
                    estado = "🟢 Vigente (" + (diffDays / 365) + " año/s)";
                } else if (diffDays >= 30) {
                    estado = "🟡 Próximo a vencer (" + (diffDays / 30) + " mes/es)";
                } else {
                    estado = "🔴 Vencido / < 1 mes";
                }
            } catch (Exception ex) {
                // Formato incorrecto o nulo
            }

            tableModel.addRow(new Object[]{
                f.getIdCliente(),
                nombreCliente,
                rfcCliente,
                f.fecha_expiracion,
                f.fecha_renovacion,
                estado
            });
        }
    }

    private void alternarTema() {
        FlatAnimatedLafChange.showSnapshot();
        try {
            if (!isDarkMode) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
                isDarkMode = true;
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
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

    private void abrirListasContadores() {
        VerListasContadores vlc = new VerListasContadores(this, rootPaneCheckingEnabled, c);
        vlc.setVisible(true);
        cargarDatosDashboard(); // refrescar
    }

    private void abrirDeclaracionesContadores() {
        DeclaracionesContadores dc = new DeclaracionesContadores(this, rootPaneCheckingEnabled, c);
        dc.setVisible(true);
        cargarDatosDashboard(); // refrescar
    }

    private void confirmarCierre() {
        int response = JOptionPane.showConfirmDialog(this, "¿Deseas cerrar el programa?", "Confirmar Salida", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
