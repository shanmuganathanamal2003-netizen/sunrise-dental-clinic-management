package org.example.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.example.model.Appointment;
import org.example.model.User;
import org.example.service.AppointmentService;
import org.example.view.components.AppMenuBar;
import org.example.view.components.DatePickerDialog;
import org.example.view.components.UIHelper;

/**
 * DoctorQueueView - Doctor's Patient Schedule & Clinical Queue
 * 
 * Displays filtered appointments assigned to the logged-in doctor with flexible
 * date navigation ("Today", "Tomorrow", "Next 7 Days", custom date picker) and
 * clinical diagnosis / treatment notes recording.
 */
public class DoctorQueueView extends JFrame {

    private final User currentUser;
    private final AppointmentService appointmentService;

    // Filter controls
    private JButton btnToday;
    private JButton btnTomorrow;
    private JButton btnNext7Days;
    private JButton btnPickDate;
    private JLabel lblActiveFilter;

    // Current filter state
    private String currentFilterMode = "TODAY";
    private String customSelectedDate = null;

    // Appointments Table
    private JTable tblDoctorAppointments;
    private DefaultTableModel tableModel;
    private JLabel lblRecordCount;

    // Action Buttons
    private JButton btnAddNotes;
    private JButton btnViewPatientHistory;
    private JButton btnConfirmAppt;
    private JButton btnCancelAppt;
    private JButton btnRefresh;
    private JButton btnBack;

    public DoctorQueueView(User user) {
        this.currentUser = user;
        this.appointmentService = new AppointmentService();
        initializeUI();
        loadAppointmentsToday();
    }

