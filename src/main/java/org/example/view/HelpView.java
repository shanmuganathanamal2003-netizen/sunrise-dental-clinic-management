package org.example.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

/**
 * HelpView - System User Manual and Help Screen
 * 
 * Provides step-by-step guidance for new clinic staff on how to use every feature.
 */
public class HelpView extends JDialog {

    public HelpView(Frame parent) {
        super(parent, "Sunrise Dental Clinic - System Help & User Guide", true);
        initializeUI();
    }

    private void initializeUI() {
        setSize(650, 550);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        // ------------------ Header Panel ------------------
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(24, 90, 157));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JLabel lblTitle = new JLabel("SYSTEM USER GUIDE & HELP", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Instructions for Clinic Reception & Staff", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(220, 235, 252));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // ------------------ Help Text Content ------------------
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        textPane.setCaretPosition(0);

        String helpHtml = 
            "<html>" +
            "<body style='font-family: Segoe UI, sans-serif; font-size: 11pt; padding: 10px; color: #222222;'>" +
            
            "<h3 style='color: #185a9d; margin-top: 0;'>1. System Login & Authentication</h3>" +
            "<p>• Enter your authorized staff username and password.<br>" +
            "• Default credentials provided:<br>" +
            "&nbsp;&nbsp;<b>Admin:</b> Username: <code>admin</code> | Password: <code>admin123</code><br>" +
            "&nbsp;&nbsp;<b>Staff:</b> Username: <code>staff</code> | Password: <code>staff123</code><br>" +
            "• Make sure WAMP Server is running with green icon before logging in.</p>" +

            "<h3 style='color: #185a9d;'>2. Registering a New Appointment</h3>" +
            "<p>• The system automatically generates a unique <b>Appointment Number</b> (e.g. 1001, 1002).<br>" +
            "• Fill in the patient's Full Name, Residential Address, and Contact Number.<br>" +
            "• Select the <b>Assigned Dentist</b> and <b>Treatment Type</b> from the dropdown menus.<br>" +
            "• Choose the Appointment Date (format: <code>YYYY-MM-DD</code>) and convenient Time Slot.<br>" +
            "• Click <b>'Save & Book Appointment'</b>. All fields are mandatory.</p>" +

            "<h3 style='color: #185a9d;'>3. Searching / Viewing Appointment Records</h3>" +
            "<p>• Enter the specific <b>Appointment Number</b> in the search bar and click <b>'Search'</b>.<br>" +
            "• View full details including Patient Name, Address, Contact, Doctor, Treatment, and Current Status.<br>" +
            "• You can directly click <b>'Calculate / Print Bill'</b> to jump to invoicing for that patient.</p>" +

            "<h3 style='color: #185a9d;'>4. Calculating and Printing Bills</h3>" +
            "<p>• Load the appointment by entering the Appointment Number.<br>" +
            "• The system calculates: <b>Total Bill = Doctor Consultation Fee + Treatment Procedure Cost</b>.<br>" +
            "• Click <b>'Generate Receipt'</b> to format the official patient invoice.<br>" +
            "• Click <b>'Save Bill to DB'</b> to record the billing status in the database.<br>" +
            "• Click <b>'Print / Save as PDF'</b> to send the receipt directly to your printer or export as PDF.</p>" +

            "<h3 style='color: #185a9d;'>5. Troubleshooting & Support</h3>" +
            "<p>• <b>Database Error:</b> Ensure WAMP is active and phpMyAdmin has imported <code>database.sql</code> (Database name: <code>sunrise_dental_db</code>).<br>" +
            "• <b>Port Conflict:</b> Ensure MySQL port 3306 is not blocked.<br>" +
            "• Contact Sunrise Dental IT Support: <b>+94 11 234 5678</b>.</p>" +

            "</body>" +
            "</html>";

        textPane.setText(helpHtml);
        textPane.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        add(scrollPane, BorderLayout.CENTER);

        // ------------------ Footer Button ------------------
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnClose = new JButton("Close Help");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.setPreferredSize(new Dimension(120, 32));
        btnClose.addActionListener(e -> dispose());
        footerPanel.add(btnClose);

        add(footerPanel, BorderLayout.SOUTH);
    }
}
