package UI;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Componente interactivo para selección de fecha con formato DD/MM/YYYY.
 * Incluye un campo de texto y un botón que despliega/retrae un calendario popup elegante
 * para facilitar la selección sin saturar la interfaz.
 */
public class DatePickerField extends JPanel {

    private final JTextField txtDate;
    private final JButton btnCalendar;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final List<ActionListener> actionListeners = new ArrayList<>();
    private JPopupMenu popupMenu;

    public DatePickerField() {
        this(new Date());
    }

    public DatePickerField(Date initialDate) {
        setLayout(new BorderLayout(4, 0));
        setOpaque(false);

        txtDate = new JTextField(10);
        txtDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDate.putClientProperty("JTextField.roundRect", true);
        if (initialDate != null) {
            txtDate.setText(dateFormat.format(initialDate));
        }

        btnCalendar = new JButton("📅");
        btnCalendar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        btnCalendar.setToolTipText("Abrir calendario interactivo");
        btnCalendar.putClientProperty("JButton.buttonType", "roundRect");
        btnCalendar.setMargin(new Insets(2, 6, 2, 6));
        btnCalendar.setFocusPainted(false);
        btnCalendar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCalendar.addActionListener(e -> toggleCalendarPopup());

        add(txtDate, BorderLayout.CENTER);
        add(btnCalendar, BorderLayout.EAST);
    }

    public String getText() {
        return txtDate.getText().trim();
    }

    public void setText(String text) {
        txtDate.setText(text != null ? text.trim() : "");
    }

    public Date getDate() {
        String val = getText();
        if (val.isEmpty()) return null;
        try {
            dateFormat.setLenient(false);
            return dateFormat.parse(val);
        } catch (ParseException e) {
            return null;
        }
    }

    public void setDate(Date date) {
        if (date != null) {
            txtDate.setText(dateFormat.format(date));
        } else {
            txtDate.setText("");
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        txtDate.setEnabled(enabled);
        btnCalendar.setEnabled(enabled);
    }

    public JTextField getTextField() {
        return txtDate;
    }

    public void addActionListener(ActionListener l) {
        if (l != null) {
            actionListeners.add(l);
        }
    }

    private void fireActionPerformed() {
        ActionEvent evt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getText());
        for (ActionListener l : actionListeners) {
            l.actionPerformed(evt);
        }
    }

    private void toggleCalendarPopup() {
        if (!isEnabled()) return;

        if (popupMenu != null && popupMenu.isVisible()) {
            popupMenu.setVisible(false);
            return;
        }

        popupMenu = new JPopupMenu();
        popupMenu.setBorder(new LineBorder(AppTheme.BORDER, 1, true));
        popupMenu.setBackground(AppTheme.SURFACE);

        Calendar cal = Calendar.getInstance();
        Date current = getDate();
        if (current != null) {
            cal.setTime(current);
        }

        CalendarPanel calendarPanel = new CalendarPanel(cal, selectedDate -> {
            setDate(selectedDate);
            fireActionPerformed();
            popupMenu.setVisible(false);
        }, () -> popupMenu.setVisible(false));

        popupMenu.add(calendarPanel);
        popupMenu.show(this, 0, getHeight() + 2);
    }

    /**
     * Panel interno del calendario desplegable.
     */
    private static class CalendarPanel extends JPanel {
        private final Calendar calendar;
        private final java.util.function.Consumer<Date> onDateSelected;
        private final Runnable onClose;
        private final JLabel lblMonthYear;
        private final JPanel daysGrid;
        private static final String[] MESES = {
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };
        private static final String[] DIAS_SEMANA = {"Lu", "Ma", "Mi", "Ju", "Vi", "Sá", "Do"};

