package org.example.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.example.model.Appointment;
import org.example.model.User;
import org.example.service.AppointmentService;
import org.example.view.components.AppMenuBar;
import org.example.view.components.UIHelper;

/**
 * AppointmentListView - View All Confirmed Appointments & Interactive Patient Search
 * 
 * Shows all appointments in a searchable, filterable JTable with instant search,
 * cancellation management, billing, patient history inspection, and printing.
 */
public class AppointmentListView extends JFrame {

    private final User currentUser;
    private final AppointmentService appointmentService;

    // Search and Filter Controls
    private JTextField txtSearch;
    private JComboBox<String> cmbStatusFilter;
    private JButton btnSearch;
    private JButton btnReset;
    private JLabel lblTotalRecords;


    // Table
    private JTable tblAppointments;
    private DefaultTableModel tableModel;

    // Action Buttons
    private JButton btnBillSelected;
    private JButton btnCancelAppt;
    private JButton btnPatientHistory;
    private JButton btnNewAppt;
    private JButton btnBack;

    public AppointmentListView(User user) {
        this.currentUser = user;
        this.appointmentService = new AppointmentService();
        initializeUI();
        loadAppointments(null);
    }

    private void initializeUI() {
        setTitle("Sunrise Dental Clinic - View All Confirmed Appointments");
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
            "ALL CONFIRMED APPOINTMENTS & PATIENT RECORDS",
            "Search, manage bookings, cancel appointments, generate bills, or review patient history",
            currentUser
        );
        add(headerPanel, BorderLayout.NORTH);

        // ------------------ Center Content (Search & Table) ------------------
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Top Search and Filter Bar
        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        searchBarPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Search & Filter Records"),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        JLabel lblSearch = new JLabel("Search Name / Phone / Appt #:");
        lblSearch.setFont(UIHelper.FONT_BOLD);

        txtSearch = new JTextField(18);
        txtSearch.setFont(UIHelper.FONT_REGULAR);

        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setFont(UIHelper.FONT_BOLD);
        String[] statusOptions = {"All Statuses", "Scheduled", "Confirmed", "Billed", "Cancelled"};
        cmbStatusFilter = new JComboBox<>(statusOptions);
        cmbStatusFilter.setFont(UIHelper.FONT_REGULAR);

        btnSearch = UIHelper.createPrimaryButton("Search", new Dimension(85, 28));
        btnReset = UIHelper.createSecondaryButton("Reset / Refresh", new Dimension(120, 28));

        lblTotalRecords = new JLabel("Showing: 0 records");
        lblTotalRecords.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblTotalRecords.setForeground(new Color(80, 80, 80));

        searchBarPanel.add(lblSearch);
        searchBarPanel.add(txtSearch);
        searchBarPanel.add(lblStatus);
        searchBarPanel.add(cmbStatusFilter);
        searchBarPanel.add(btnSearch);
        searchBarPanel.add(btnReset);
        searchBarPanel.add(lblTotalRecords);

        centerPanel.add(searchBarPanel, BorderLayout.NORTH);

