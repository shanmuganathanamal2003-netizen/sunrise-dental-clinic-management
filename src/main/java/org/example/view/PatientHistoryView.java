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
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.example.dao.PatientDAO;
import org.example.model.Appointment;
import org.example.model.Patient;
import org.example.model.User;
import org.example.service.AppointmentService;
import org.example.view.components.AppMenuBar;
import org.example.view.components.UIHelper;

/**
 * PatientHistoryView - Unified Patient Search & Clinical Records History Screen
 * 
 * Merges Patient Search (by Name, Phone, ID, or Appointment Number) with complete
 * demographic profiles, medical notes, appointment timeline, direct booking,
 * billing, appointment cancellation, and history printing.
 */
public class PatientHistoryView extends JFrame {

    private final User currentUser;
    private final PatientDAO patientDAO;
    private final AppointmentService appointmentService;
    private Patient currentPatient;

    // Search Controls
    private JTextField txtSearch;
    private JComboBox<Patient> cmbQuickSelect;
    private JButton btnSearch;
    private JButton btnReloadPatients;
    private JButton btnClear;

    // Patient Profile Labels
    private JLabel lblPatientIdVal;
    private JLabel lblPatientNameVal;
    private JLabel lblAgeVal;
    private JLabel lblContactVal;
    private JLabel lblAddressVal;
    private JLabel lblMedicalHistoryVal;
    private JLabel lblTotalVisitsVal;
    private JLabel lblTotalSpentVal;

    // History Table
    private JTable tblHistory;
    private DefaultTableModel historyModel;

    // Action Buttons
    private JButton btnBookNewAppointment;
    private JButton btnBillSelected;
    private JButton btnCancelSelected;
    private JButton btnPrintHistory;
    private JButton btnBack;

    public PatientHistoryView(User user) {
        this(user, -1);
    }

    public PatientHistoryView(User user, int preloadedPatientId) {
        this.currentUser = user;
        this.patientDAO = new PatientDAO();
        this.appointmentService = new AppointmentService();
        initializeUI();
        loadPatientDropdown();

        if (preloadedPatientId > 0) {
            selectPatientById(preloadedPatientId);
        } else if (cmbQuickSelect.getItemCount() > 0) {
            cmbQuickSelect.setSelectedIndex(0);
        }
    }

