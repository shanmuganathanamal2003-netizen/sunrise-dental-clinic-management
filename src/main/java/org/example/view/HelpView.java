package org.example.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import org.example.view.components.UIHelper;

/**
 * HelpView - System User Manual and Help Screen
 *
 * Provides comprehensive guidance for clinic staff on all system modules.
 */
public class HelpView extends JDialog {

    public HelpView(Frame parent) {
        super(parent, "Sunrise Dental Clinic - System Help & User Guide", true);
        initializeUI();
    }

    private void initializeUI() {
        setSize(780, 660);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        // ------------------ Top Header Banner ------------------
        JPanel headerPanel = UIHelper.createHeaderBanner(
                "SYSTEM USER GUIDE & OPERATIONAL MANUAL",
                "Sunrise Dental Clinic - Staff Operational Instructions & Feature Walkthrough",
                null
        );
        add(headerPanel, BorderLayout.NORTH);

        // ------------------ Help Text Content ------------------
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        textPane.setCaretPosition(0);

        String helpHtml =
                "<html>" +
                        "<body style='font-family: Segoe UI, sans-serif; font-size: 11pt; padding: 12px; color: #222222;'>" +

                        "<h3 style='color: #185a9d; margin-top: 0;'>1. System Roles & Access Control</h3>" +
                        "<p>• <b>Admin:</b> Full administrative authority. Can book appointments, generate bills, access clinic revenue reports, and manage all patient records.<br>" +
                        "• <b>Receptionist:</b> Front desk operations. Can book appointments (for new or existing patients), view confirmed schedules, look up patient medical history, and calculate & print bills.<br>" +
                        "• <b>Doctor:</b> Clinical practice portal. Can view their dedicated patient schedule ('Today', 'Tomorrow', 'Next 7 Days', or custom date picker), inspect patient medical histories, and add/edit clinical diagnosis notes.<br>" +
                        "• <b>Default Credentials:</b><br>" +
                        "&nbsp;&nbsp;• Admin: <code>admin</code> / <code>admin123</code><br>" +
                        "&nbsp;&nbsp;• Receptionist: <code>receptionist1</code> / <code>receptionist123</code><br>" +
                        "&nbsp;&nbsp;• Doctor: <code>doctor1</code> / <code>doctor123</code> | <code>doctor2</code> / <code>doctor123</code></p>" +

                        "<h3 style='color: #185a9d;'>2. Adding New Appointments (New vs. Registered Patients)</h3>" +
                        "<p>• Select either <b>'Registered / Old Patient'</b> or <b>'New Patient'</b> mode:<br>" +
                        "&nbsp;&nbsp;• <i>Registered Patient:</i> Select an existing patient from the dropdown list to automatically auto-fill their demographic and contact details.<br>" +
                        "&nbsp;&nbsp;• <i>New Patient:</i> Input patient name, address, contact, age, and medical history.<br>" +
                        "• <b>Assigned Doctor:</b> Select an active doctor from the clinic's registered medical team.<br>" +
                        "• <b>Patient Age (Starting from 1 Month):</b> Specify age starting at 1 Month (e.g. 1 Month, 6 Months, 2 Years, 35 Years) using the unit selector.<br>" +
                        "• <b>Interactive Calendar Date Picker:</b> Click the <b>'📅 Calendar'</b> button next to the date field to open a visual calendar picker with month/year navigation.<br>" +
                        "• <b>Double-Booking Prevention:</b> The system verifies doctor availability and prevents double booking for the same time slot.<br>" +
                        "• <b>Clear Fee Breakdown:</b> View structured rows for Consultation Fee, Procedure Cost, and Total Estimate.</p>" +

                        "<h3 style='color: #185a9d;'>3. Doctor Schedule & Clinical Queue</h3>" +
                        "<p>• Doctors access their schedule directly from the <b>'My Patient Schedule & Clinical Queue'</b> dashboard tile or menu.<br>" +
                        "• <b>Quick Date Navigation:</b> Easily switch between <b>'Today'</b>, <b>'Tomorrow'</b>, <b>'Next 7 Days'</b>, or pick any specific calendar date.<br>" +
                        "• <b>Clinical Diagnosis Notes:</b> Click <b>'📝 Add Diagnosis / Treatment Notes'</b> to document clinical findings, prescriptions, and procedures for any patient visit.</p>" +

                        "<h3 style='color: #185a9d;'>4. Patient Medical & Appointment History</h3>" +
                        "<p>• Open <b>'Patient Search & Medical History'</b> to inspect a patient's complete journey.<br>" +
                        "• Search by Patient Name, Phone Number, Patient ID, or Appointment Number.<br>" +
                        "• View demographic details, total appointments attended, total billing spent, clinical doctor notes, and full chronological appointment timeline.<br>" +
                        "• Supports patients having multiple (2 or more) appointments over time.</p>" +

                        "<h3 style='color: #185a9d;'>5. Calculating and Printing Bills</h3>" +
                        "<p>• Enter Patient Name or Appointment Number to load appointment records.<br>" +
                        "• Calculates: <b>Total Bill = Doctor Consultation Fee + Treatment Procedure Cost</b>.<br>" +
                        "• Patient Age is automatically displayed and printed on the official invoice.<br>" +
                        "• Click <b>'Patient History'</b> to review full patient records and past visits.<br>" +
                        "• Click <b>'Save Bill to DB'</b> to record payment as Billed in the database.<br>" +
                        "• Click <b>'Print / Save as PDF'</b> to print the official bill or export as PDF.</p>" +

                        "<h3 style='color: #185a9d;'>6. Management Analytics & Decision Making (Admin Only)</h3>" +
                        "<p>• Open <b>'Clinic Analytics & Financial Reports'</b> from the Admin dashboard or top menu bar.<br>" +
                        "• View Overall Revenue Summary (including Scheduled, Billed, and Cancelled counts), Doctor Workload, and Treatment popularity statistics on screen.</p>" +

                        "<h3 style='color: #185a9d;'>7. Cancelling Appointments</h3>" +
                        "<p>• Select the appointment you want to cancel from the appointment list (Staff) or clinical queue (Doctor), then click <b>'Cancel Appointment'</b>.<br>" +
                        "• A confirmation dialog appears (Yes/No) to prevent cancelling by accident. Click <b>'Yes'</b> to proceed.<br>" +
                        "• You will then be prompted to enter a <b>Reason for Cancellation</b>. This field cannot be left blank — if no reason is entered, the system will keep prompting until a valid reason is provided.<br>" +
                        "• Once a valid reason is entered, the appointment status and reason are updated in the database.<br>" +
                        "• The appointment row is immediately highlighted in <b style='color:#c0392b;'>red</b> in the list so cancelled appointments are easy to spot at a glance.<br>" +
                        "• This cancellation process works the same way for both Staff and Doctors — only the screen you start from differs.</p>" +

                        "<h3 style='color: #185a9d;'>8. Staff & Doctor Management (Admin Only)</h3>" +
                        "<p>• <b>Admin Exclusive:</b> Only users with the <b>Admin</b> role have access to create, update, or remove clinic personnel.<br>" +
                        "• <b>Manage Doctors & Receptionists:</b> Access from the Admin Dashboard (Button 6) or the 'Staff & Doctors' menu bar.<br>" +
                        "• <b>Add New Staff/Doctor:</b> Register new doctors or front desk receptionists with customized credentials, full names, and roles.<br>" +
                        "• <b>Edit Staff:</b> Update employee names, system roles, or change/reset passwords.<br>" +
                        "• <b>Remove Staff:</b> Delete inactive staff members safely (with protection against deleting the active Administrator account).</p>" +

                        "</body>" +
                        "</html>";

        textPane.setText(helpHtml);
        textPane.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        add(scrollPane, BorderLayout.CENTER);

        // ------------------ Footer Button ------------------
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnClose = UIHelper.createPrimaryButton("Close Help Guide", new Dimension(160, 34));
        btnClose.addActionListener(e -> dispose());
        footerPanel.add(btnClose);

        add(footerPanel, BorderLayout.SOUTH);
    }
}