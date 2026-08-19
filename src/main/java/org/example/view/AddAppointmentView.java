package org.example.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import org.example.dao.PatientDAO;
import org.example.dao.TreatmentDAO;
import org.example.model.Appointment;
import org.example.model.Patient;
import org.example.model.Treatment;
import org.example.model.User;
import org.example.service.AppointmentService;
import org.example.view.components.AppMenuBar;
import org.example.view.components.DatePickerDialog;
import org.example.view.components.UIHelper;

/**
 * AddAppointmentView - Add New Appointment Screen
 * 
 * Supports both Existing (Old) Registered Patients and New Patient Registration.
 * Features age configuration starting from 1 month, interactive visual calendar picker,
 * multi-row fee breakdown, and double-booking prevention.
 */
public class AddAppointmentView extends JFrame {

    private final User currentUser;
    private final AppointmentService appointmentService;
    private final PatientDAO patientDAO;
    private final TreatmentDAO treatmentDAO;

    // Patient Type Mode Switch
    private JRadioButton radExistingPatient;
    private JRadioButton radNewPatient;
    private JPanel patientDetailsCardPanel;
    private CardLayout patientCardLayout;

    // Existing Patient Controls
    private JComboBox<Patient> cmbExistingPatient;
    private JLabel lblExistingNameVal;
    private JLabel lblExistingAgeVal;
    private JLabel lblExistingContactVal;
    private JLabel lblExistingAddressVal;
    private JLabel lblExistingHistoryBadge;

    // New Patient Input Controls
    private JTextField txtNewPatientName;
    private JSpinner spnAge;
    private JComboBox<String> cmbAgeUnit;
    private JTextField txtNewContact;
    private JTextField txtNewAddress;
    private JTextField txtNewMedicalNotes;

    // Common Appointment Controls
    private JLabel lblNextApptNumber;
    private JComboBox<String> cmbDentist;
    private JComboBox<Treatment> cmbTreatment;
    private JTextField txtAppointmentDate;
    private JButton btnPickCalendar;
    private JComboBox<String> cmbAppointmentTime;

    // Multi-row Fee Summary Labels
    private JLabel lblConsultationFeeVal;
    private JLabel lblTreatmentCostVal;
    private JLabel lblTotalEstimateVal;

    // Action Buttons
    private JButton btnSave;
    private JButton btnClear;
    private JButton btnViewAll;
    private JButton btnPatientHistory;
    private JButton btnBack;

    public AddAppointmentView(User user) {
        this.currentUser = user;
        this.appointmentService = new AppointmentService();
        this.patientDAO = new PatientDAO();
        this.treatmentDAO = new TreatmentDAO();
        initializeUI();
        loadInitialData();
    }

