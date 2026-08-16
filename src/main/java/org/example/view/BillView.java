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
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.example.dao.AppointmentDAO;
import org.example.model.Appointment;
import org.example.model.User;

/**
 * BillView - Calculate and Print Bill Screen
 * 
 * Allows staff to calculate the total fee (Treatment Cost + Consultation Fee),
 * preview a formatted receipt, save the billing record to the database,
 * and print or export the receipt as PDF.
 */
public class BillView extends JFrame {

    private User currentUser;
    private AppointmentDAO appointmentDAO;
    private Appointment currentAppointment;

    // Search Controls
    private JTextField txtApptNumberInput;
    private JButton btnFetch;

    // Appointment Summary Labels
    private JLabel lblPatientVal;
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
    private JButton btnGenerateReceipt;
    private JButton btnPrint;
    private JButton btnSaveBill;
    private JButton btnBack;

    public BillView(User user, int preloadedApptNumber) {
        this.currentUser = user;
        this.appointmentDAO = new AppointmentDAO();
        initializeUI();

        if (preloadedApptNumber > 0) {
            txtApptNumberInput.setText(String.valueOf(preloadedApptNumber));
            fetchAppointmentData(preloadedApptNumber);
        }
    }

    private void initializeUI() {
        setTitle("Sunrise Dental Clinic - Calculate & Print Bill");
        setSize(850, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setLayout(new BorderLayout(10, 10));

        // ------------------ Header Panel ------------------
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(24, 90, 157));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("CALCULATE & PRINT PATIENT BILL", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Sunrise Dental Clinic - Billing and Invoicing Department", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(220, 235, 252));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // ------------------ Main Split Layout ------------------
        // Left Side: Inputs and Calculations
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));

        // 1. Search Box
        JPanel lookupPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        lookupPanel.setBorder(BorderFactory.createTitledBorder("Step 1: Select Appointment"));

        JLabel lblAppt = new JLabel("Appt No:");
        lblAppt.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtApptNumberInput = new JTextField(8);
        txtApptNumberInput.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnFetch = new JButton("Load Details");
        btnFetch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnFetch.setBackground(new Color(24, 90, 157));
        btnFetch.setForeground(Color.WHITE);

        lookupPanel.add(lblAppt);
        lookupPanel.add(txtApptNumberInput);
        lookupPanel.add(btnFetch);
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

        // Patient Info Rows
        lblPatientVal = addInfoRow(calcFormPanel, gbc, row++, "Patient Name:", "-");
        lblDentistVal = addInfoRow(calcFormPanel, gbc, row++, "Dentist:", "-");
        lblTreatmentVal = addInfoRow(calcFormPanel, gbc, row++, "Treatment:", "-");
        lblDateTimeVal = addInfoRow(calcFormPanel, gbc, row++, "Date & Time:", "-");

        // Separator
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        calcFormPanel.add(new JLabel("--------------------------------------------------"), gbc);
        gbc.gridwidth = 1;

        // Fee Inputs
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblFeeTag = new JLabel("Consultation Fee (LKR):");
        lblFeeTag.setFont(new Font("Segoe UI", Font.BOLD, 12));
        calcFormPanel.add(lblFeeTag, gbc);

        gbc.gridx = 1;
        txtConsultFee = new JTextField("1500.00", 10);
        txtConsultFee.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        calcFormPanel.add(txtConsultFee, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblCostTag = new JLabel("Treatment Cost (LKR):");
        lblCostTag.setFont(new Font("Segoe UI", Font.BOLD, 12));
        calcFormPanel.add(lblCostTag, gbc);

        gbc.gridx = 1;
        txtTreatmentCost = new JTextField("0.00", 10);
        txtTreatmentCost.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        calcFormPanel.add(txtTreatmentCost, gbc);
        row++;

        // Calculate Button
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        btnCalculate = new JButton("Calculate Total Amount");
        btnCalculate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCalculate.setBackground(new Color(240, 244, 250));
        calcFormPanel.add(btnCalculate, gbc);
        gbc.gridwidth = 1;
        row++;

        // Total Amount Display
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblTotalTag = new JLabel("TOTAL BILL:");
        lblTotalTag.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalTag.setForeground(new Color(24, 90, 157));
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
        splitPane.setDividerLocation(380);
        splitPane.setResizeWeight(0.45);
        add(splitPane, BorderLayout.CENTER);

        // ------------------ Bottom Action Buttons ------------------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));

        btnGenerateReceipt = new JButton("Generate Receipt");
        btnGenerateReceipt.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnSaveBill = new JButton("Save Bill to DB");
        btnSaveBill.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSaveBill.setBackground(new Color(40, 140, 60));
        btnSaveBill.setForeground(Color.WHITE);

        btnPrint = new JButton("Print / Save as PDF");
        btnPrint.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPrint.setBackground(new Color(24, 90, 157));
        btnPrint.setForeground(Color.WHITE);

        btnBack = new JButton("Back to Main Menu");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        buttonPanel.add(btnGenerateReceipt);
        buttonPanel.add(btnSaveBill);
        buttonPanel.add(btnPrint);
        buttonPanel.add(btnBack);

        add(buttonPanel, BorderLayout.SOUTH);

        // ------------------ Action Listeners ------------------
        btnFetch.addActionListener(e -> {
            String text = txtApptNumberInput.getText().trim();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an Appointment Number.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                int id = Integer.parseInt(text);
                fetchAppointmentData(id);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric Appointment Number.", "Invalid Number", JOptionPane.ERROR_MESSAGE);
            }
        });

        txtApptNumberInput.addActionListener(e -> btnFetch.doClick());

