package org.example.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.example.model.User;

/**
 * MainMenuView - Main Dashboard Navigation Menu
 * 
 * Provides centralized navigation to all functional modules of Sunrise Dental Clinic.
 */
public class MainMenuView extends JFrame {

    private User currentUser;

    public MainMenuView(User user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Sunrise Dental Clinic - Main Dashboard");
        setSize(600, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout(15, 15));

        // Window closing confirmation
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                confirmExit();
            }
        });

        // ------------------ Header Panel ------------------
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(24, 90, 157));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel lblTitle = new JLabel("SUNRISE DENTAL CLINIC", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        String userDisplay = (currentUser != null) ? currentUser.getFullName() + " (" + currentUser.getRole() + ")" : "Staff";
        JLabel lblUser = new JLabel("Logged in as: " + userDisplay, SwingConstants.CENTER);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUser.setForeground(new Color(220, 235, 252));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblUser, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // ------------------ Menu Buttons Panel ------------------
        JPanel menuPanel = new JPanel(new GridLayout(4, 1, 12, 12));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton btnRegister = createMenuButton("1. Register New Appointment");
        JButton btnSearch = createMenuButton("2. Search / View Appointment Details");
        JButton btnBill = createMenuButton("3. Calculate & Print Patient Bill");
        JButton btnHelp = createMenuButton("4. System Help & User Guide");

        menuPanel.add(btnRegister);
        menuPanel.add(btnSearch);
        menuPanel.add(btnBill);
        menuPanel.add(btnHelp);

        add(menuPanel, BorderLayout.CENTER);

        // ------------------ Footer / Action Panel ------------------
        JPanel footerPanel = new JPanel();
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnLogout.setPreferredSize(new Dimension(110, 35));

        JButton btnExit = new JButton("Exit Application");
        btnExit.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnExit.setPreferredSize(new Dimension(140, 35));

        footerPanel.add(btnLogout);
        footerPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        footerPanel.add(btnExit);

        add(footerPanel, BorderLayout.SOUTH);

        // ------------------ Action Listeners ------------------
        btnRegister.addActionListener(e -> {
            new RegisterAppointmentView(currentUser).setVisible(true);
            dispose();
        });

        btnSearch.addActionListener(e -> {
            new SearchAppointmentView(currentUser).setVisible(true);
            dispose();
        });

        btnBill.addActionListener(e -> {
            new BillView(currentUser, -1).setVisible(true);
            dispose();
        });

        btnHelp.addActionListener(e -> {
            new HelpView(MainMenuView.this).setVisible(true);
        });

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginView().setVisible(true);
                dispose();
            }
        });

        btnExit.addActionListener(e -> confirmExit());
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBackground(new Color(245, 248, 252));
        button.setForeground(new Color(24, 90, 157));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 205, 230), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        return button;
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
