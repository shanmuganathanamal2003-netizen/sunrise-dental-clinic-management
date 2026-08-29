package org.example.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.example.model.Appointment;
import org.example.model.User;
import org.example.service.AppointmentService;
import org.example.view.components.AppMenuBar;
import org.example.view.components.UIHelper;

/**
 * MainMenuView - Interactive Dashboard & Menu-Driven Hub
 * 
 * Provides quick-access navigation to all core modules and recent appointments overview.
 */
public class MainMenuView extends JFrame {

    private final User currentUser;
    private final AppointmentService appointmentService;

    // Recent Appointments Table
    private JTable tblRecent;
    private DefaultTableModel recentModel;

    public MainMenuView(User user) {
        this.currentUser = user;
        this.appointmentService = new AppointmentService();
        initializeUI();
        loadRecentAppointments();
    }

    private void initializeUI() {
        setTitle("Sunrise Dental Clinic - Main Dashboard & Management Hub");
        setSize(1040, 700);
        setMinimumSize(new Dimension(880, 580));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(true);
        setLayout(new BorderLayout(10, 10));

        // Reusable Top Menu Bar
        setJMenuBar(AppMenuBar.createMenuBar(this, currentUser));

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                confirmExit();
            }
        });

        // ------------------ Header Panel ------------------
        JPanel headerPanel = UIHelper.createHeaderBanner(
            "SUNRISE DENTAL CLINIC MANAGEMENT DASHBOARD",
            "Main Control Center (" + currentUser.getRole() + " Portal) - Colombo, Sri Lanka",
            currentUser
        );
        add(headerPanel, BorderLayout.NORTH);

        // ------------------ Center Dashboard Content ------------------
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Left Side: Action Buttons Grid
        JPanel actionMenuPanel = buildActionMenuPanel();

        // Right Side: Recent Appointments Table Preview
        String role = currentUser.getRole() != null ? currentUser.getRole() : "Receptionist";
        boolean isDoctor = "Doctor".equalsIgnoreCase(role);

        JPanel recentPanel = new JPanel(new BorderLayout(8, 8));
        recentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(isDoctor ? "My Assigned Appointments Overview" : "Recent Clinic Appointments Overview"),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        String[] cols = isDoctor 
            ? new String[]{"Appt #", "Patient Name", "Age", "Date", "Time", "Status"}
            : new String[]{"Appt #", "Patient Name", "Age", "Doctor", "Date", "Status"};

        recentModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblRecent = new JTable(recentModel);
        tblRecent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblRecent.setRowHeight(26);
        tblRecent.getTableHeader().setFont(UIHelper.FONT_BOLD);
        tblRecent.getTableHeader().setBackground(new Color(235, 242, 250));

        // Column widths
        tblRecent.getColumnModel().getColumn(0).setPreferredWidth(55);
        tblRecent.getColumnModel().getColumn(1).setPreferredWidth(140);
        tblRecent.getColumnModel().getColumn(2).setPreferredWidth(75);
        tblRecent.getColumnModel().getColumn(3).setPreferredWidth(140);
        tblRecent.getColumnModel().getColumn(4).setPreferredWidth(85);
        tblRecent.getColumnModel().getColumn(5).setPreferredWidth(80);

        // Status renderer
        tblRecent.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
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

        recentPanel.add(new JScrollPane(tblRecent), BorderLayout.CENTER);

        // Split Layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, actionMenuPanel, recentPanel);
        splitPane.setDividerLocation(380);
        splitPane.setResizeWeight(0.35);
        centerPanel.add(splitPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // ------------------ Bottom Footer ------------------
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));

        JButton btnHelp = UIHelper.createSecondaryButton("System Help Guide", new Dimension(160, 34));
        JButton btnRefresh = UIHelper.createSecondaryButton("Refresh Overview", new Dimension(160, 34));
        JButton btnLogout = UIHelper.createSecondaryButton("Logout", new Dimension(110, 34));
        JButton btnExit = UIHelper.createDangerButton("Exit System", new Dimension(120, 34));

        footerPanel.add(btnHelp);
        footerPanel.add(btnRefresh);
        footerPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        footerPanel.add(btnLogout);
        footerPanel.add(btnExit);

        add(footerPanel, BorderLayout.SOUTH);

        btnHelp.addActionListener(e -> new HelpView(MainMenuView.this).setVisible(true));
        btnRefresh.addActionListener(e -> loadRecentAppointments());

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                UIHelper.navigate(this, new LoginView());
            }
        });

        btnExit.addActionListener(e -> confirmExit());
    }

    private JPanel buildActionMenuPanel() {
        String role = currentUser.getRole() != null ? currentUser.getRole() : "Receptionist";
        boolean isAdmin = "Admin".equalsIgnoreCase(role);
        boolean isDoctor = "Doctor".equalsIgnoreCase(role);

        JPanel actionMenuPanel;
        if (isAdmin) {
            actionMenuPanel = new JPanel(new GridLayout(6, 1, 10, 10));
            actionMenuPanel.setPreferredSize(new Dimension(360, 430));

            JButton btnAddAppt = createActionButton("1. Add New Appointment (New / Old Patient)");
            JButton btnViewAll = createActionButton("2. View All Confirmed Appointments");
            JButton btnPatientSearchHistory = createActionButton("3. Patient Search & Medical History");
            JButton btnBill = createActionButton("4. Calculate & Print Patient Bill");
            JButton btnReports = createActionButton("5. Management & Revenue Reports");
            JButton btnManageStaff = createActionButton("6. Manage Staff Accounts (Admin)");

            btnAddAppt.addActionListener(e -> UIHelper.navigate(this, new AddAppointmentView(currentUser)));
            btnViewAll.addActionListener(e -> UIHelper.navigate(this, new AppointmentListView(currentUser)));
            btnPatientSearchHistory.addActionListener(e -> UIHelper.navigate(this, new PatientHistoryView(currentUser)));
            btnBill.addActionListener(e -> UIHelper.navigate(this, new BillView(currentUser, -1)));
            btnReports.addActionListener(e -> UIHelper.navigate(this, new ReportsView(currentUser)));
            btnManageStaff.addActionListener(e -> UIHelper.navigate(this, new RegisterUserView(currentUser)));

            actionMenuPanel.add(btnAddAppt);
            actionMenuPanel.add(btnViewAll);
            actionMenuPanel.add(btnPatientSearchHistory);
            actionMenuPanel.add(btnBill);
            actionMenuPanel.add(btnReports);
            actionMenuPanel.add(btnManageStaff);
        } else if (isDoctor) {
            actionMenuPanel = new JPanel(new GridLayout(2, 1, 10, 10));
            actionMenuPanel.setPreferredSize(new Dimension(360, 160));

            JButton btnDoctorQueue = createActionButton("1. My Patient Schedule & Clinical Queue");
            JButton btnPatientHistory = createActionButton("2. Patient Medical History (View Only)");

            btnDoctorQueue.addActionListener(e -> UIHelper.navigate(this, new DoctorQueueView(currentUser)));
            btnPatientHistory.addActionListener(e -> UIHelper.navigate(this, new PatientHistoryView(currentUser)));

            actionMenuPanel.add(btnDoctorQueue);
            actionMenuPanel.add(btnPatientHistory);
        } else {
            // Receptionist Role
            actionMenuPanel = new JPanel(new GridLayout(4, 1, 10, 10));
            actionMenuPanel.setPreferredSize(new Dimension(360, 310));

            JButton btnAddAppt = createActionButton("1. Add New Appointment (New / Old Patient)");
            JButton btnViewAll = createActionButton("2. View All Confirmed Appointments");
            JButton btnPatientSearchHistory = createActionButton("3. Patient Search & Medical History");
            JButton btnBill = createActionButton("4. Calculate & Print Patient Bill");

            btnAddAppt.addActionListener(e -> UIHelper.navigate(this, new AddAppointmentView(currentUser)));
            btnViewAll.addActionListener(e -> UIHelper.navigate(this, new AppointmentListView(currentUser)));
            btnPatientSearchHistory.addActionListener(e -> UIHelper.navigate(this, new PatientHistoryView(currentUser)));
            btnBill.addActionListener(e -> UIHelper.navigate(this, new BillView(currentUser, -1)));

            actionMenuPanel.add(btnAddAppt);
            actionMenuPanel.add(btnViewAll);
            actionMenuPanel.add(btnPatientSearchHistory);
            actionMenuPanel.add(btnBill);
        }

        actionMenuPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Quick Action Navigation Menu (" + role + ")"),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        return actionMenuPanel;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UIHelper.FONT_BOLD);
        button.setFocusPainted(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        button.setBackground(new Color(245, 248, 252));
        button.setForeground(UIHelper.COLOR_PRIMARY);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 205, 230), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        return button;
    }

    private void loadRecentAppointments() {
        recentModel.setRowCount(0);
        String role = currentUser.getRole() != null ? currentUser.getRole() : "Receptionist";
        boolean isDoctor = "Doctor".equalsIgnoreCase(role);

        try {
            List<Appointment> all;
            if (isDoctor) {
                all = appointmentService.getAppointmentsForDoctor(currentUser.getUsername());
            } else {
                all = appointmentService.getAllAppointments();
            }

            int limit = Math.min(all.size(), 12);
            for (int i = 0; i < limit; i++) {
                Appointment a = all.get(i);
                if (isDoctor) {
                    recentModel.addRow(new Object[]{
                        a.getAppointmentNumber(),
                        a.getPatientName(),
                        a.getPatientAge() != null ? a.getPatientAge() : "1 Month",
                        a.getAppointmentDate(),
                        a.getAppointmentTime(),
                        a.getStatus()
                    });
                } else {
                    recentModel.addRow(new Object[]{
                        a.getAppointmentNumber(),
                        a.getPatientName(),
                        a.getPatientAge() != null ? a.getPatientAge() : "1 Month",
                        a.getDentistName(),
                        a.getAppointmentDate(),
                        a.getStatus()
                    });
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error loading recent appointments: " + ex.getMessage());
        }
    }

    private void confirmExit() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to exit Sunrise Dental Clinic Management System?",
            "Exit Confirmation",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}