        public CalendarPanel(Calendar initialCal, java.util.function.Consumer<Date> onDateSelected, Runnable onClose) {
            this.calendar = (Calendar) initialCal.clone();
            this.onDateSelected = onDateSelected;
            this.onClose = onClose;

            setLayout(new BorderLayout(5, 5));
            setBorder(new EmptyBorder(10, 10, 10, 10));
            setBackground(AppTheme.SURFACE);
            setPreferredSize(new Dimension(280, 260));

            // ========== Header: Navegación de Mes/Año ==========
            JPanel headerPanel = new JPanel(new BorderLayout(5, 0));
            headerPanel.setBackground(AppTheme.SURFACE);

            JButton btnPrev = new JButton("◀");
            btnPrev.setFont(new Font("Segoe UI", Font.BOLD, 10));
            btnPrev.setMargin(new Insets(2, 6, 2, 6));
            btnPrev.putClientProperty("JButton.buttonType", "roundRect");
            btnPrev.addActionListener(e -> {
                calendar.add(Calendar.MONTH, -1);
                updateCalendar();
            });

            JButton btnNext = new JButton("▶");
            btnNext.setFont(new Font("Segoe UI", Font.BOLD, 10));
            btnNext.setMargin(new Insets(2, 6, 2, 6));
            btnNext.putClientProperty("JButton.buttonType", "roundRect");
            btnNext.addActionListener(e -> {
                calendar.add(Calendar.MONTH, 1);
                updateCalendar();
            });

            lblMonthYear = new JLabel("", SwingConstants.CENTER);
            lblMonthYear.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblMonthYear.setForeground(AppTheme.PRIMARY);

            headerPanel.add(btnPrev, BorderLayout.WEST);
            headerPanel.add(lblMonthYear, BorderLayout.CENTER);
            headerPanel.add(btnNext, BorderLayout.EAST);
            add(headerPanel, BorderLayout.NORTH);

            // ========== Centro: Días de la semana + Grilla ==========
            JPanel centerPanel = new JPanel(new BorderLayout(0, 4));
            centerPanel.setBackground(AppTheme.SURFACE);

            JPanel weekHeader = new JPanel(new GridLayout(1, 7, 2, 2));
            weekHeader.setBackground(AppTheme.PANEL_BACKGROUND);
            weekHeader.setBorder(new EmptyBorder(4, 0, 4, 0));
            for (String dia : DIAS_SEMANA) {
                JLabel lblDia = new JLabel(dia, SwingConstants.CENTER);
                lblDia.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblDia.setForeground(AppTheme.TEXT_SECONDARY);
                weekHeader.add(lblDia);
            }
            centerPanel.add(weekHeader, BorderLayout.NORTH);

            daysGrid = new JPanel(new GridLayout(6, 7, 2, 2));
            daysGrid.setBackground(AppTheme.SURFACE);
            centerPanel.add(daysGrid, BorderLayout.CENTER);

            add(centerPanel, BorderLayout.CENTER);

            // ========== Footer: Botones Rápidos (Hoy / Cerrar) ==========
            JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            footerPanel.setBackground(AppTheme.SURFACE);

            JButton btnHoy = new JButton("Hoy");
            btnHoy.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btnHoy.putClientProperty("JButton.buttonType", "roundRect");
            btnHoy.addActionListener(e -> onDateSelected.accept(new Date()));

            JButton btnCerrar = new JButton("Cerrar");
            btnCerrar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btnCerrar.putClientProperty("JButton.buttonType", "roundRect");
            btnCerrar.addActionListener(e -> onClose.run());

            footerPanel.add(btnHoy);
            footerPanel.add(btnCerrar);
            add(footerPanel, BorderLayout.SOUTH);

            updateCalendar();
        }

        private void updateCalendar() {
            int mes = calendar.get(Calendar.MONTH);
            int anio = calendar.get(Calendar.YEAR);
            lblMonthYear.setText(MESES[mes] + " " + anio);

            daysGrid.removeAll();

            Calendar temp = (Calendar) calendar.clone();
            temp.set(Calendar.DAY_OF_MONTH, 1);

            // Determinar primer día de la semana (Lunes = 0, Domingo = 6)
            int primerDiaSemana = temp.get(Calendar.DAY_OF_WEEK); // Domingo=1, Lunes=2
            int shift = (primerDiaSemana == Calendar.SUNDAY) ? 6 : (primerDiaSemana - 2);

            int diasEnMes = temp.getActualMaximum(Calendar.DAY_OF_MONTH);

            // Celdas vacías antes del día 1
            for (int i = 0; i < shift; i++) {
                JLabel lblEmpty = new JLabel("");
                daysGrid.add(lblEmpty);
            }

            Calendar hoy = Calendar.getInstance();

            // Días del mes
            for (int dia = 1; dia <= diasEnMes; dia++) {
                final int currentDay = dia;
                JButton btnDia = new JButton(String.valueOf(dia));
                btnDia.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                btnDia.setMargin(new Insets(1, 1, 1, 1));
                btnDia.setFocusPainted(false);
                btnDia.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                boolean isToday = (hoy.get(Calendar.YEAR) == anio &&
                                   hoy.get(Calendar.MONTH) == mes &&
                                   hoy.get(Calendar.DAY_OF_MONTH) == dia);

                if (isToday) {
                    btnDia.setBackground(AppTheme.PRIMARY);
                    btnDia.setForeground(AppTheme.SURFACE);
                    btnDia.setFont(new Font("Segoe UI", Font.BOLD, 11));
                } else {
                    btnDia.setBackground(AppTheme.SURFACE);
                    btnDia.setForeground(AppTheme.TEXT_PRIMARY);
                    btnDia.setBorder(new LineBorder(AppTheme.BORDER, 1, true));
                }

                btnDia.addActionListener(e -> {
                    Calendar sel = (Calendar) calendar.clone();
                    sel.set(Calendar.DAY_OF_MONTH, currentDay);
                    onDateSelected.accept(sel.getTime());
                });

                daysGrid.add(btnDia);
            }

            // Completar celdas restantes de la grilla (6 filas * 7 columnas = 42)
            int totalCeldas = shift + diasEnMes;
            for (int i = totalCeldas; i < 42; i++) {
                JLabel lblEmpty = new JLabel("");
                daysGrid.add(lblEmpty);
            }

            daysGrid.revalidate();
            daysGrid.repaint();
        }
    }
}