    private void initializeUI() {
        setTitle("Sunrise Dental Clinic - Patient Search & Medical History");
        setSize(1060, 740);
        setMinimumSize(new Dimension(880, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setLayout(new BorderLayout(10, 10));

        // Reusable Menu Bar
        setJMenuBar(AppMenuBar.createMenuBar(this, currentUser));

        // ------------------ Top Header Banner ------------------
        JPanel headerPanel = UIHelper.createHeaderBanner(
            "PATIENT SEARCH & MEDICAL HISTORY",
            "Lookup patient profiles, medical notes, appointment timeline, and manage bookings",
            currentUser
        );
        add(headerPanel, BorderLayout.NORTH);

        // ------------------ Center Split Panel ------------------
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Top Search & Quick Selection Bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        searchBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Search Patient (By Name, Phone, Patient ID, or Appt #)"),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));

        JLabel lblSelect = new JLabel("Quick Patient List:");
        lblSelect.setFont(UIHelper.FONT_BOLD);
        cmbQuickSelect = new JComboBox<>();
        cmbQuickSelect.setPreferredSize(new Dimension(280, 28));
        cmbQuickSelect.setFont(UIHelper.FONT_REGULAR);

        JLabel lblOr = new JLabel("OR Search:");
        lblOr.setFont(UIHelper.FONT_BOLD);
        txtSearch = new JTextField(16);
        txtSearch.setFont(UIHelper.FONT_REGULAR);

        btnSearch = UIHelper.createPrimaryButton("Search", new Dimension(85, 28));
        btnClear = UIHelper.createSecondaryButton("Clear", new Dimension(75, 28));
        btnReloadPatients = UIHelper.createSecondaryButton("Refresh List", new Dimension(105, 28));

        searchBar.add(lblSelect);
        searchBar.add(cmbQuickSelect);
        searchBar.add(lblOr);
        searchBar.add(txtSearch);
        searchBar.add(btnSearch);
        searchBar.add(btnClear);
        searchBar.add(btnReloadPatients);
        centerPanel.add(searchBar, BorderLayout.NORTH);

        // Main Body Split: Left = Patient Profile Card, Right = Appointment History Table
        JPanel leftProfilePanel = new JPanel(new GridBagLayout());
        leftProfilePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Patient Demographic & Clinical Profile"),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        leftProfilePanel.setPreferredSize(new Dimension(340, 420));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        lblPatientIdVal = addProfileRow(leftProfilePanel, gbc, row++, "Patient ID:", "-");
        lblPatientNameVal = addProfileRow(leftProfilePanel, gbc, row++, "Full Name:", "-");
        lblAgeVal = addProfileRow(leftProfilePanel, gbc, row++, "Age:", "-");
        lblContactVal = addProfileRow(leftProfilePanel, gbc, row++, "Contact No:", "-");
        lblAddressVal = addProfileRow(leftProfilePanel, gbc, row++, "Address:", "-");
        lblMedicalHistoryVal = addProfileRow(leftProfilePanel, gbc, row++, "Medical Notes:", "-");
        lblTotalVisitsVal = addProfileRow(leftProfilePanel, gbc, row++, "Total Appointments:", "0");
        lblTotalSpentVal = addProfileRow(leftProfilePanel, gbc, row++, "Total Billed Spent:", "LKR 0.00");

        // Right Side: Appointments Table
        JPanel rightHistoryPanel = new JPanel(new BorderLayout(5, 5));
        rightHistoryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Appointment History & Timeline Records"),
            BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));

        String[] cols = {"Appt #", "Date", "Time", "Dentist", "Treatment Procedure", "Total Bill (LKR)", "Status", "Cancellation Reason"};
        historyModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblHistory = new JTable(historyModel);
        tblHistory.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblHistory.setRowHeight(25);
        tblHistory.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblHistory.getTableHeader().setFont(UIHelper.FONT_BOLD);
        tblHistory.getTableHeader().setBackground(new Color(235, 242, 250));

        // Column widths
        tblHistory.getColumnModel().getColumn(0).setPreferredWidth(55);
        tblHistory.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblHistory.getColumnModel().getColumn(2).setPreferredWidth(70);
        tblHistory.getColumnModel().getColumn(3).setPreferredWidth(140);
        tblHistory.getColumnModel().getColumn(4).setPreferredWidth(140);
        tblHistory.getColumnModel().getColumn(5).setPreferredWidth(95);
        tblHistory.getColumnModel().getColumn(6).setPreferredWidth(75);
        tblHistory.getColumnModel().getColumn(7).setPreferredWidth(120);

        // Status Renderer
        tblHistory.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                String st = String.valueOf(value);
                if ("Billed".equalsIgnoreCase(st)) {
                    setForeground(new Color(0, 130, 0));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if ("Cancelled".equalsIgnoreCase(st)) {
                    setForeground(new Color(190, 25, 25));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    setForeground(new Color(210, 100, 0));
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                return comp;
            }
        });

        rightHistoryPanel.add(new JScrollPane(tblHistory), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(leftProfilePanel), rightHistoryPanel);
        splitPane.setDividerLocation(340);
        splitPane.setResizeWeight(0.35);
        centerPanel.add(splitPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // ------------------ Bottom Action Buttons ------------------
        JPanel footerButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 12));

        btnBookNewAppointment = UIHelper.createPrimaryButton("+ Add New Appointment for Patient", new Dimension(265, 36));
        btnBillSelected = UIHelper.createSuccessButton("Calculate / Print Bill", new Dimension(170, 36));
        btnCancelSelected = UIHelper.createDangerButton("Cancel Appointment", new Dimension(160, 36));
        btnPrintHistory = UIHelper.createSecondaryButton("Print Patient Record", new Dimension(155, 36));
        btnBack = UIHelper.createSecondaryButton("Back to Dashboard", new Dimension(145, 36));

        footerButtons.add(btnBookNewAppointment);
        footerButtons.add(btnBillSelected);
        footerButtons.add(btnCancelSelected);
        footerButtons.add(btnPrintHistory);
        footerButtons.add(btnBack);
        add(footerButtons, BorderLayout.SOUTH);

