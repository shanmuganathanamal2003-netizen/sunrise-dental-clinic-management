package org.example.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.print.PrinterException;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.example.model.Appointment;
import org.example.model.User;
import org.example.service.AppointmentService;
import org.example.service.BillingService;
import org.example.view.components.AppMenuBar;
import org.example.view.components.UIHelper;

/**
 * BillView - Calculate & Print Patient Invoices & Receipts
 * 
 * Allows staff to lookup an appointment by Patient Name or ID, calculate fees,
 * preview formatted receipt, save to database, and print/export to PDF.
 */
public class BillView extends JFrame {

    private final User currentUser;
    private final AppointmentService appointmentService;
    private final BillingService billingService;
    private Appointment currentAppointment;

    // Search Controls
    private JTextField txtSearchInput;
    private JButton btnFetch;
    private JButton btnBrowseTable;

    // Appointment Summary Labels
    private JLabel lblPatientVal;
    private JLabel lblAgeVal;
    private JLabel lblDentistVal;
    private JLabel lblTreatmentVal;
    private JLabel lblDateTimeVal;

    // Fee & Calculation Inputs
    private JTextField txtConsultFee;
    private JTextField txtTreatmentCost;
    private JLabel lblCalculatedTotal;
    private JButton btnCalculate;

    // Receipt Preview & Print Controls
    private JTextArea txtReceiptArea;
    private JButton btnPatientHistory;
    private JButton btnPrint;
    private JButton btnSaveBill;
    private JButton btnBack;

    public BillView(User user, int preloadedApptNumber) {
        this.currentUser = user;
        this.appointmentService = new AppointmentService();
        this.billingService = new BillingService();
        initializeUI();

        if (preloadedApptNumber > 0) {
            txtSearchInput.setText(String.valueOf(preloadedApptNumber));
            fetchAppointmentData(txtSearchInput.getText().trim());
        }
    }

