package org.example.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import org.example.dao.AppointmentDAO;
import org.example.dao.ReportDAO;
import org.example.model.User;
import org.example.view.components.AppMenuBar;
import org.example.view.components.UIHelper;

/**
 * ReportsView - Executive Analytics & Decision-Making Reports
 * 
 * Provides financial revenue breakdown, doctor workload distributions,
 * and treatment service summaries for clinic administration.
 */
public class ReportsView extends JFrame {

    private final User currentUser;
    private final ReportDAO reportDAO;
    private final AppointmentDAO appointmentDAO;

    private JTabbedPane tabbedPane;
    private JTable tblDoctorWorkload;
    private JTable tblTreatmentSummary;
    private JLabel lblFinancialSummary;

    private JButton btnPrintReport;
    private JButton btnRefresh;
    private JButton btnBack;

    public ReportsView(User user) {
        this.currentUser = user;
        this.reportDAO = new ReportDAO();
        this.appointmentDAO = new AppointmentDAO();
        initializeUI();
        loadAllReports();
    }

    private void initializeUI() {
        setTitle("Sunrise Dental Clinic - Management Analytics & Reports");
        setSize(960, 680);
        setMinimumSize(new Dimension(840, 560));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setLayout(new BorderLayout(10, 10));

        // Reusable Menu Bar
        setJMenuBar(AppMenuBar.createMenuBar(this, currentUser));

        // ------------------ Top Header Banner ------------------
        JPanel headerPanel = UIHelper.createHeaderBanner(
            "CLINIC MANAGEMENT & DECISION-MAKING REPORTS",
            "Financial revenue breakdown, doctor workload, and treatment performance analytics",
            currentUser
        );
        add(headerPanel, BorderLayout.NORTH);

        // ------------------ Tabbed Pane ------------------
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIHelper.FONT_BOLD);

        // Tab 1: Financial Summary & Overview
        JPanel pnlFinancial = createFinancialPanel();
        tabbedPane.addTab("Financial & Revenue Summary", pnlFinancial);

        // Tab 2: Doctor Workload
        JPanel pnlWorkload = createWorkloadPanel();
        tabbedPane.addTab("Doctor Workload Analysis", pnlWorkload);

        // Tab 3: Treatment Popularity
        JPanel pnlTreatments = createTreatmentPanel();
        tabbedPane.addTab("Treatment Popularity Breakdown", pnlTreatments);

        add(tabbedPane, BorderLayout.CENTER);

        // ------------------ Bottom Buttons ------------------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));

        btnPrintReport = UIHelper.createPrimaryButton("Print Current Report", new Dimension(180, 36));
        btnRefresh = UIHelper.createSecondaryButton("Refresh Data", new Dimension(130, 36));
        btnBack = UIHelper.createSecondaryButton("Back to Dashboard", new Dimension(150, 36));

