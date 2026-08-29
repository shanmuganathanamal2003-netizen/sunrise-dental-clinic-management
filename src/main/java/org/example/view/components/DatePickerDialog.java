package org.example.view.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * DatePickerDialog - Interactive Swing Calendar Date Picker
 *
 * Allows users to visually browse months/years and pick a date.
 */

public class DatePickerDialog extends JDialog {

    private LocalDate selectedDate;
    private YearMonth currentYearMonth;
    private final JPanel daysPanel;
    private final JLabel lblMonthYear;
    private final JComboBox<Integer> cmbYear;
    private final JComboBox<String> cmbMonth;
    private boolean isConfirmed = false;

    public DatePickerDialog(Frame parent, LocalDate initialDate) {
        super(parent, "Select Appointment Date", true);
        this.selectedDate = (initialDate != null) ? initialDate : LocalDate.now();
        this.currentYearMonth = YearMonth.from(this.selectedDate);

        setSize(360, 360);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout(8, 8));

        // ------------------ Top Navigation Panel ------------------
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBackground(UIHelper.COLOR_PRIMARY);
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JButton btnPrev = new JButton("< Prev");
        btnPrev.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnPrev.setFocusPainted(false);
        btnPrev.addActionListener(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            updateSelectors();
            renderCalendarDays();
        });

        JButton btnNext = new JButton("Next >");
        btnNext.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnNext.setFocusPainted(false);
        btnNext.addActionListener(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            updateSelectors();
            renderCalendarDays();
        });

        lblMonthYear = new JLabel("", SwingConstants.CENTER);
        lblMonthYear.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblMonthYear.setForeground(Color.WHITE);

        topPanel.add(btnPrev, BorderLayout.WEST);
        topPanel.add(lblMonthYear, BorderLayout.CENTER);
        topPanel.add(btnNext, BorderLayout.EAST);

        // Sub Selector Row: Quick Month & Year Dropdowns
        JPanel selectorRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        selectorRow.setBackground(new Color(230, 240, 250));

        String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };
        cmbMonth = new JComboBox<>(months);
        cmbMonth.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        int currentYear = LocalDate.now().getYear();
        Integer[] years = new Integer[15];
        for (int i = 0; i < years.length; i++) {
            years[i] = currentYear - 2 + i;
        }
        cmbYear = new JComboBox<>(years);
        cmbYear.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        cmbMonth.addActionListener(e -> {
            int m = cmbMonth.getSelectedIndex() + 1;
            Integer y = (Integer) cmbYear.getSelectedItem();
            if (y != null) {
                currentYearMonth = YearMonth.of(y, m);
                renderCalendarDays();
            }
        });

        cmbYear.addActionListener(e -> {
            int m = cmbMonth.getSelectedIndex() + 1;
            Integer y = (Integer) cmbYear.getSelectedItem();
            if (y != null) {
                currentYearMonth = YearMonth.of(y, m);
                renderCalendarDays();
            }
        });

        selectorRow.add(new JLabel("Month:"));
        selectorRow.add(cmbMonth);
        selectorRow.add(new JLabel("Year:"));
        selectorRow.add(cmbYear);

        JPanel headerContainer = new JPanel(new BorderLayout());
        headerContainer.add(topPanel, BorderLayout.NORTH);
        headerContainer.add(selectorRow, BorderLayout.SOUTH);
        add(headerContainer, BorderLayout.NORTH);

        // ------------------ Calendar Days Grid ------------------
        JPanel calendarContainer = new JPanel(new BorderLayout(2, 2));
        calendarContainer.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        // Day names header (Sun, Mon, Tue...)
        JPanel dayNamesPanel = new JPanel(new GridLayout(1, 7, 2, 2));
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : dayNames) {
            JLabel lblDay = new JLabel(day, SwingConstants.CENTER);
            lblDay.setFont(new Font("Segoe UI", Font.BOLD, 12));
            if ("Sun".equals(day)) {
                lblDay.setForeground(new Color(190, 30, 30));
            } else {
                lblDay.setForeground(new Color(70, 70, 70));
            }
            dayNamesPanel.add(lblDay);
        }
        calendarContainer.add(dayNamesPanel, BorderLayout.NORTH);

        // Days Buttons Grid (6 weeks x 7 days)
        daysPanel = new JPanel(new GridLayout(6, 7, 3, 3));
        calendarContainer.add(daysPanel, BorderLayout.CENTER);
        add(calendarContainer, BorderLayout.CENTER);

        // ------------------ Footer Buttons ------------------
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        JButton btnToday = UIHelper.createSecondaryButton("Today", new Dimension(80, 28));
        JButton btnCancel = UIHelper.createSecondaryButton("Cancel", new Dimension(80, 28));

        btnToday.addActionListener(e -> {
            selectedDate = LocalDate.now();
            currentYearMonth = YearMonth.from(selectedDate);
            isConfirmed = true;
            dispose();
        });

        btnCancel.addActionListener(e -> {
            isConfirmed = false;
            dispose();
        });

        footerPanel.add(btnToday);
        footerPanel.add(btnCancel);
        add(footerPanel, BorderLayout.SOUTH);

        updateSelectors();
        renderCalendarDays();
    }

    private void updateSelectors() {
        lblMonthYear.setText(currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)));
        cmbMonth.setSelectedIndex(currentYearMonth.getMonthValue() - 1);
        cmbYear.setSelectedItem(currentYearMonth.getYear());
    }

    private void renderCalendarDays() {
        daysPanel.removeAll();
        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday = 0
        int daysInMonth = currentYearMonth.lengthOfMonth();

        LocalDate today = LocalDate.now();

        // Empty labels for preceding days
        for (int i = 0; i < dayOfWeek; i++) {
            JLabel empty = new JLabel("");
            daysPanel.add(empty);
        }

        // Day buttons
        for (int d = 1; d <= daysInMonth; d++) {
            final int dayNum = d;
            LocalDate thisDate = currentYearMonth.atDay(dayNum);

            JButton btnDay = new JButton(String.valueOf(dayNum));
            btnDay.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btnDay.setFocusPainted(false);
            btnDay.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnDay.setMargin(new java.awt.Insets(1, 1, 1, 1));

            if (thisDate.equals(selectedDate)) {
                btnDay.setBackground(UIHelper.COLOR_PRIMARY);
                btnDay.setForeground(Color.WHITE);
                btnDay.setFont(new Font("Segoe UI", Font.BOLD, 12));
            } else if (thisDate.equals(today)) {
                btnDay.setBackground(new Color(220, 245, 220));
                btnDay.setForeground(new Color(0, 100, 0));
                btnDay.setFont(new Font("Segoe UI", Font.BOLD, 12));
            } else {
                btnDay.setBackground(Color.WHITE);
                btnDay.setForeground(Color.BLACK);
            }

            btnDay.addActionListener(e -> {
                selectedDate = thisDate;
                isConfirmed = true;
                dispose();
            });

            daysPanel.add(btnDay);
        }

        // Fill remaining slots in 42-grid
        int totalCellsFilled = dayOfWeek + daysInMonth;
        for (int i = totalCellsFilled; i < 42; i++) {
            JLabel empty = new JLabel("");
            daysPanel.add(empty);
        }

        daysPanel.revalidate();
        daysPanel.repaint();
    }

    public static String showDatePicker(Frame parent, String initialDateStr) {
        LocalDate initial = null;
        try {
            if (initialDateStr != null && !initialDateStr.trim().isEmpty()) {
                initial = LocalDate.parse(initialDateStr.trim());
            }
        } catch (Exception ignore) {}

        DatePickerDialog dialog = new DatePickerDialog(parent, initial);
        dialog.setVisible(true);

        if (dialog.isConfirmed && dialog.selectedDate != null) {
            return dialog.selectedDate.toString();
        }
        return null;
    }
}