    private void initializeUI() {
        setTitle("Sunrise Dental Clinic - Calculate & Print Patient Bill");
        setSize(960, 740);
        setMinimumSize(new Dimension(840, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setLayout(new BorderLayout(10, 10));

        // Reusable Top Menu Bar
        setJMenuBar(AppMenuBar.createMenuBar(this, currentUser));

        // ------------------ Top Header Banner ------------------
        JPanel headerPanel = UIHelper.createHeaderBanner(
            "CALCULATE & PRINT PATIENT BILL",
            "Billing and Invoicing Department - Fee computation, database updates, and receipt printing",
            currentUser
        );
        add(headerPanel, BorderLayout.NORTH);

        // ------------------ Main Split Layout ------------------
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));

        // 1. Search Box
        JPanel lookupPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        lookupPanel.setBorder(BorderFactory.createTitledBorder("Step 1: Select Patient / Appt #"));

        JLabel lblAppt = new JLabel("Patient Name / Appt #:");
        lblAppt.setFont(UIHelper.FONT_BOLD);
        txtSearchInput = new JTextField(12);
        txtSearchInput.setFont(UIHelper.FONT_REGULAR);

        btnFetch = UIHelper.createPrimaryButton("Load Details", new Dimension(110, 28));
        btnBrowseTable = UIHelper.createSecondaryButton("Browse Table", new Dimension(110, 28));

        lookupPanel.add(lblAppt);
        lookupPanel.add(txtSearchInput);
        lookupPanel.add(btnFetch);
        lookupPanel.add(btnBrowseTable);
        leftPanel.add(lookupPanel, BorderLayout.NORTH);

        // 2. Details & Fee Calculation Form
        JPanel calcFormPanel = new JPanel(new GridBagLayout());
        calcFormPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Step 2: Fee Breakdown & Calculation"),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        lblPatientVal = addInfoRow(calcFormPanel, gbc, row++, "Patient Name:", "-");
        lblAgeVal = addInfoRow(calcFormPanel, gbc, row++, "Patient Age:", "-");
        lblDentistVal = addInfoRow(calcFormPanel, gbc, row++, "Dentist:", "-");
        lblTreatmentVal = addInfoRow(calcFormPanel, gbc, row++, "Treatment:", "-");
        lblDateTimeVal = addInfoRow(calcFormPanel, gbc, row++, "Date & Time:", "-");

        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        calcFormPanel.add(new JLabel("--------------------------------------------------"), gbc);
        gbc.gridwidth = 1;

        // Fee Inputs
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblFeeTag = new JLabel("Consultation Fee (LKR):");
        lblFeeTag.setFont(UIHelper.FONT_BOLD);
        calcFormPanel.add(lblFeeTag, gbc);

        gbc.gridx = 1;
        txtConsultFee = new JTextField("1500.00", 10);
        txtConsultFee.setFont(UIHelper.FONT_REGULAR);
        calcFormPanel.add(txtConsultFee, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblCostTag = new JLabel("Treatment Cost (LKR):");
        lblCostTag.setFont(UIHelper.FONT_BOLD);
        calcFormPanel.add(lblCostTag, gbc);

        gbc.gridx = 1;
        txtTreatmentCost = new JTextField("0.00", 10);
        txtTreatmentCost.setFont(UIHelper.FONT_REGULAR);
        calcFormPanel.add(txtTreatmentCost, gbc);
        row++;

        // Calculate Button
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        btnCalculate = UIHelper.createPrimaryButton("Calculate Total Amount", new Dimension(220, 32));
        calcFormPanel.add(btnCalculate, gbc);
        gbc.gridwidth = 1;
        row++;

        // Total Amount Display
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblTotalTag = new JLabel("TOTAL BILL:");
        lblTotalTag.setFont(UIHelper.FONT_BOLD);
        lblTotalTag.setForeground(UIHelper.COLOR_PRIMARY);
        calcFormPanel.add(lblTotalTag, gbc);

        gbc.gridx = 1;
        lblCalculatedTotal = new JLabel("LKR 0.00");
        lblCalculatedTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCalculatedTotal.setForeground(new Color(180, 0, 0));
        calcFormPanel.add(lblCalculatedTotal, gbc);
        row++;

        leftPanel.add(calcFormPanel, BorderLayout.CENTER);

        // Right Side: Formatted Printable Receipt Area
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 15),
            BorderFactory.createTitledBorder("Step 3: Printable Receipt Preview")
        ));

        txtReceiptArea = new JTextArea();
        txtReceiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtReceiptArea.setEditable(false);
        txtReceiptArea.setBackground(new Color(254, 254, 254));
        txtReceiptArea.setText("\n  Please load an appointment and calculate total\n  to generate the official bill receipt.");

        JScrollPane scrollReceipt = new JScrollPane(txtReceiptArea);
        rightPanel.add(scrollReceipt, BorderLayout.CENTER);

        // Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(440);
        splitPane.setResizeWeight(0.45);
        add(splitPane, BorderLayout.CENTER);

        // ------------------ Bottom Action Buttons ------------------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));

        btnPatientHistory = UIHelper.createSecondaryButton("Patient History", new Dimension(150, 36));
        btnSaveBill = UIHelper.createSuccessButton("Save Bill to DB", new Dimension(150, 36));
        btnPrint = UIHelper.createPrimaryButton("Print / Save as PDF", new Dimension(170, 36));
        btnBack = UIHelper.createSecondaryButton("Back to Dashboard", new Dimension(150, 36));

        buttonPanel.add(btnPatientHistory);
        buttonPanel.add(btnSaveBill);
        buttonPanel.add(btnPrint);
        buttonPanel.add(btnBack);

        add(buttonPanel, BorderLayout.SOUTH);

        // ------------------ Action Listeners ------------------
        btnFetch.addActionListener(e -> {
            String text = txtSearchInput.getText().trim();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an Appointment Number or Patient Name.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            fetchAppointmentData(text);
        });

        btnBrowseTable.addActionListener(e -> {
            UIHelper.navigate(this, new AppointmentListView(currentUser));
        });

        txtSearchInput.addActionListener(e -> btnFetch.doClick());
        btnCalculate.addActionListener(e -> {
            if (calculateTotal() >= 0 && currentAppointment != null) {
                generateReceiptText();
            }
        });
        btnPatientHistory.addActionListener(e -> {
            if (currentAppointment != null && currentAppointment.getPatientId() != null && currentAppointment.getPatientId() > 0) {
                UIHelper.navigate(this, new PatientHistoryView(currentUser, currentAppointment.getPatientId()));
            } else {
                UIHelper.navigate(this, new PatientHistoryView(currentUser));
            }
        });
        btnSaveBill.addActionListener(e -> saveBillToDatabase());
        btnPrint.addActionListener(e -> printReceipt());

        btnBack.addActionListener(e -> {
            UIHelper.navigate(this, new MainMenuView(currentUser));
        });
    }

    private JLabel addInfoRow(JPanel panel, GridBagConstraints gbc, int row, String label, String defaultVal) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.4;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIHelper.FONT_REGULAR);
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.6;
        JLabel val = new JLabel(defaultVal);
        val.setFont(UIHelper.FONT_BOLD);
        panel.add(val, gbc);
        return val;
    }

    private void fetchAppointmentData(String query) {
        try {
            Appointment appt = null;
            if (query.matches("^\\d+$")) {
                int id = Integer.parseInt(query);
                appt = appointmentService.getAppointmentByNumber(id);
            }

            if (appt == null) {
                List<Appointment> results = appointmentService.searchAppointments(query);
                if (!results.isEmpty()) {
                    appt = results.get(0);
                }
            }

            if (appt != null) {
                this.currentAppointment = appt;
                lblPatientVal.setText(appt.getPatientName());
                lblAgeVal.setText(appt.getPatientAge() != null ? appt.getPatientAge() : "1 Month");
                lblDentistVal.setText(appt.getDentistName());
                lblTreatmentVal.setText(appt.getTreatmentType());
                lblDateTimeVal.setText(appt.getAppointmentDate() + " " + appt.getAppointmentTime());

                txtConsultFee.setText(String.format("%.2f", appt.getConsultationFee()));
                txtTreatmentCost.setText(String.format("%.2f", appt.getTreatmentCost()));
                
                double total = appt.getConsultationFee() + appt.getTreatmentCost();
                lblCalculatedTotal.setText(String.format("LKR %,.2f", total));

                generateReceiptText();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "No appointment found matching: " + query,
                    "Appointment Not Found",
                    JOptionPane.WARNING_MESSAGE
                );
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Database error while loading appointment:\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private double calculateTotal() {
        try {
            double fee = Double.parseDouble(txtConsultFee.getText().trim());
            double cost = Double.parseDouble(txtTreatmentCost.getText().trim());
            double total = billingService.calculateTotal(fee, cost);
            lblCalculatedTotal.setText(String.format("LKR %,.2f", total));
            return total;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
            return -1;
        }
    }

    private void generateReceiptText() {
        if (currentAppointment == null) {
            JOptionPane.showMessageDialog(this, "Please load an appointment first.", "No Appointment Loaded", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total = calculateTotal();
        if (total < 0) return;

        double fee = Double.parseDouble(txtConsultFee.getText().trim());
        double cost = Double.parseDouble(txtTreatmentCost.getText().trim());

        String receipt = billingService.generateReceipt(currentAppointment, fee, cost, currentUser);
        txtReceiptArea.setText(receipt);
        txtReceiptArea.setCaretPosition(0);
        billingService.printReceiptToConsole(currentAppointment, fee, cost, currentUser);
    }

    private void saveBillToDatabase() {
        if (currentAppointment == null) {
            JOptionPane.showMessageDialog(this, "Please load an appointment first.", "No Appointment", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total = calculateTotal();
        if (total < 0) return;

        double fee = Double.parseDouble(txtConsultFee.getText().trim());
        double cost = Double.parseDouble(txtTreatmentCost.getText().trim());

        try {
            boolean updated = billingService.processBill(currentAppointment.getAppointmentNumber(), cost, fee);
            if (updated) {
                currentAppointment.setConsultationFee(fee);
                currentAppointment.setTreatmentCost(cost);
                currentAppointment.setTotalBill(total);
                currentAppointment.setStatus("Billed");

                System.out.println("[Billing] Bill saved to database for Appointment #" + currentAppointment.getAppointmentNumber() + ", Total: LKR " + total);

                JOptionPane.showMessageDialog(
                    this,
                    "Billing details successfully updated and saved in MySQL database!\nTotal: LKR " + total,
                    "Bill Saved",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Database error while updating billing:\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void printReceipt() {
        if (currentAppointment == null || txtReceiptArea.getText().trim().isEmpty() || txtReceiptArea.getText().contains("Please load an appointment")) {
            JOptionPane.showMessageDialog(this, "Please generate a receipt before printing.", "No Receipt Ready", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean complete = txtReceiptArea.print(
                new java.text.MessageFormat("Sunrise Dental Clinic - Official Receipt"),
                new java.text.MessageFormat("Page {0}")
            );
            if (complete) {
                JOptionPane.showMessageDialog(this, "Printing completed successfully.", "Print Job Done", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Printing error: " + ex.getMessage() + "\n(You can select 'Microsoft Print to PDF' to save as PDF)",
                "Print Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

