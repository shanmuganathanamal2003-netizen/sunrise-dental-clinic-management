package org.example.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.example.dao.AppointmentDAO;
import org.example.model.Appointment;
import org.example.model.User;

/**
 * SearchAppointmentView - Search and View Appointment Screen
 * 
 * Allows staff to look up any appointment by appointment number and view complete details.
 */
public class SearchAppointmentView extends JFrame {

    private User currentUser;
    private AppointmentDAO appointmentDAO;
    private Appointment currentAppointment;

    // Search Component
    private JTextField txtSearchNumber;
    private JButton btnSearch;

    // Detail Display Labels
    private JLabel lblValNumber;
    private JLabel lblValPatientName;
    private JLabel lblValContact;
    private JLabel lblValAddress;
    private JLabel lblValDentist;
    private JLabel lblValTreatment;
    private JLabel lblValDateTime;
    private JLabel lblValConsultFee;
    private JLabel lblValTreatCost;
    private JLabel lblValTotal;
    private JLabel lblValStatus;

    // Action Buttons
    private JButton btnProceedToBill;
    private JButton btnClear;
    private JButton btnBack;

    public SearchAppointmentView(User user) {
        this.currentUser = user;
        this.appointmentDAO = new AppointmentDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Sunrise Dental Clinic - Search / View Appointment");
        setSize(660, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        // ------------------ Header Panel ------------------
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(24, 90, 157));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("SEARCH / VIEW APPOINTMENT DETAILS", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Enter Appointment Number to retrieve records", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(220, 235, 252));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // ------------------ Center Content Panel ------------------
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        // Search Bar Panel
        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchBarPanel.setBorder(BorderFactory.createTitledBorder("Lookup Appointment"));

        JLabel lblSearch = new JLabel("Appointment Number:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));

        txtSearchNumber = new JTextField(12);
        txtSearchNumber.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnSearch = new JButton("Search");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSearch.setBackground(new Color(24, 90, 157));
        btnSearch.setForeground(Color.WHITE);

        searchBarPanel.add(lblSearch);
        searchBarPanel.add(txtSearchNumber);
        searchBarPanel.add(btnSearch);

        centerPanel.add(searchBarPanel, BorderLayout.NORTH);

        // Details Display Card
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Appointment Record Details"),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        lblValNumber = addDetailRow(detailsPanel, gbc, row++, "Appointment No:", "-");
        lblValPatientName = addDetailRow(detailsPanel, gbc, row++, "Patient Name:", "-");
        lblValContact = addDetailRow(detailsPanel, gbc, row++, "Contact Number:", "-");
        lblValAddress = addDetailRow(detailsPanel, gbc, row++, "Address:", "-");
        lblValDentist = addDetailRow(detailsPanel, gbc, row++, "Assigned Dentist:", "-");
        lblValTreatment = addDetailRow(detailsPanel, gbc, row++, "Treatment Type:", "-");
        lblValDateTime = addDetailRow(detailsPanel, gbc, row++, "Date & Time:", "-");
        lblValConsultFee = addDetailRow(detailsPanel, gbc, row++, "Consultation Fee:", "-");
        lblValTreatCost = addDetailRow(detailsPanel, gbc, row++, "Treatment Cost:", "-");
        lblValTotal = addDetailRow(detailsPanel, gbc, row++, "Total Bill Amount:", "-");
        lblValStatus = addDetailRow(detailsPanel, gbc, row++, "Billing Status:", "-");

        centerPanel.add(detailsPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // ------------------ Button Panel ------------------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));

        btnProceedToBill = new JButton("Calculate / Print Bill");
        btnProceedToBill.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnProceedToBill.setBackground(new Color(40, 140, 60));
        btnProceedToBill.setForeground(Color.WHITE);
        btnProceedToBill.setEnabled(false);

        btnClear = new JButton("Clear Search");
        btnClear.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        btnBack = new JButton("Back to Main Menu");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        buttonPanel.add(btnProceedToBill);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnBack);

        add(buttonPanel, BorderLayout.SOUTH);

        // ------------------ Listeners ------------------
        txtSearchNumber.addActionListener(e -> performSearch());
        btnSearch.addActionListener(e -> performSearch());

        btnClear.addActionListener(e -> clearDetails());

        btnProceedToBill.addActionListener(e -> {
            if (currentAppointment != null) {
                new BillView(currentUser, currentAppointment.getAppointmentNumber()).setVisible(true);
                dispose();
            }
        });

        btnBack.addActionListener(e -> {
            new MainMenuView(currentUser).setVisible(true);
            dispose();
        });
    }

    private JLabel addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, String defaultValue) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        JLabel val = new JLabel(defaultValue);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(val, gbc);

        return val;
    }

    private void performSearch() {
        String input = txtSearchNumber.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Appointment Number to search.", "Input Required", JOptionPane.WARNING_MESSAGE);
            txtSearchNumber.requestFocus();
            return;
        }

        int apptNo;
        try {
            apptNo = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric Appointment Number (e.g. 1001).", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            txtSearchNumber.selectAll();
            return;
        }

        try {
            Appointment appt = appointmentDAO.getAppointmentByNumber(apptNo);
            if (appt != null) {
                this.currentAppointment = appt;
                displayAppointment(appt);
                btnProceedToBill.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "No appointment found with Appointment Number: " + apptNo,
                    "Record Not Found",
                    JOptionPane.INFORMATION_MESSAGE
                );
                clearDetails();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Database error while searching appointment:\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void displayAppointment(Appointment appt) {
        lblValNumber.setText(String.valueOf(appt.getAppointmentNumber()));
        lblValPatientName.setText(appt.getPatientName());
        lblValContact.setText(appt.getContactNumber());
        lblValAddress.setText(appt.getAddress());
        lblValDentist.setText(appt.getDentistName());
        lblValTreatment.setText(appt.getTreatmentType());
        lblValDateTime.setText(appt.getAppointmentDate() + " at " + appt.getAppointmentTime());
        lblValConsultFee.setText("LKR " + String.format("%.2f", appt.getConsultationFee()));
        lblValTreatCost.setText("LKR " + String.format("%.2f", appt.getTreatmentCost()));
        lblValTotal.setText("LKR " + String.format("%.2f", appt.getTotalBill()));
        lblValStatus.setText(appt.getStatus());

        if ("Billed".equalsIgnoreCase(appt.getStatus())) {
            lblValStatus.setForeground(new Color(0, 120, 0));
        } else {
            lblValStatus.setForeground(new Color(180, 80, 0));
        }
    }

    private void clearDetails() {
        currentAppointment = null;
        txtSearchNumber.setText("");
        lblValNumber.setText("-");
        lblValPatientName.setText("-");
        lblValContact.setText("-");
        lblValAddress.setText("-");
        lblValDentist.setText("-");
        lblValTreatment.setText("-");
        lblValDateTime.setText("-");
        lblValConsultFee.setText("-");
        lblValTreatCost.setText("-");
        lblValTotal.setText("-");
        lblValStatus.setText("-");
        lblValStatus.setForeground(Color.BLACK);
        btnProceedToBill.setEnabled(false);
        txtSearchNumber.requestFocus();
    }
}