    private void initializeUI() {
        setTitle("Sunrise Dental Clinic - Add New Appointment");
        setSize(860, 750);
        setMinimumSize(new Dimension(780, 640));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setLayout(new BorderLayout(10, 10));

        // Reusable Application Menu Bar
        setJMenuBar(AppMenuBar.createMenuBar(this, currentUser));

        // ------------------ Top Header Banner ------------------
        JPanel headerPanel = UIHelper.createHeaderBanner(
            "ADD NEW APPOINTMENT",
            "Fill in appointment and treatment details for existing or new patients",
            currentUser
        );
        add(headerPanel, BorderLayout.NORTH);

        // ------------------ Main Form Content ------------------
        JPanel mainContentPanel = new JPanel(new BorderLayout(10, 10));
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Top Section: Appointment ID & Patient Mode Selector
        JPanel topSelectionPanel = new JPanel(new BorderLayout(8, 8));
        topSelectionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Step 1: Patient Type & Booking ID"),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        // Auto Appointment Number Row
        JPanel idRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        JLabel lblApptTag = new JLabel("Appointment No (Auto):");
        lblApptTag.setFont(UIHelper.FONT_BOLD);
        lblNextApptNumber = new JLabel("Generating next ID...");
        lblNextApptNumber.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNextApptNumber.setForeground(UIHelper.COLOR_PRIMARY);
        idRow.add(lblApptTag);
        idRow.add(lblNextApptNumber);
        topSelectionPanel.add(idRow, BorderLayout.NORTH);

        // Radio Button Selector for Old vs New Patient
        JPanel radioRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 4));
        JLabel lblSelectType = new JLabel("Booking For:");
        lblSelectType.setFont(UIHelper.FONT_BOLD);

        radExistingPatient = new JRadioButton("Existing / Registered Patient (Old Patient)", true);
        radNewPatient = new JRadioButton("Register as New Patient", false);
        radExistingPatient.setFont(UIHelper.FONT_BOLD);
        radNewPatient.setFont(UIHelper.FONT_BOLD);

        ButtonGroup group = new ButtonGroup();
        group.add(radExistingPatient);
        group.add(radNewPatient);

        radioRow.add(lblSelectType);
        radioRow.add(radExistingPatient);
        radioRow.add(radNewPatient);
        topSelectionPanel.add(radioRow, BorderLayout.SOUTH);

        mainContentPanel.add(topSelectionPanel, BorderLayout.NORTH);

        // Center Section: Patient Details Card (Switches between Existing and New Patient)
        patientCardLayout = new CardLayout();
        patientDetailsCardPanel = new JPanel(patientCardLayout);

        // 1. Existing Patient Card
        JPanel cardExisting = createExistingPatientCard();
        patientDetailsCardPanel.add(cardExisting, "EXISTING");

        // 2. New Patient Card
        JPanel cardNew = createNewPatientCard();
        patientDetailsCardPanel.add(cardNew, "NEW");

        // Common Appointment Details (Doctor, Treatment, Date/Calendar, Time)
        JPanel appointmentDetailsPanel = createAppointmentDetailsPanel();

        JPanel centerContainer = new JPanel(new BorderLayout(8, 8));
        centerContainer.add(patientDetailsCardPanel, BorderLayout.NORTH);
        centerContainer.add(appointmentDetailsPanel, BorderLayout.CENTER);

        // Structured Fee Summary Box
        JPanel feeSummaryCard = createFeeSummaryCard();
        centerContainer.add(feeSummaryCard, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(centerContainer);
        scrollPane.setBorder(null);
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainContentPanel, BorderLayout.CENTER);

        // ------------------ Bottom Action Buttons ------------------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));

        btnSave = UIHelper.createPrimaryButton("Save & Book Appointment", new Dimension(220, 36));
        btnClear = UIHelper.createSecondaryButton("Clear Form", new Dimension(110, 36));
        btnViewAll = UIHelper.createSecondaryButton("View All Appointments", new Dimension(175, 36));
        btnPatientHistory = UIHelper.createSecondaryButton("Patient History", new Dimension(135, 36));
        btnBack = UIHelper.createSecondaryButton("Back to Dashboard", new Dimension(150, 36));

        buttonPanel.add(btnSave);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnViewAll);
        buttonPanel.add(btnPatientHistory);
        buttonPanel.add(btnBack);

        add(buttonPanel, BorderLayout.SOUTH);

        // ------------------ Event Listeners ------------------
        radExistingPatient.addActionListener(e -> {
            patientCardLayout.show(patientDetailsCardPanel, "EXISTING");
            updateExistingPatientFields();
        });

        radNewPatient.addActionListener(e -> {
            patientCardLayout.show(patientDetailsCardPanel, "NEW");
            txtNewPatientName.requestFocus();
        });

        cmbTreatment.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateFeeSummaryRows();
            }
        });

        btnSave.addActionListener(e -> saveAppointment());
        btnClear.addActionListener(e -> clearForm());

        btnViewAll.addActionListener(e -> {
            UIHelper.navigate(this, new AppointmentListView(currentUser));
        });

        btnPatientHistory.addActionListener(e -> {
            UIHelper.navigate(this, new PatientHistoryView(currentUser));
        });

        btnBack.addActionListener(e -> {
            UIHelper.navigate(this, new MainMenuView(currentUser));
        });
    }

    private JPanel createExistingPatientCard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Step 2: Existing Patient Lookup"),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;

        // Dropdown to pick existing registered patient
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.3;
        JLabel lblChoose = new JLabel("Select Registered Patient *:");
        lblChoose.setFont(UIHelper.FONT_BOLD);
        panel.add(lblChoose, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        cmbExistingPatient = new JComboBox<>();
        cmbExistingPatient.setFont(UIHelper.FONT_REGULAR);
        cmbExistingPatient.addActionListener(e -> updateExistingPatientFields());
        panel.add(cmbExistingPatient, gbc);
        r++;

        // Auto-populated fields preview
        lblExistingNameVal = addReadOnlyInfoRow(panel, gbc, r++, "Patient Name:", "-");
        lblExistingAgeVal = addReadOnlyInfoRow(panel, gbc, r++, "Registered Age:", "-");
        lblExistingContactVal = addReadOnlyInfoRow(panel, gbc, r++, "Contact Number:", "-");
        lblExistingAddressVal = addReadOnlyInfoRow(panel, gbc, r++, "Residential Address:", "-");
        lblExistingHistoryBadge = addReadOnlyInfoRow(panel, gbc, r++, "Previous History:", "No past appointments");

        return panel;
    }

    private JPanel createNewPatientCard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Step 2: New Patient Registration Details"),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;

        // Full Name
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.3;
        JLabel lblName = new JLabel("Patient Full Name *:");
        lblName.setFont(UIHelper.FONT_BOLD);
        panel.add(lblName, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtNewPatientName = new JTextField();
        txtNewPatientName.setFont(UIHelper.FONT_REGULAR);
        panel.add(txtNewPatientName, gbc);
        r++;

        // Age with Month / Year selection starting from 1 Month
        gbc.gridx = 0; gbc.gridy = r;
        JLabel lblAge = new JLabel("Patient Age (min 1 month) *:");
        lblAge.setFont(UIHelper.FONT_BOLD);
        panel.add(lblAge, gbc);

        gbc.gridx = 1;
        JPanel ageContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        spnAge = new JSpinner(new SpinnerNumberModel(1, 1, 120, 1));
        spnAge.setFont(UIHelper.FONT_REGULAR);
        spnAge.setPreferredSize(new Dimension(70, 26));

        String[] ageUnits = {"Month(s)", "Year(s)"};
        cmbAgeUnit = new JComboBox<>(ageUnits);
        cmbAgeUnit.setSelectedIndex(1); // default Years, but supports Months starting at 1 Month
        cmbAgeUnit.setFont(UIHelper.FONT_REGULAR);

        ageContainer.add(spnAge);
        ageContainer.add(cmbAgeUnit);
        panel.add(ageContainer, gbc);
        r++;

        // Contact Number
        gbc.gridx = 0; gbc.gridy = r;
        JLabel lblContact = new JLabel("Contact Number *:");
        lblContact.setFont(UIHelper.FONT_BOLD);
        panel.add(lblContact, gbc);

        gbc.gridx = 1;
        txtNewContact = new JTextField();
        txtNewContact.setFont(UIHelper.FONT_REGULAR);
        panel.add(txtNewContact, gbc);
        r++;

        // Residential Address
        gbc.gridx = 0; gbc.gridy = r;
        JLabel lblAddress = new JLabel("Residential Address *:");
        lblAddress.setFont(UIHelper.FONT_BOLD);
        panel.add(lblAddress, gbc);

        gbc.gridx = 1;
        txtNewAddress = new JTextField();
        txtNewAddress.setFont(UIHelper.FONT_REGULAR);
        panel.add(txtNewAddress, gbc);
        r++;

        // Medical History Notes
        gbc.gridx = 0; gbc.gridy = r;
        JLabel lblMed = new JLabel("Medical / Dental Notes:");
        lblMed.setFont(UIHelper.FONT_REGULAR);
        panel.add(lblMed, gbc);

        gbc.gridx = 1;
        txtNewMedicalNotes = new JTextField();
        txtNewMedicalNotes.setFont(UIHelper.FONT_REGULAR);
        panel.add(txtNewMedicalNotes, gbc);
        r++;

        return panel;
    }

    private JPanel createAppointmentDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Step 3: Appointment Scheduling & Treatment Details"),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;

        // Assigned Dentist
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.3;
        JLabel lblDentist = new JLabel("Assigned Dentist *:");
        lblDentist.setFont(UIHelper.FONT_BOLD);
        panel.add(lblDentist, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        String[] dentists = {
            "Dr. Samantha Perera (Senior Dental Surgeon)",
            "Dr. Nihal Silva (Orthodontist)",
            "Dr. Kasun Fernando (Endodontist)",
            "Dr. Anoma Wickramasinghe (General Dental Practitioner)"
        };
        cmbDentist = new JComboBox<>(dentists);
        cmbDentist.setFont(UIHelper.FONT_REGULAR);
        panel.add(cmbDentist, gbc);
        r++;

        // Treatment Procedure
        gbc.gridx = 0; gbc.gridy = r;
        JLabel lblTreatment = new JLabel("Treatment Procedure *:");
        lblTreatment.setFont(UIHelper.FONT_BOLD);
        panel.add(lblTreatment, gbc);

        gbc.gridx = 1;
        cmbTreatment = new JComboBox<>();
        cmbTreatment.setFont(UIHelper.FONT_REGULAR);
        panel.add(cmbTreatment, gbc);
        r++;

        // Date (YYYY-MM-DD) with Calendar Picker and Quick Today/Tomorrow Buttons
        gbc.gridx = 0; gbc.gridy = r;
        JLabel lblDate = new JLabel("Date (YYYY-MM-DD) *:");
        lblDate.setFont(UIHelper.FONT_BOLD);
        panel.add(lblDate, gbc);

        gbc.gridx = 1;
        JPanel dateContainer = new JPanel(new BorderLayout(5, 0));
        txtAppointmentDate = new JTextField(LocalDate.now().toString());
        txtAppointmentDate.setFont(UIHelper.FONT_REGULAR);

        JPanel quickDatePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));

        btnPickCalendar = UIHelper.createSecondaryButton("📅 Pick Date", new Dimension(105, 26));
        btnPickCalendar.addActionListener(e -> {
            String picked = DatePickerDialog.showDatePicker(this, txtAppointmentDate.getText().trim());
            if (picked != null) {
                txtAppointmentDate.setText(picked);
            }
        });

        JButton btnToday = new JButton("Today");
        JButton btnTomorrow = new JButton("Tomorrow");
        btnToday.setFont(UIHelper.FONT_SMALL);
        btnTomorrow.setFont(UIHelper.FONT_SMALL);

        btnToday.addActionListener(e -> txtAppointmentDate.setText(LocalDate.now().toString()));
        btnTomorrow.addActionListener(e -> txtAppointmentDate.setText(LocalDate.now().plusDays(1).toString()));

        quickDatePanel.add(btnPickCalendar);
        quickDatePanel.add(btnToday);
        quickDatePanel.add(btnTomorrow);

        dateContainer.add(txtAppointmentDate, BorderLayout.CENTER);
        dateContainer.add(quickDatePanel, BorderLayout.EAST);
        panel.add(dateContainer, gbc);
        r++;

        // Appointment Time Slot
        gbc.gridx = 0; gbc.gridy = r;
        JLabel lblTime = new JLabel("Appointment Time Slot *:");
        lblTime.setFont(UIHelper.FONT_BOLD);
        panel.add(lblTime, gbc);

        gbc.gridx = 1;
        String[] timeSlots = {
            "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM",
            "11:00 AM", "11:30 AM", "02:00 PM", "02:30 PM",
            "03:00 PM", "03:30 PM", "04:00 PM", "04:30 PM", "05:00 PM"
        };
        cmbAppointmentTime = new JComboBox<>(timeSlots);
        cmbAppointmentTime.setFont(UIHelper.FONT_REGULAR);
        panel.add(cmbAppointmentTime, gbc);
        r++;

        return panel;
    }

    private JPanel createFeeSummaryCard() {
        JPanel feeSummaryCard = new JPanel(new GridLayout(3, 1, 6, 6));
        feeSummaryCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIHelper.COLOR_PRIMARY, 2),
                "Estimated Fee Breakdown (LKR)"
            ),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        feeSummaryCard.setBackground(new Color(248, 251, 255));

        // Row 1: Consultation Fee
        JPanel row1 = new JPanel(new BorderLayout());
        row1.setOpaque(false);
        JLabel lblConsultTag = new JLabel("1. Doctor Consultation Fee:");
        lblConsultTag.setFont(UIHelper.FONT_REGULAR);
        lblConsultationFeeVal = new JLabel("LKR 1,500.00", SwingConstants.RIGHT);
        lblConsultationFeeVal.setFont(UIHelper.FONT_BOLD);
        row1.add(lblConsultTag, BorderLayout.WEST);
        row1.add(lblConsultationFeeVal, BorderLayout.EAST);

        // Row 2: Procedure Cost
        JPanel row2 = new JPanel(new BorderLayout());
        row2.setOpaque(false);
        JLabel lblProcedureTag = new JLabel("2. Treatment Procedure Cost:");
        lblProcedureTag.setFont(UIHelper.FONT_REGULAR);
        lblTreatmentCostVal = new JLabel("LKR 0.00", SwingConstants.RIGHT);
        lblTreatmentCostVal.setFont(UIHelper.FONT_BOLD);
        row2.add(lblProcedureTag, BorderLayout.WEST);
        row2.add(lblTreatmentCostVal, BorderLayout.EAST);

        // Row 3: Grand Total Estimate
        JPanel row3 = new JPanel(new BorderLayout());
        row3.setOpaque(false);
        JLabel lblTotalTag = new JLabel("ESTIMATED TOTAL AMOUNT:");
        lblTotalTag.setFont(UIHelper.FONT_BOLD);
        lblTotalTag.setForeground(UIHelper.COLOR_PRIMARY);

        lblTotalEstimateVal = new JLabel("LKR 1,500.00", SwingConstants.RIGHT);
        lblTotalEstimateVal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalEstimateVal.setForeground(new Color(180, 0, 0));
        row3.add(lblTotalTag, BorderLayout.WEST);
        row3.add(lblTotalEstimateVal, BorderLayout.EAST);

        feeSummaryCard.add(row1);
        feeSummaryCard.add(row2);
        feeSummaryCard.add(row3);
        return feeSummaryCard;
    }

    private JLabel addReadOnlyInfoRow(JPanel panel, GridBagConstraints gbc, int row, String label, String defaultVal) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIHelper.FONT_BOLD);
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        JLabel val = new JLabel(defaultVal);
        val.setFont(UIHelper.FONT_REGULAR);
        panel.add(val, gbc);
        return val;
    }

    private void loadInitialData() {
        // Load treatments
        List<Treatment> treatments = treatmentDAO.getAllTreatments();
        cmbTreatment.removeAllItems();
        for (Treatment t : treatments) {
            cmbTreatment.addItem(t);
        }

        // Load existing registered patients
        loadRegisteredPatients();

        refreshNextAppointmentNumber();
        updateFeeSummaryRows();
    }

    private void loadRegisteredPatients() {
        cmbExistingPatient.removeAllItems();
        List<Patient> patients = patientDAO.getAllPatients();
        for (Patient p : patients) {
            cmbExistingPatient.addItem(p);
        }
        updateExistingPatientFields();
    }

    public void selectExistingPatient(Patient patient) {
        radExistingPatient.setSelected(true);
        patientCardLayout.show(patientDetailsCardPanel, "EXISTING");
        for (int i = 0; i < cmbExistingPatient.getItemCount(); i++) {
            Patient p = cmbExistingPatient.getItemAt(i);
            if (p.getPatientId() == patient.getPatientId()) {
                cmbExistingPatient.setSelectedIndex(i);
                updateExistingPatientFields();
                return;
            }
        }
    }

    private void updateExistingPatientFields() {
        Patient selected = (Patient) cmbExistingPatient.getSelectedItem();
        if (selected != null) {
            lblExistingNameVal.setText(selected.getPatientName());
            lblExistingAgeVal.setText(selected.getAge() != null ? selected.getAge() : "1 Month");
            lblExistingContactVal.setText(selected.getContactNumber());
            lblExistingAddressVal.setText(selected.getAddress());

            // Count previous appointments
            try {
                List<Appointment> list = appointmentService.getAppointmentsByPatientNameOrPhone(selected.getPatientName(), selected.getContactNumber());
                lblExistingHistoryBadge.setText(list.size() + " prior appointment(s) on file");
                lblExistingHistoryBadge.setForeground(new Color(0, 100, 180));
            } catch (Exception e) {
                lblExistingHistoryBadge.setText("Existing Patient Record");
            }
        } else {
            lblExistingNameVal.setText("-");
            lblExistingAgeVal.setText("-");
            lblExistingContactVal.setText("-");
            lblExistingAddressVal.setText("-");
            lblExistingHistoryBadge.setText("-");
        }
    }

    private void refreshNextAppointmentNumber() {
        int nextId = appointmentService.getNextAppointmentNumberPreview();
        lblNextApptNumber.setText("APT-" + nextId + " (Auto-assigned)");
    }

    private void updateFeeSummaryRows() {
        Treatment selected = (Treatment) cmbTreatment.getSelectedItem();
        if (selected != null) {
            lblConsultationFeeVal.setText(String.format("LKR %,.2f", selected.getConsultationFee()));
            lblTreatmentCostVal.setText(String.format("LKR %,.2f", selected.getTreatmentCost()));
            lblTotalEstimateVal.setText(String.format("LKR %,.2f", selected.getTotalEstimate()));
        }
    }

    private void saveAppointment() {
        boolean isExisting = radExistingPatient.isSelected();
        Integer patientId = null;
        String patientName;
        String patientAge;
        String contactNumber;
        String address;

        if (isExisting) {
            Patient selected = (Patient) cmbExistingPatient.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select an existing patient from the list.", "Missing Patient", JOptionPane.WARNING_MESSAGE);
                return;
            }
            patientId = selected.getPatientId();
            patientName = selected.getPatientName();
            patientAge = selected.getAge();
            contactNumber = selected.getContactNumber();
            address = selected.getAddress();
        } else {
            patientName = txtNewPatientName.getText().trim();
            int ageVal = (int) spnAge.getValue();
            String ageUnit = (String) cmbAgeUnit.getSelectedItem();
            patientAge = ageVal + " " + (ageUnit.startsWith("Month") ? "Month(s)" : "Year(s)");
            contactNumber = txtNewContact.getText().trim();
            address = txtNewAddress.getText().trim();
            String medicalNotes = txtNewMedicalNotes.getText().trim();

            if (patientName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the Patient Full Name.", "Missing Name", JOptionPane.WARNING_MESSAGE);
                txtNewPatientName.requestFocus();
                return;
            }
            if (contactNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the Contact Number.", "Missing Contact", JOptionPane.WARNING_MESSAGE);
                txtNewContact.requestFocus();
                return;
            }
            if (address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the Residential Address.", "Missing Address", JOptionPane.WARNING_MESSAGE);
                txtNewAddress.requestFocus();
                return;
            }

            // Create patient record
            try {
                Patient newP = new Patient(patientName, patientAge, "Not Specified", contactNumber, address, medicalNotes);
                patientId = patientDAO.createPatient(newP);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error creating patient: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String dentist = (String) cmbDentist.getSelectedItem();
        Treatment treatment = (Treatment) cmbTreatment.getSelectedItem();
        String appointmentDate = txtAppointmentDate.getText().trim();
        String appointmentTime = (String) cmbAppointmentTime.getSelectedItem();

        if (treatment == null) {
            JOptionPane.showMessageDialog(this, "Please select a Treatment procedure.", "Missing Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Appointment appointment = new Appointment(
            patientId,
            patientName,
            patientAge,
            address,
            contactNumber,
            dentist,
            treatment.getTreatmentName(),
            appointmentDate,
            appointmentTime,
            treatment.getTreatmentCost(),
            treatment.getConsultationFee()
        );

        try {
            int generatedId = appointmentService.registerAppointment(appointment);
            if (generatedId > 0) {
                JOptionPane.showMessageDialog(
                    this,
                    "Appointment Successfully Added & Confirmed!\n\n" +
                    "Appointment Number : " + generatedId + "\n" +
                    "Patient Name       : " + patientName + " (Age: " + patientAge + ")\n" +
                    "Assigned Doctor    : " + dentist + "\n" +
                    "Procedure          : " + treatment.getTreatmentName() + "\n" +
                    "Date & Time        : " + appointmentDate + " at " + appointmentTime + "\n" +
                    "Total Estimate     : LKR " + String.format("%,.2f", appointment.getTotalBill()),
                    "Booking Confirmed",
                    JOptionPane.INFORMATION_MESSAGE
                );

                clearForm();
                loadRegisteredPatients();
                refreshNextAppointmentNumber();
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Warning", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Double Booking Prevented", JOptionPane.ERROR_MESSAGE);
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
        txtNewPatientName.setText("");
        spnAge.setValue(1);
        cmbAgeUnit.setSelectedIndex(1);
        txtNewContact.setText("");
        txtNewAddress.setText("");
        txtNewMedicalNotes.setText("");
        txtAppointmentDate.setText(LocalDate.now().toString());

        if (cmbDentist.getItemCount() > 0) cmbDentist.setSelectedIndex(0);
        if (cmbTreatment.getItemCount() > 0) cmbTreatment.setSelectedIndex(0);
        if (cmbAppointmentTime.getItemCount() > 0) cmbAppointmentTime.setSelectedIndex(0);

        refreshNextAppointmentNumber();
        updateFeeSummaryRows();
    }
}