        // ------------------ Event Listeners ------------------
        cmbQuickSelect.addActionListener(e -> {
            Patient p = (Patient) cmbQuickSelect.getSelectedItem();
            if (p != null) {
                displayPatient(p);
            }
        });

        btnSearch.addActionListener(e -> searchPatientOrAppointment());
        txtSearch.addActionListener(e -> searchPatientOrAppointment());

        btnClear.addActionListener(e -> clearSearch());

        btnReloadPatients.addActionListener(e -> loadPatientDropdown());

        btnBookNewAppointment.addActionListener(e -> {
            AddAppointmentView view = new AddAppointmentView(currentUser);
            if (currentPatient != null) {
                view.selectExistingPatient(currentPatient);
            }
            UIHelper.navigate(this, view);
        });

        btnBillSelected.addActionListener(e -> {
            int selectedRow = tblHistory.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an appointment from the history table first.", "No Appointment Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int apptNo = (int) historyModel.getValueAt(selectedRow, 0);
            UIHelper.navigate(this, new BillView(currentUser, apptNo));
        });

        btnCancelSelected.addActionListener(e -> cancelSelectedAppointment());

        btnPrintHistory.addActionListener(e -> printHistory());

        btnBack.addActionListener(e -> {
            UIHelper.navigate(this, new MainMenuView(currentUser));
        });
    }

    private JLabel addProfileRow(JPanel panel, GridBagConstraints gbc, int row, String label, String defaultVal) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.38;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIHelper.FONT_BOLD);
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.62;
        JLabel val = new JLabel(defaultVal);
        val.setFont(UIHelper.FONT_REGULAR);
        panel.add(val, gbc);
        return val;
    }

    private void loadPatientDropdown() {
        cmbQuickSelect.removeAllItems();
        List<Patient> list = patientDAO.getAllPatients();
        for (Patient p : list) {
            cmbQuickSelect.addItem(p);
        }
        if (!list.isEmpty()) {
            displayPatient(list.get(0));
        }
    }

    public void selectPatientById(int patientId) {
        for (int i = 0; i < cmbQuickSelect.getItemCount(); i++) {
            Patient p = cmbQuickSelect.getItemAt(i);
            if (p.getPatientId() == patientId) {
                cmbQuickSelect.setSelectedIndex(i);
                return;
            }
        }
        try {
            Patient p = patientDAO.getPatientById(patientId);
            if (p != null) {
                displayPatient(p);
            }
        } catch (SQLException ignore) {}
    }

    private void searchPatientOrAppointment() {
        String query = txtSearch.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Patient Name, Phone Number, Patient ID, or Appointment Number.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 1. Check if user entered an Appointment Number (e.g. "1001" or "APT-1001")
            String cleanQuery = query.replaceAll("(?i)^APT-", "").trim();
            if (cleanQuery.matches("^\\d+$")) {
                int numericId = Integer.parseInt(cleanQuery);
                // Check if it matches an Appointment
                Appointment appt = appointmentService.getAppointmentByNumber(numericId);
                if (appt != null) {
                    // Find or create patient record for this appointment
                    Patient patient = null;
                    if (appt.getPatientId() != null && appt.getPatientId() > 0) {
                        patient = patientDAO.getPatientById(appt.getPatientId());
                    }
                    if (patient == null) {
                        List<Patient> matched = patientDAO.searchPatients(appt.getPatientName());
                        if (!matched.isEmpty()) {
                            patient = matched.get(0);
                        } else {
                            patient = new Patient(0, appt.getPatientName(), appt.getPatientAge(), "Other", appt.getContactNumber(), appt.getAddress(), "None");
                        }
                    }
                    displayPatient(patient);

                    // Highlight the specific appointment in the history table
                    for (int i = 0; i < tblHistory.getRowCount(); i++) {
                        if ((int) historyModel.getValueAt(i, 0) == numericId) {
                            tblHistory.setRowSelectionInterval(i, i);
                            tblHistory.scrollRectToVisible(tblHistory.getCellRect(i, 0, true));
                            break;
                        }
                    }
                    return;
                }

                // If not an appointment, check if it matches a Patient ID
                Patient p = patientDAO.getPatientById(numericId);
                if (p != null) {
                    displayPatient(p);
                    return;
                }
            }

            // 2. Search by Patient Name or Phone
            List<Patient> results = patientDAO.searchPatients(query);
            if (results.isEmpty()) {
                // Also search appointments by name to see if we can find any matching patient
                List<Appointment> apptResults = appointmentService.searchAppointments(query);
                if (!apptResults.isEmpty()) {
                    Appointment first = apptResults.get(0);
                    Patient p = new Patient(0, first.getPatientName(), first.getPatientAge(), "Other", first.getContactNumber(), first.getAddress(), "None");
                    displayPatient(p);
                    return;
                }
                JOptionPane.showMessageDialog(this, "No patient or appointment record found matching: '" + query + "'", "Not Found", JOptionPane.INFORMATION_MESSAGE);
            } else if (results.size() == 1) {
                displayPatient(results.get(0));
            } else {
                cmbQuickSelect.removeAllItems();
                for (Patient p : results) {
                    cmbQuickSelect.addItem(p);
                }
                displayPatient(results.get(0));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayPatient(Patient p) {
        this.currentPatient = p;
        lblPatientIdVal.setText(p.getPatientId() > 0 ? "PAT-" + p.getPatientId() : "Auto-Linked");
        lblPatientNameVal.setText(p.getPatientName());
        lblAgeVal.setText(p.getAge() != null ? p.getAge() : "1 Month");
        lblContactVal.setText(p.getContactNumber());
        lblAddressVal.setText(p.getAddress());
        lblMedicalHistoryVal.setText((p.getMedicalHistory() != null && !p.getMedicalHistory().trim().isEmpty()) ? p.getMedicalHistory() : "None recorded");

        // Load Appointment History for this Patient
        historyModel.setRowCount(0);
        try {
            List<Appointment> appts = appointmentService.getAppointmentsByPatientNameOrPhone(p.getPatientName(), p.getContactNumber());
            lblTotalVisitsVal.setText(String.valueOf(appts.size()) + " appointment(s)");

            double totalSpent = 0.0;
            for (Appointment a : appts) {
                if ("Billed".equalsIgnoreCase(a.getStatus())) {
                    totalSpent += a.getTotalBill();
                }
                historyModel.addRow(new Object[]{
                    a.getAppointmentNumber(),
                    a.getAppointmentDate(),
                    a.getAppointmentTime(),
                    a.getDentistName(),
                    a.getTreatmentType(),
                    String.format("%,.2f", a.getTotalBill()),
                    a.getStatus(),
                    a.getCancellationReason() != null ? a.getCancellationReason() : "-"
                });
            }
            lblTotalSpentVal.setText(String.format("LKR %,.2f", totalSpent));
        } catch (SQLException ex) {
            System.err.println("Error loading patient history: " + ex.getMessage());
        }
    }

    private void cancelSelectedAppointment() {
        int selectedRow = tblHistory.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment from the table to cancel.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int apptNo = (int) historyModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) historyModel.getValueAt(selectedRow, 6);

        if ("Cancelled".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this, "This appointment is already cancelled.", "Already Cancelled", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to CANCEL Appointment #" + apptNo + "?\n\n" +
            "This will free up the dentist's time slot immediately for new bookings.",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            String reason = JOptionPane.showInputDialog(
                this,
                "Optional: Enter cancellation reason (e.g. Patient requested reschedule):",
                "Cancellation Reason",
                JOptionPane.PLAIN_MESSAGE
            );

            try {
                boolean success = appointmentService.cancelAppointment(apptNo, reason);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Appointment #" + apptNo + " has been cancelled.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    if (currentPatient != null) {
                        displayPatient(currentPatient);
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearSearch() {
        txtSearch.setText("");
        loadPatientDropdown();
    }

    private void printHistory() {
        if (currentPatient == null) return;
        try {
            boolean complete = tblHistory.print(
                JTable.PrintMode.FIT_WIDTH,
                new java.text.MessageFormat("Sunrise Dental Clinic - Patient Record: " + currentPatient.getPatientName() + " (Age: " + currentPatient.getAge() + ")"),
                new java.text.MessageFormat("Page {0}")
            );
            if (complete) {
                JOptionPane.showMessageDialog(this, "Patient record printed successfully.", "Print Job Done", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Print Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