    private void initializeUI() {
        setTitle("Sunrise Dental Clinic - My Patient Schedule (" + currentUser.getFullName() + ")");
        setSize(1080, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setLayout(new BorderLayout(10, 10));

        // Reusable Top Menu Bar
        setJMenuBar(AppMenuBar.createMenuBar(this, currentUser));

        // ------------------ Top Header Banner ------------------
        JPanel headerPanel = UIHelper.createHeaderBanner(
            "MY PATIENT SCHEDULE & CLINICAL QUEUE",
            "Assigned patient appointments and clinical diagnosis notes for " + currentUser.getFullName(),
            currentUser
        );
        add(headerPanel, BorderLayout.NORTH);

        // ------------------ Center Content ------------------
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Filter Bar Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Date Navigation & Schedule Filter"),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        JLabel lblQuick = new JLabel("Quick Filters:");
        lblQuick.setFont(UIHelper.FONT_BOLD);

        btnToday = UIHelper.createPrimaryButton("Today", new Dimension(90, 28));
        btnTomorrow = UIHelper.createSecondaryButton("Tomorrow", new Dimension(105, 28));
        btnNext7Days = UIHelper.createSecondaryButton("Next 7 Days", new Dimension(115, 28));
        btnPickDate = UIHelper.createSecondaryButton("📅 Pick Specific Date", new Dimension(160, 28));

        lblActiveFilter = new JLabel("Viewing: Today (" + LocalDate.now() + ")");
        lblActiveFilter.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblActiveFilter.setForeground(UIHelper.COLOR_PRIMARY);

        lblRecordCount = new JLabel("0 patient(s) found");
        lblRecordCount.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblRecordCount.setForeground(new Color(90, 90, 90));

        filterPanel.add(lblQuick);
        filterPanel.add(btnToday);
        filterPanel.add(btnTomorrow);
        filterPanel.add(btnNext7Days);
        filterPanel.add(btnPickDate);
        filterPanel.add(lblActiveFilter);
        filterPanel.add(lblRecordCount);

        centerPanel.add(filterPanel, BorderLayout.NORTH);

        // Appointments Table
        String[] columnNames = {
            "Appt #", "Patient Name", "Age", "Contact", "Date", "Time",
            "Treatment Procedure", "Status", "Clinical Notes / Diagnosis"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblDoctorAppointments = new JTable(tableModel);
        tblDoctorAppointments.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblDoctorAppointments.setRowHeight(28);
        tblDoctorAppointments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDoctorAppointments.getTableHeader().setFont(UIHelper.FONT_BOLD);
        tblDoctorAppointments.getTableHeader().setBackground(new Color(235, 242, 250));

        // Column Widths
        tblDoctorAppointments.getColumnModel().getColumn(0).setPreferredWidth(55);
        tblDoctorAppointments.getColumnModel().getColumn(1).setPreferredWidth(140);
        tblDoctorAppointments.getColumnModel().getColumn(2).setPreferredWidth(75);
        tblDoctorAppointments.getColumnModel().getColumn(3).setPreferredWidth(95);
        tblDoctorAppointments.getColumnModel().getColumn(4).setPreferredWidth(85);
        tblDoctorAppointments.getColumnModel().getColumn(5).setPreferredWidth(75);
        tblDoctorAppointments.getColumnModel().getColumn(6).setPreferredWidth(150);
        tblDoctorAppointments.getColumnModel().getColumn(7).setPreferredWidth(80);
        tblDoctorAppointments.getColumnModel().getColumn(8).setPreferredWidth(220);

        // Status Renderer
        tblDoctorAppointments.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                String val = String.valueOf(value);
                if ("Billed".equalsIgnoreCase(val)) {
                    setForeground(new Color(0, 130, 0));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if ("Cancelled".equalsIgnoreCase(val)) {
                    setForeground(new Color(190, 25, 25));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if ("Confirmed".equalsIgnoreCase(val)) {
                    setForeground(new Color(20, 90, 170));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    setForeground(new Color(210, 100, 0));
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblDoctorAppointments);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // ------------------ Bottom Action Buttons ------------------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));

        btnAddNotes = UIHelper.createPrimaryButton("📝 Add Diagnosis / Treatment Notes", new Dimension(260, 36));
        btnViewPatientHistory = UIHelper.createSecondaryButton("View Patient History", new Dimension(170, 36));
        btnConfirmAppt = UIHelper.createPrimaryButton("✅ Confirm Appointment", new Dimension(190, 36));
        btnCancelAppt = UIHelper.createDangerButton("❌ Cancel Appointment", new Dimension(180, 36));
        btnRefresh = UIHelper.createSecondaryButton("Refresh Schedule", new Dimension(145, 36));
        btnBack = UIHelper.createSecondaryButton("Back to Dashboard", new Dimension(150, 36));

        buttonPanel.add(btnAddNotes);
        buttonPanel.add(btnViewPatientHistory);
        buttonPanel.add(btnConfirmAppt);
        buttonPanel.add(btnCancelAppt);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnBack);

        add(buttonPanel, BorderLayout.SOUTH);

        // ------------------ Event Listeners ------------------
        btnToday.addActionListener(e -> loadAppointmentsToday());
        btnTomorrow.addActionListener(e -> loadAppointmentsTomorrow());
        btnNext7Days.addActionListener(e -> loadAppointmentsNext7Days());
        btnPickDate.addActionListener(e -> openDatePicker());

        btnAddNotes.addActionListener(e -> openAddNotesDialog());
        btnConfirmAppt.addActionListener(e -> confirmSelectedAppointment());
        btnCancelAppt.addActionListener(e -> cancelSelectedAppointment());

        btnViewPatientHistory.addActionListener(e -> {
            int selectedRow = tblDoctorAppointments.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an appointment from the table to view patient records.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int apptNo = (int) tableModel.getValueAt(selectedRow, 0);
            try {
                Appointment appt = appointmentService.getAppointmentByNumber(apptNo);
                if (appt != null && appt.getPatientId() != null && appt.getPatientId() > 0) {
                    UIHelper.navigate(this, new PatientHistoryView(currentUser, appt.getPatientId()));
                } else {
                    UIHelper.navigate(this, new PatientHistoryView(currentUser));
                }
            } catch (SQLException ex) {
                UIHelper.navigate(this, new PatientHistoryView(currentUser));
            }
        });

        btnRefresh.addActionListener(e -> reloadCurrentFilter());

        btnBack.addActionListener(e -> {
            UIHelper.navigate(this, new MainMenuView(currentUser));
        });
    }

    private void updateFilterButtonStyles(JButton activeBtn) {
        btnToday.setBackground(new Color(245, 248, 252));
        btnToday.setForeground(UIHelper.COLOR_PRIMARY);
        btnTomorrow.setBackground(new Color(245, 248, 252));
        btnTomorrow.setForeground(UIHelper.COLOR_PRIMARY);
        btnNext7Days.setBackground(new Color(245, 248, 252));
        btnNext7Days.setForeground(UIHelper.COLOR_PRIMARY);
        btnPickDate.setBackground(new Color(245, 248, 252));
        btnPickDate.setForeground(UIHelper.COLOR_PRIMARY);

        if (activeBtn != null) {
            activeBtn.setBackground(UIHelper.COLOR_PRIMARY);
            activeBtn.setForeground(Color.WHITE);
        }
    }

    private void loadAppointmentsToday() {
        currentFilterMode = "TODAY";
        updateFilterButtonStyles(btnToday);
        String today = LocalDate.now().toString();
        lblActiveFilter.setText("Viewing: Today (" + today + ")");
        loadAppointmentsForDate(today);
    }

    private void loadAppointmentsTomorrow() {
        currentFilterMode = "TOMORROW";
        updateFilterButtonStyles(btnTomorrow);
        String tomorrow = LocalDate.now().plusDays(1).toString();
        lblActiveFilter.setText("Viewing: Tomorrow (" + tomorrow + ")");
        loadAppointmentsForDate(tomorrow);
    }

    private void loadAppointmentsNext7Days() {
        currentFilterMode = "NEXT_7_DAYS";
        updateFilterButtonStyles(btnNext7Days);
        String fromDate = LocalDate.now().toString();
        String toDate = LocalDate.now().plusDays(7).toString();
        lblActiveFilter.setText("Viewing: Next 7 Days (" + fromDate + " to " + toDate + ")");
        loadAppointmentsForDateRange(fromDate, toDate);
    }

    private void openDatePicker() {
        String defaultDate = customSelectedDate != null ? customSelectedDate : LocalDate.now().toString();
        String picked = DatePickerDialog.showDatePicker(this, defaultDate);
        if (picked != null && !picked.trim().isEmpty()) {
            currentFilterMode = "CUSTOM";
            customSelectedDate = picked.trim();
            updateFilterButtonStyles(btnPickDate);
            lblActiveFilter.setText("Viewing: Picked Date (" + customSelectedDate + ")");
            loadAppointmentsForDate(customSelectedDate);
        }
    }

    private void reloadCurrentFilter() {
        switch (currentFilterMode) {
            case "TOMORROW":
                loadAppointmentsTomorrow();
                break;
            case "NEXT_7_DAYS":
                loadAppointmentsNext7Days();
                break;
            case "CUSTOM":
                if (customSelectedDate != null) {
                    loadAppointmentsForDate(customSelectedDate);
                } else {
                    loadAppointmentsToday();
                }
                break;
            case "TODAY":
            default:
                loadAppointmentsToday();
                break;
        }
    }

    private void loadAppointmentsForDate(String date) {
        tableModel.setRowCount(0);
        try {
            List<Appointment> list = appointmentService.getAppointmentsForDoctor(currentUser.getUsername(), date);
            populateTable(list);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error loading doctor schedule:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAppointmentsForDateRange(String fromDate, String toDate) {
        tableModel.setRowCount(0);
        try {
            List<Appointment> list = appointmentService.getAppointmentsForDoctor(currentUser.getUsername(), fromDate, toDate);
            populateTable(list);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error loading doctor schedule:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<Appointment> list) {
        tableModel.setRowCount(0);
        for (Appointment a : list) {
            tableModel.addRow(new Object[]{
                a.getAppointmentNumber(),
                a.getPatientName(),
                a.getPatientAge() != null ? a.getPatientAge() : "1 Month",
                a.getContactNumber(),
                a.getAppointmentDate(),
                a.getAppointmentTime(),
                a.getTreatmentType(),
                a.getStatus(),
                a.getDoctorNotes() != null ? a.getDoctorNotes() : "-"
            });
        }
        lblRecordCount.setText(list.size() + " appointment(s) found");
    }

    private void openAddNotesDialog() {
        int selectedRow = tblDoctorAppointments.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment from the table to add or edit clinical diagnosis notes.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int apptNo = (int) tableModel.getValueAt(selectedRow, 0);
        String patientName = (String) tableModel.getValueAt(selectedRow, 1);
        String procedure = (String) tableModel.getValueAt(selectedRow, 6);
        Object currentNotesObj = tableModel.getValueAt(selectedRow, 8);
        String existingNotes = (currentNotesObj != null && !"-".equals(currentNotesObj)) ? String.valueOf(currentNotesObj) : "";

        JDialog dialog = new JDialog(this, "Add / Edit Clinical Diagnosis Notes - Appt #" + apptNo, true);
        dialog.setSize(520, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.3;
        JLabel lblAppt = new JLabel("Appointment:");
        lblAppt.setFont(UIHelper.FONT_BOLD);
        formPanel.add(lblAppt, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(new JLabel("#" + apptNo + " - " + patientName + " (" + procedure + ")"), gbc);
        r++;

        gbc.gridx = 0; gbc.gridy = r;
        JLabel lblNotes = new JLabel("Clinical Notes *:");
        lblNotes.setFont(UIHelper.FONT_BOLD);
        formPanel.add(lblNotes, gbc);

        gbc.gridx = 1;
        JTextArea txtNotesArea = new JTextArea(existingNotes, 8, 25);
        txtNotesArea.setFont(UIHelper.FONT_REGULAR);
        txtNotesArea.setLineWrap(true);
        txtNotesArea.setWrapStyleWord(true);
        JScrollPane areaScroll = new JScrollPane(txtNotesArea);
        formPanel.add(areaScroll, gbc);
        r++;

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnSave = UIHelper.createPrimaryButton("Save Notes", new Dimension(120, 32));
        JButton btnCancel = UIHelper.createSecondaryButton("Cancel", new Dimension(90, 32));

        btnSave.addActionListener(e -> {
            String newNotes = txtNotesArea.getText().trim();
            if (newNotes.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter diagnosis or treatment notes.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                boolean success = appointmentService.saveDoctorNotes(apptNo, newNotes);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Clinical notes successfully saved for Appointment #" + apptNo + "!", "Notes Saved", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    reloadCurrentFilter();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to save notes. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void confirmSelectedAppointment() {
        int selectedRow = tblDoctorAppointments.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment from the table to confirm.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int apptNo = (int) tableModel.getValueAt(selectedRow, 0);
        String patientName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 7);

        if ("Confirmed".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this, "This appointment is already confirmed.", "Already Confirmed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if ("Cancelled".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this, "This appointment is cancelled and cannot be confirmed.", "Cannot Confirm", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Confirm that you will see patient '" + patientName + "' for Appointment #" + apptNo + "?",
                "Confirm Appointment",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean success = appointmentService.confirmAppointment(apptNo);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Appointment #" + apptNo + " has been CONFIRMED.", "Appointment Confirmed", JOptionPane.INFORMATION_MESSAGE);
                    reloadCurrentFilter();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cancelSelectedAppointment() {
        int selectedRow = tblDoctorAppointments.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment from the table to cancel.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int apptNo = (int) tableModel.getValueAt(selectedRow, 0);
        String patientName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 7);

        if ("Cancelled".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this, "This appointment is already cancelled.", "Already Cancelled", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to CANCEL Appointment #" + apptNo + " for patient '" + patientName + "'?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            String reason = JOptionPane.showInputDialog(
                    this,
                    "Optional: Enter cancellation reason:",
                    "Cancellation Reason",
                    JOptionPane.PLAIN_MESSAGE
            );
            try {
                boolean success = appointmentService.cancelAppointment(apptNo, reason);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Appointment #" + apptNo + " has been CANCELLED.", "Appointment Cancelled", JOptionPane.INFORMATION_MESSAGE);
                    reloadCurrentFilter();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
