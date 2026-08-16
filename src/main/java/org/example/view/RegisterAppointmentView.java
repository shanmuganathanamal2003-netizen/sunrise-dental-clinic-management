package org.example.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.example.dao.AppointmentDAO;
import org.example.dao.TreatmentDAO;
import org.example.model.Appointment;
import org.example.model.Treatment;
import org.example.model.User;

/**
 * RegisterAppointmentView - New Appointment Registration Screen
 * 
 * Collects patient personal details, dentist selection, treatment choice,
 * appointment date/time, and registers the appointment with an auto-generated ID.
 */
public class RegisterAppointmentView extends JFrame {

    private User currentUser;
    private AppointmentDAO appointmentDAO;
    private TreatmentDAO treatmentDAO;

    // Form Components
    private JLabel lblNextApptNumber;
    private JTextField txtPatientName;
    private JTextField txtAddress;
    private JTextField txtContactNumber;
    private JComboBox<String> cmbDentist;
    private JComboBox<Treatment> cmbTreatment;
    private JTextField txtAppointmentDate;
    private JComboBox<String> cmbAppointmentTime;
    private JLabel lblCostSummary;

    private JButton btnSave;
    private JButton btnClear;
    private JButton btnBack;

    public RegisterAppointmentView(User user) {
        this.currentUser = user;
        this.appointmentDAO = new AppointmentDAO();
        this.treatmentDAO = new TreatmentDAO();
        initializeUI();
        loadInitialData();
    }

    private void initializeUI() {
        setTitle("Sunrise Dental Clinic - Register New Appointment");
        setSize(650, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        // ------------------ Header Panel ------------------
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(24, 90, 157));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("REGISTER NEW APPOINTMENT", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Enter patient details to schedule an appointment", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(220, 235, 252));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // ------------------ Form Panel ------------------
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 10, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Auto Generated Appointment Number Preview
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        JLabel lblApptTag = new JLabel("Appointment No (Auto):");
        lblApptTag.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblApptTag, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        lblNextApptNumber = new JLabel("Generating...");
        lblNextApptNumber.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNextApptNumber.setForeground(new Color(24, 90, 157));
        formPanel.add(lblNextApptNumber, gbc);
        row++;

        // Patient Name
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        JLabel lblName = new JLabel("Patient Full Name *:");
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(lblName, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        txtPatientName = new JTextField();
        txtPatientName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtPatientName, gbc);
        row++;

        // Patient Address
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblAddress = new JLabel("Residential Address *:");
        lblAddress.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(lblAddress, gbc);

        gbc.gridx = 1;
        txtAddress = new JTextField();
        txtAddress.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtAddress, gbc);
        row++;

        // Contact Number
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblContact = new JLabel("Contact Number *:");
        lblContact.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(lblContact, gbc);

        gbc.gridx = 1;
        txtContactNumber = new JTextField();
        txtContactNumber.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtContactNumber, gbc);
        row++;

        // Dentist Name Selection
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblDentist = new JLabel("Assigned Dentist *:");
        lblDentist.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(lblDentist, gbc);

        gbc.gridx = 1;
        String[] dentists = {
            "Dr. Samantha Perera (Senior Dental Surgeon)",
            "Dr. Nihal Silva (Orthodontist)",
            "Dr. Kasun Fernando (Endodontist)",
            "Dr. Anoma Wickramasinghe (General Dental Practitioner)"
        };
        cmbDentist = new JComboBox<>(dentists);
        cmbDentist.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(cmbDentist, gbc);
        row++;

        // Treatment Type Selection
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblTreatment = new JLabel("Treatment Type *:");
        lblTreatment.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(lblTreatment, gbc);

        gbc.gridx = 1;
        cmbTreatment = new JComboBox<>();
        cmbTreatment.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(cmbTreatment, gbc);
        row++;

        // Appointment Date
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblDate = new JLabel("Appointment Date (YYYY-MM-DD) *:");
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(lblDate, gbc);