        // Interactive Table with Age column
        String[] columnNames = {
            "Appt #", "Patient Name", "Age", "Contact", "Assigned Dentist", "Treatment Type",
            "Date", "Time", "Total Bill (LKR)", "Status"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only cells
            }
        };

        tblAppointments = new JTable(tableModel);
        tblAppointments.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblAppointments.setRowHeight(26);
        tblAppointments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAppointments.getTableHeader().setFont(UIHelper.FONT_BOLD);
        tblAppointments.getTableHeader().setBackground(new Color(235, 242, 250));

        // Column Widths
        tblAppointments.getColumnModel().getColumn(0).setPreferredWidth(55);
        tblAppointments.getColumnModel().getColumn(1).setPreferredWidth(130);
        tblAppointments.getColumnModel().getColumn(2).setPreferredWidth(75);
        tblAppointments.getColumnModel().getColumn(3).setPreferredWidth(95);
        tblAppointments.getColumnModel().getColumn(4).setPreferredWidth(160);
        tblAppointments.getColumnModel().getColumn(5).setPreferredWidth(140);
        tblAppointments.getColumnModel().getColumn(6).setPreferredWidth(85);
        tblAppointments.getColumnModel().getColumn(7).setPreferredWidth(75);
        tblAppointments.getColumnModel().getColumn(8).setPreferredWidth(95);
        tblAppointments.getColumnModel().getColumn(9).setPreferredWidth(80);

        // Status Column Badge Renderer
        tblAppointments.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
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
                } else {
                    setForeground(new Color(210, 100, 0));
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                return c;
            }
        });

        JScrollPane tableScroll = new JScrollPane(tblAppointments);
        centerPanel.add(tableScroll, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // ------------------ Bottom Action Buttons ------------------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 12));

        btnBillSelected = UIHelper.createSuccessButton("Calculate / Print Bill", new Dimension(175, 36));
        btnCancelAppt = UIHelper.createDangerButton("Cancel Appointment", new Dimension(160, 36));
        btnPatientHistory = UIHelper.createSecondaryButton("Patient History", new Dimension(135, 36));
        btnNewAppt = UIHelper.createPrimaryButton("+ Add New Appointment", new Dimension(190, 36));
        btnBack = UIHelper.createSecondaryButton("Back to Dashboard", new Dimension(145, 36));

        buttonPanel.add(btnBillSelected);
        buttonPanel.add(btnCancelAppt);
        buttonPanel.add(btnPatientHistory);
        buttonPanel.add(btnNewAppt);
        buttonPanel.add(btnBack);

        add(buttonPanel, BorderLayout.SOUTH);

        // ------------------ Listeners ------------------
        btnSearch.addActionListener(e -> applyFilter());
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cmbStatusFilter.setSelectedIndex(0);
            loadAppointments(null);
        });

        // Instant search as user types
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void removeUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });

        cmbStatusFilter.addActionListener(e -> applyFilter());

        // Double click row opens Billing
        tblAppointments.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tblAppointments.getSelectedRow() != -1) {
                    openBillingForSelectedRow();
                }
            }
        });

        btnBillSelected.addActionListener(e -> openBillingForSelectedRow());

        btnCancelAppt.addActionListener(e -> cancelSelectedAppointment());

        btnPatientHistory.addActionListener(e -> {
            int selectedRow = tblAppointments.getSelectedRow();
            if (selectedRow != -1) {
                int apptNo = (int) tableModel.getValueAt(selectedRow, 0);
                PatientHistoryView view = new PatientHistoryView(currentUser);
                UIHelper.navigate(this, view);
            } else {
                UIHelper.navigate(this, new PatientHistoryView(currentUser));
            }
        });

        btnNewAppt.addActionListener(e -> {
            UIHelper.navigate(this, new AddAppointmentView(currentUser));
        });

        btnBack.addActionListener(e -> {
            UIHelper.navigate(this, new MainMenuView(currentUser));
        });
    }

    private void loadAppointments(String query) {
        tableModel.setRowCount(0);
        try {
            List<Appointment> list;
            if (query == null || query.trim().isEmpty()) {
                list = appointmentService.getAllAppointments();
            } else {
                list = appointmentService.searchAppointments(query);
            }

            String selectedStatus = (String) cmbStatusFilter.getSelectedItem();
            int count = 0;

            for (Appointment a : list) {
                if (selectedStatus != null && !"All Statuses".equalsIgnoreCase(selectedStatus)) {
                    if (!selectedStatus.equalsIgnoreCase(a.getStatus())) {
                        continue;
                    }
                }

                tableModel.addRow(new Object[]{
                    a.getAppointmentNumber(),
                    a.getPatientName(),
                    a.getPatientAge() != null ? a.getPatientAge() : "1 Month",
                    a.getContactNumber(),
                    a.getDentistName(),
                    a.getTreatmentType(),
                    a.getAppointmentDate(),
                    a.getAppointmentTime(),
                    String.format("%,.2f", a.getTotalBill()),
                    a.getStatus()
                });
                count++;
            }

            lblTotalRecords.setText("Showing: " + count + " records");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Database error loading appointments:\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void applyFilter() {
        String keyword = txtSearch.getText().trim();
        loadAppointments(keyword);
    }

    private void openBillingForSelectedRow() {
        int selectedRow = tblAppointments.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select an appointment from the table first.",
                "No Appointment Selected",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int apptNo = (int) tableModel.getValueAt(selectedRow, 0);
        UIHelper.navigate(this, new BillView(currentUser, apptNo));
    }

    private void cancelSelectedAppointment() {
        int selectedRow = tblAppointments.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select an appointment from the table to cancel.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int apptNo = (int) tableModel.getValueAt(selectedRow, 0);
        String patientName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 9);

        if ("Cancelled".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this, "This appointment is already cancelled.", "Already Cancelled", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to CANCEL Appointment #" + apptNo + " for patient '" + patientName + "'?\n\n" +
            "This will free up the dentist's time slot for new patient bookings.",
            "Confirm Appointment Cancellation",
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
                    JOptionPane.showMessageDialog(
                        this,
                        "Appointment #" + apptNo + " has been successfully CANCELLED.\nThe time slot is now available for new bookings.",
                        "Appointment Cancelled",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    applyFilter();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

