package UI;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Gestor del Tema Corporativo para el Despacho Contable.
 * Paleta de colores:
 *  - Primary / Accent:   #4F2384 (Morado Principal)
 *  - Background:         #FDFBF7 (Blanco Cálido / Canvas)
 *  - Panel Background:   #F3EFE6 (Beige / Grisáceo Secundario)
 *  - Surface / Cards:    #FFFFFF (Blanco Puro)
 *  - Borders:            #E5E7EB (Gris Claro Neutro)
 *  - Text Primary:       #1E1B26 (Carbón Púrpura Oscuro)
 *  - Text Secondary:     #6B7280 (Gris Neutro / Subtítulos)
 */
public final class AppTheme {

    public static final Color PRIMARY = Color.decode("#4F2384");
    public static final Color BACKGROUND = Color.decode("#FDFBF7");
    public static final Color PANEL_BACKGROUND = Color.decode("#F3EFE6");
    public static final Color SURFACE = Color.decode("#FFFFFF");
    public static final Color BORDER = Color.decode("#E5E7EB");
    public static final Color TEXT_PRIMARY = Color.decode("#1E1B26");
    public static final Color TEXT_SECONDARY = Color.decode("#6B7280");

    // Colores semánticos para el Semáforo de E-Firmas y Alertas
    public static final Color COLOR_VIGENTE_BG = new Color(46, 204, 113, 60);
    public static final Color COLOR_PROXIMO_BG = new Color(241, 196, 15, 75);
    public static final Color COLOR_VENCIDO_BG = new Color(231, 76, 60, 75);

    private AppTheme() {}

    /**
     * Configura FlatLaf e inyecta las propiedades de la paleta en UIManager.
     */
    public static void setupTheme() {
        try {
            // Configurar propiedades antes de inicializar FlatLightLaf
            UIManager.put("@accentColor", PRIMARY);
            UIManager.put("Component.accentColor", PRIMARY);
            UIManager.put("Component.focusColor", PRIMARY);
            UIManager.put("Component.borderColor", BORDER);
            UIManager.put("Component.focusedBorderColor", PRIMARY);

            // Fondos generales
            UIManager.put("Panel.background", BACKGROUND);
            UIManager.put("control", BACKGROUND);
            UIManager.put("window", BACKGROUND);

            // Textos
            UIManager.put("Label.foreground", TEXT_PRIMARY);
            UIManager.put("Label.disabledForeground", TEXT_SECONDARY);
            UIManager.put("text", TEXT_PRIMARY);
            UIManager.put("textHighlight", PRIMARY);
            UIManager.put("textHighlightText", SURFACE);

            // Tablas
            UIManager.put("Table.background", SURFACE);
            UIManager.put("Table.foreground", TEXT_PRIMARY);
            UIManager.put("Table.gridColor", BORDER);
            UIManager.put("Table.selectionBackground", PRIMARY);
            UIManager.put("Table.selectionForeground", SURFACE);
            UIManager.put("TableHeader.background", PANEL_BACKGROUND);
            UIManager.put("TableHeader.foreground", TEXT_PRIMARY);
            UIManager.put("TableHeader.bottomSeparatorColor", BORDER);

            // Botones
            UIManager.put("Button.arc", 10);
            UIManager.put("Button.background", SURFACE);
            UIManager.put("Button.foreground", TEXT_PRIMARY);
            UIManager.put("Button.borderColor", BORDER);
            UIManager.put("Button.hoverBorderColor", PRIMARY);
            UIManager.put("Button.focusedBorderColor", PRIMARY);

            // Campos de texto
            UIManager.put("TextField.arc", 8);
            UIManager.put("TextField.background", SURFACE);
            UIManager.put("TextField.foreground", TEXT_PRIMARY);
            UIManager.put("TextField.borderColor", BORDER);
            UIManager.put("TextField.focusedBorderColor", PRIMARY);

            // Scrollbars y Separadores
            UIManager.put("ScrollBar.thumb", BORDER);
            UIManager.put("ScrollBar.thumbHover", PRIMARY);
            UIManager.put("Separator.foreground", BORDER);

            FlatLightLaf.setup();
        } catch (Exception ex) {
            System.err.println("Advertencia al aplicar AppTheme: " + ex.getMessage());
        }
    }

    /**
     * Aplica estilo de botón principal (Fondo morado #4F2384 y texto blanco).
     */
    public static void stylePrimaryButton(JButton btn) {
        btn.setBackground(PRIMARY);
        btn.setForeground(SURFACE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Aplica estilo de botón secundario (Fondo blanco, borde #E5E7EB y texto morado/oscuro).
     */
    public static void styleSecondaryButton(JButton btn) {
        btn.setBackground(SURFACE);
        btn.setForeground(TEXT_PRIMARY);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(6, 12, 6, 12)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Crea un borde estilizado para tarjetas o paneles.
     */
    public static Border createCardBorder() {
        return new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        );
    }
}