        gbc.gridx = 1;
        txtAppointmentDate = new JTextField(LocalDate.now().toString());
        txtAppointmentDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtAppointmentDate, gbc);
        row++;

        // Appointment Time
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblTime = new JLabel("Appointment Time Slot *:");
        lblTime.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(lblTime, gbc);

        gbc.gridx = 1;
        String[] timeSlots = {
            "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM",
            "11:00 AM", "11:30 AM", "02:00 PM", "02:30 PM",
            "03:00 PM", "03:30 PM", "04:00 PM", "04:30 PM", "05:00 PM"
        };
        cmbAppointmentTime = new JComboBox<>(timeSlots);
        cmbAppointmentTime.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(cmbAppointmentTime, gbc);
        row++;

        // Estimated Fee / Cost Display
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblEst = new JLabel("Fee Summary:");
        lblEst.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblEst, gbc);

        gbc.gridx = 1;
        lblCostSummary = new JLabel("Consultation: LKR 1500.00 | Treatment: LKR 0.00");
        lblCostSummary.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblCostSummary.setForeground(new Color(60, 60, 60));
        formPanel.add(lblCostSummary, gbc);

        add(formPanel, BorderLayout.CENTER);

        // ------------------ Button Panel ------------------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));

        btnSave = new JButton("Save & Book Appointment");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setBackground(new Color(24, 90, 157));
        btnSave.setForeground(Color.WHITE);
        btnSave.setPreferredSize(new Dimension(210, 36));

        btnClear = new JButton("Clear Form");
        btnClear.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnClear.setPreferredSize(new Dimension(110, 36));

        btnBack = new JButton("Back to Main Menu");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnBack.setPreferredSize(new Dimension(160, 36));

        buttonPanel.add(btnSave);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnBack);

        add(buttonPanel, BorderLayout.SOUTH);

        // ------------------ Event Listeners ------------------
        cmbTreatment.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateFeeSummary();
            }
        });

        btnSave.addActionListener(e -> saveAppointment());

        btnClear.addActionListener(e -> clearForm());

        btnBack.addActionListener(e -> {
            new MainMenuView(currentUser).setVisible(true);
            dispose();
        });
    }

    private void loadInitialData() {
        // Load Treatment Types
        List<Treatment> treatments = treatmentDAO.getAllTreatments();
        cmbTreatment.removeAllItems();
        for (Treatment t : treatments) {
            cmbTreatment.addItem(t);
        }

        // Load Next Appointment Number Preview
        refreshNextAppointmentNumber();
        updateFeeSummary();
    }

    private void refreshNextAppointmentNumber() {
        int nextId = appointmentDAO.getNextAppointmentNumberPreview();
        lblNextApptNumber.setText("APT-" + nextId + " (Auto-assigned)");
    }

    private void updateFeeSummary() {
        Treatment selected = (Treatment) cmbTreatment.getSelectedItem();
        if (selected != null) {
            lblCostSummary.setText(String.format(
                "Consultation: LKR %.2f | Procedure: LKR %.2f | Est. Total: LKR %.2f",
                selected.getConsultationFee(),
                selected.getTreatmentCost(),
                selected.getTotalEstimate()
            ));
        }
    }

    private void saveAppointment() {
        String patientName = txtPatientName.getText().trim();
        String address = txtAddress.getText().trim();
        String contactNumber = txtContactNumber.getText().trim();
        String dentist = (String) cmbDentist.getSelectedItem();
        Treatment treatment = (Treatment) cmbTreatment.getSelectedItem();
        String appointmentDate = txtAppointmentDate.getText().trim();
        String appointmentTime = (String) cmbAppointmentTime.getSelectedItem();

        // 1. Mandatory Fields Validation (Check no field is left empty)
        if (patientName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the Patient's Name.", "Missing Information", JOptionPane.WARNING_MESSAGE);
            txtPatientName.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the Patient's Address.", "Missing Information", JOptionPane.WARNING_MESSAGE);
            txtAddress.requestFocus();
            return;
        }

        if (contactNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the Contact Number.", "Missing Information", JOptionPane.WARNING_MESSAGE);
            txtContactNumber.requestFocus();
            return;
        }

        if (treatment == null) {
            JOptionPane.showMessageDialog(this, "Please select a Treatment Type.", "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (appointmentDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the Appointment Date (YYYY-MM-DD).", "Missing Information", JOptionPane.WARNING_MESSAGE);
            txtAppointmentDate.requestFocus();
            return;
        }

        // Basic Contact Number format check (Digits / minimum length)
        if (!contactNumber.matches("^[0-9+ -]{7,15}$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid phone number (digits only).", "Invalid Contact Number", JOptionPane.WARNING_MESSAGE);
            txtContactNumber.requestFocus();
            return;
        }

        // 2. Create Appointment Object
        Appointment appointment = new Appointment(
            patientName,
            address,
            contactNumber,
            dentist,
            treatment.getTreatmentName(),
            appointmentDate,
            appointmentTime,
            treatment.getTreatmentCost(),
            treatment.getConsultationFee()
        );

        // 3. Save to MySQL Database
        try {
            int generatedId = appointmentDAO.createAppointment(appointment);
            if (generatedId > 0) {
                JOptionPane.showMessageDialog(
                    this,
                    "Appointment Successfully Registered!\n\n" +
                    "Appointment Number: " + generatedId + "\n" +
                    "Patient Name: " + patientName + "\n" +
                    "Dentist: " + dentist + "\n" +
                    "Date & Time: " + appointmentDate + " at " + appointmentTime + "\n" +
                    "Estimated Total: LKR " + String.format("%.2f", appointment.getTotalBill()),
                    "Booking Confirmed",
                    JOptionPane.INFORMATION_MESSAGE
                );

                clearForm();
                refreshNextAppointmentNumber();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Failed to save appointment. Please try again.",
                    "Save Failed",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Database error while saving appointment:\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void clearForm() {
        txtPatientName.setText("");
        txtAddress.setText("");
        txtContactNumber.setText("");
        txtAppointmentDate.setText(LocalDate.now().toString());
        if (cmbDentist.getItemCount() > 0) cmbDentist.setSelectedIndex(0);
        if (cmbTreatment.getItemCount() > 0) cmbTreatment.setSelectedIndex(0);
        if (cmbAppointmentTime.getItemCount() > 0) cmbAppointmentTime.setSelectedIndex(0);
        refreshNextAppointmentNumber();
        updateFeeSummary();
        txtPatientName.requestFocus();
    }
}