        buttonPanel.add(btnPrintReport);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnBack);
        add(buttonPanel, BorderLayout.SOUTH);

        // Listeners
        btnRefresh.addActionListener(e -> loadAllReports());
        btnPrintReport.addActionListener(e -> printCurrentTab());
        btnBack.addActionListener(e -> {
            UIHelper.navigate(this, new MainMenuView(currentUser));
        });
    }

    private JPanel createFinancialPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        lblFinancialSummary = new JLabel("<html>Loading financial data...</html>");
        lblFinancialSummary.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblFinancialSummary.setVerticalAlignment(SwingConstants.TOP);

        JScrollPane scrollPane = new JScrollPane(lblFinancialSummary);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Overall Revenue Summary"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createWorkloadPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] cols = {"Dentist / Surgeon Name", "Total Booked Appointments", "Completed / Billed", "Total Revenue Generated (LKR)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDoctorWorkload = new JTable(model);
        tblDoctorWorkload.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblDoctorWorkload.setRowHeight(28);
        tblDoctorWorkload.getTableHeader().setFont(UIHelper.FONT_BOLD);
        tblDoctorWorkload.getTableHeader().setBackground(new Color(235, 242, 250));

        panel.add(new JScrollPane(tblDoctorWorkload), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTreatmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] cols = {"Treatment Procedure", "Total Patient Bookings", "Total Procedure Fees (LKR)", "Total Consultation Fees (LKR)", "Gross Revenue (LKR)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTreatmentSummary = new JTable(model);
        tblTreatmentSummary.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblTreatmentSummary.setRowHeight(28);
        tblTreatmentSummary.getTableHeader().setFont(UIHelper.FONT_BOLD);
        tblTreatmentSummary.getTableHeader().setBackground(new Color(235, 242, 250));

        panel.add(new JScrollPane(tblTreatmentSummary), BorderLayout.CENTER);
        return panel;
    }

    private void loadAllReports() {
        // 1. Financial Overview
        Map<String, Object> stats = appointmentDAO.getDashboardStatistics();
        int totalAppts = (int) stats.getOrDefault("totalCount", 0);
        int todayAppts = (int) stats.getOrDefault("todayCount", 0);
        int scheduledAppts = (int) stats.getOrDefault("scheduledCount", 0);
        int billedAppts = (int) stats.getOrDefault("billedCount", 0);
        int cancelledAppts = (int) stats.getOrDefault("cancelledCount", 0);
        double totalRevenue = (double) stats.getOrDefault("totalRevenue", 0.0);

        String financialHtml = 
            "<html>" +
            "<body style='font-family: Segoe UI; padding: 10px;'>" +
            "<h2 style='color: #185a9d;'>Sunrise Dental Clinic - Revenue & Operations Report</h2>" +
            "<hr style='border: 0; border-top: 1px solid #cccccc;'><br>" +
            "<table cellpadding='8' style='font-size: 13pt; width: 100%;'>" +
            "<tr><td><b>Total Patient Appointments Booked:</b></td><td style='color: #185a9d;'><b>" + totalAppts + "</b></td></tr>" +
            "<tr><td><b>Today's Scheduled Appointments:</b></td><td style='color: #2e7d32;'><b>" + todayAppts + "</b></td></tr>" +
            "<tr><td><b>Pending / Scheduled Appointments:</b></td><td style='color: #ef6c00;'><b>" + scheduledAppts + "</b></td></tr>" +
            "<tr><td><b>Completed & Billed Appointments:</b></td><td style='color: #00897b;'><b>" + billedAppts + "</b></td></tr>" +
            "<tr><td><b>Cancelled Appointments:</b></td><td style='color: #c62828;'><b>" + cancelledAppts + "</b></td></tr>" +
            "<tr><td><b>Total Realized Revenue (Paid):</b></td><td style='color: #c62828; font-size: 16pt;'><b>LKR " + String.format("%,.2f", totalRevenue) + "</b></td></tr>" +
            "</table><br><br>" +
            "<div style='background: #f0f4f8; padding: 12px; border-left: 4px solid #185a9d; font-size: 11pt;'>" +
            "<b>Decision Making Insight:</b> Regular tracking of consultation vs procedure revenues allows clinic management to optimize dentist schedules, order surgical supplies proactively, and expand peak treatment hours." +
            "</div>" +
            "</body>" +
            "</html>";
        lblFinancialSummary.setText(financialHtml);

        // 2. Doctor Workload
        DefaultTableModel docModel = (DefaultTableModel) tblDoctorWorkload.getModel();
        docModel.setRowCount(0);
        List<Map<String, Object>> doctorData = reportDAO.getDoctorWorkloadReport();
        for (Map<String, Object> row : doctorData) {
            docModel.addRow(new Object[]{
                row.get("dentistName"),
                row.get("totalAppointments"),
                row.get("completedAppointments"),
                String.format("%,.2f", (Double) row.get("totalRevenue"))
            });
        }

        // 3. Treatment Summary
        DefaultTableModel treatModel = (DefaultTableModel) tblTreatmentSummary.getModel();
        treatModel.setRowCount(0);
        List<Map<String, Object>> treatData = reportDAO.getTreatmentSummaryReport();
        for (Map<String, Object> row : treatData) {
            treatModel.addRow(new Object[]{
                row.get("treatmentType"),
                row.get("appointmentCount"),
                String.format("%,.2f", (Double) row.get("totalTreatmentCost")),
                String.format("%,.2f", (Double) row.get("totalConsultFee")),
                String.format("%,.2f", (Double) row.get("grossTotal"))
            });
        }
    }

    private void printCurrentTab() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        try {
            if (selectedIndex == 1) {
                tblDoctorWorkload.print(JTable.PrintMode.FIT_WIDTH, new java.text.MessageFormat("Sunrise Dental - Doctor Workload Report"), new java.text.MessageFormat("Page {0}"));
            } else if (selectedIndex == 2) {
                tblTreatmentSummary.print(JTable.PrintMode.FIT_WIDTH, new java.text.MessageFormat("Sunrise Dental - Treatment Popularity Report"), new java.text.MessageFormat("Page {0}"));
            } else {
                JOptionPane.showMessageDialog(this, "To print detailed tables, please switch to the Workload or Treatment tab.", "Print Notice", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Print Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

