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
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.example.dao.UserDAO;
import org.example.model.User;
import org.example.view.components.UIHelper;

/**
 * LoginView - User Authentication Screen
 * 
 * Allows clinic staff/administrators to log into the system.
 * Supports window maximize/minimize.
 */
public class LoginView extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnClear;
    private JButton btnExit;

    private final UserDAO userDAO;

    public LoginView() {
        userDAO = new UserDAO();
        initializeUI();
    }

    public void setPrefilledUsername(String username) {
        if (txtUsername != null && username != null) {
            txtUsername.setText(username);
            txtPassword.requestFocus();
        }
    }

    private void initializeUI() {
        setTitle("Sunrise Dental Clinic - Staff Login");
        setSize(560, 480);
        setMinimumSize(new Dimension(500, 440));
        setLocationRelativeTo(null); // Center on screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true); // Supports maximize and minimize
        setLayout(new BorderLayout());

        // ------------------ Top Header Banner ------------------
        JPanel headerPanel = UIHelper.createHeaderBanner(
            "SUNRISE DENTAL CLINIC",
            "Appointment & Patient Management System - Colombo, Sri Lanka",
            null
        );
        add(headerPanel, BorderLayout.NORTH);

        // ------------------ Centered Content Container ------------------
        // Using GridBagLayout so that when maximized to full screen, the login card remains centered!
        JPanel centeringWrapper = new JPanel(new GridBagLayout());
        centeringWrapper.setBackground(new Color(240, 244, 250));

        JPanel cardPanel = new JPanel(new BorderLayout(10, 10));
        cardPanel.setPreferredSize(new Dimension(460, 310));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 215, 230), 1),
            BorderFactory.createEmptyBorder(18, 25, 18, 25)
        ));

        // Form Fields
        JPanel formFields = new JPanel(new GridBagLayout());
        formFields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.32;
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(UIHelper.FONT_BOLD);
        formFields.add(lblUsername, gbc);

        gbc.gridx = 1; gbc.weightx = 0.68;
        txtUsername = new JTextField(15);
        txtUsername.setFont(UIHelper.FONT_REGULAR);
        formFields.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(UIHelper.FONT_BOLD);
        formFields.add(lblPassword, gbc);

        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        txtPassword.setFont(UIHelper.FONT_REGULAR);
        formFields.add(txtPassword, gbc);

        // Default Credentials Hint
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JLabel lblHint = new JLabel("Default logins: admin / admin123  or  staff / staff123", SwingConstants.CENTER);
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblHint.setForeground(new Color(0, 90, 190));
        formFields.add(lblHint, gbc);

        cardPanel.add(formFields, BorderLayout.NORTH);

        // Buttons Section
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        buttonPanel.setOpaque(false);

        btnLogin = UIHelper.createPrimaryButton("Login", new Dimension(100, 36));
        btnClear = UIHelper.createSecondaryButton("Clear", new Dimension(85, 36));
        btnExit = UIHelper.createSecondaryButton("Exit", new Dimension(85, 36));

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnExit);

        cardPanel.add(buttonPanel, BorderLayout.SOUTH);

        centeringWrapper.add(cardPanel);
        add(centeringWrapper, BorderLayout.CENTER);

        // Enter key in password triggers login
        txtPassword.addActionListener(e -> performLogin());

        // Event Handlers
        btnLogin.addActionListener(e -> performLogin());

        btnClear.addActionListener(e -> {
            txtUsername.setText("");
            txtPassword.setText("");
            txtUsername.requestFocus();
        });

        btnExit.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                LoginView.this,
                "Are you sure you want to exit the application?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }

    /**
     * Performs authentication check against MySQL database.
     */
    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        // 1. Validation: Ensure fields are not empty
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter both username and password.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 2. Database Check
        try {
            User user = userDAO.authenticate(username, password);
            if (user != null) {
                System.out.println("[Auth] User successfully logged in: " + user.getFullName() + " [" + user.getRole() + "]");
                user.printDetails();

                JOptionPane.showMessageDialog(
                    this,
                    "Login Successful!\nWelcome, " + user.getFullName() + " (" + user.getRole() + ")",
                    "Welcome",
                    JOptionPane.INFORMATION_MESSAGE
                );
                // Open Main Menu Dashboard
                UIHelper.navigate(this, new MainMenuView(user));
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Invalid username or password.\nPlease check credentials and try again.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
                );
                txtPassword.setText("");
                txtPassword.requestFocus();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Database error during login:\n" + ex.getMessage() + "\n\nPlease ensure WAMP / MySQL is running.",
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