        btnCalculate.addActionListener(e -> calculateTotal());

        btnGenerateReceipt.addActionListener(e -> generateReceiptText());

        btnSaveBill.addActionListener(e -> saveBillToDatabase());

        btnPrint.addActionListener(e -> printReceipt());

        btnBack.addActionListener(e -> {
            new MainMenuView(currentUser).setVisible(true);
            dispose();
        });
    }

    private JLabel addInfoRow(JPanel panel, GridBagConstraints gbc, int row, String label, String defaultVal) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.4;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.6;
        JLabel val = new JLabel(defaultVal);
        val.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(val, gbc);
        return val;
    }

    private void fetchAppointmentData(int apptNo) {
        try {
            Appointment appt = appointmentDAO.getAppointmentByNumber(apptNo);
            if (appt != null) {
                this.currentAppointment = appt;
                lblPatientVal.setText(appt.getPatientName());
                lblDentistVal.setText(appt.getDentistName());
                lblTreatmentVal.setText(appt.getTreatmentType());
                lblDateTimeVal.setText(appt.getAppointmentDate() + " " + appt.getAppointmentTime());

                txtConsultFee.setText(String.format("%.2f", appt.getConsultationFee()));
                txtTreatmentCost.setText(String.format("%.2f", appt.getTreatmentCost()));
                
                double total = appt.getConsultationFee() + appt.getTreatmentCost();
                lblCalculatedTotal.setText(String.format("LKR %.2f", total));

                generateReceiptText();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "No appointment found with ID: " + apptNo,
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
            
            if (fee < 0 || cost < 0) {
                JOptionPane.showMessageDialog(this, "Fees cannot be negative values.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return -1;
            }

            double total = fee + cost;
            lblCalculatedTotal.setText(String.format("LKR %.2f", total));
            return total;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric fee amounts.", "Number Format Error", JOptionPane.ERROR_MESSAGE);
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

        String issuer = (currentUser != null) ? currentUser.getFullName() : "Authorized Staff";
        String printTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================================\n");
        sb.append("                SUNRISE DENTAL CLINIC                    \n");
        sb.append("         No. 45, Galle Road, Colombo 03, Sri Lanka        \n");
        sb.append("                Tel: +94 11 234 5678                     \n");
        sb.append("=========================================================\n");
        sb.append("               OFFICIAL PATIENT RECEIPT                  \n");
        sb.append("---------------------------------------------------------\n");
        sb.append(String.format(" Receipt Number  : REC-%04d\n", currentAppointment.getAppointmentNumber()));
        sb.append(String.format(" Appointment No : %d\n", currentAppointment.getAppointmentNumber()));
        sb.append(String.format(" Issued Date     : %s\n", printTimestamp));
        sb.append("---------------------------------------------------------\n");
        sb.append(String.format(" Patient Name    : %s\n", currentAppointment.getPatientName()));
        sb.append(String.format(" Contact Number  : %s\n", currentAppointment.getContactNumber()));
        sb.append(String.format(" Address         : %s\n", currentAppointment.getAddress()));
        sb.append(String.format(" Assigned Doctor : %s\n", currentAppointment.getDentistName()));
        sb.append(String.format(" Appt Date/Time  : %s (%s)\n", currentAppointment.getAppointmentDate(), currentAppointment.getAppointmentTime()));
        sb.append("---------------------------------------------------------\n");
        sb.append(String.format(" %-35s %18s\n", "ITEM / PROCEDURE DESCRIPTION", "AMOUNT (LKR)"));
        sb.append("---------------------------------------------------------\n");
        sb.append(String.format(" 1. Consultation Fee               %18.2f\n", fee));
        sb.append(String.format(" 2. %-30s %18.2f\n", truncate(currentAppointment.getTreatmentType(), 30), cost));
        sb.append("---------------------------------------------------------\n");
        sb.append(String.format(" GRAND TOTAL BILL (LKR)           %18.2f\n", total));
        sb.append("=========================================================\n");
        sb.append(" Payment Status : PAID IN FULL\n");
        sb.append(" Billed By      : " + issuer + "\n\n");
        sb.append("         Thank you for choosing Sunrise Dental!          \n");
        sb.append("     For emergency inquiries, call +94 11 234 5678       \n");
        sb.append("=========================================================\n");

        txtReceiptArea.setText(sb.toString());
        txtReceiptArea.setCaretPosition(0);
    }

    private String truncate(String val, int maxLen) {
        if (val == null) return "";
        if (val.length() <= maxLen) return val;
        return val.substring(0, maxLen - 3) + "...";
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
            boolean updated = appointmentDAO.updateAppointmentBilling(
                currentAppointment.getAppointmentNumber(),
                cost,
                fee,
                total
            );

            if (updated) {
                currentAppointment.setConsultationFee(fee);
                currentAppointment.setTreatmentCost(cost);
                currentAppointment.setTotalBill(total);
                currentAppointment.setStatus("Billed");

                JOptionPane.showMessageDialog(
                    this,
                    "Billing details successfully updated and saved in MySQL database!\nTotal: LKR " + String.format("%.2f", total),
                    "Bill Saved",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update bill record in database.", "Update Error", JOptionPane.ERROR_MESSAGE);
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
                new java.text.MessageFormat("Sunrise Dental Clinic - Patient Receipt"),
                new java.text.MessageFormat("Page - {0}")
            );
            if (complete) {
                JOptionPane.showMessageDialog(this, "Printing completed successfully.", "Print Job Done", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Printing error: " + ex.getMessage() + "\n(You can also choose 'Microsoft Print to PDF' to save as PDF file)",
                "Print Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
