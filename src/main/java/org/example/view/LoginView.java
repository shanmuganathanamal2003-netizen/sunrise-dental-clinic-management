package org.example.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

/**
 * LoginView - User Authentication Screen
 * 
 * Allows clinic staff/administrators to securely log into the system.
 */
public class LoginView extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnClear;
    private JButton btnExit;

    private UserDAO userDAO;

    public LoginView() {
        userDAO = new UserDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Sunrise Dental Clinic - Staff Login");
        setSize(480, 360);
        setLocationRelativeTo(null); // Center on screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        // ------------------ Header Panel ------------------
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(24, 90, 157)); // Dental Blue
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("SUNRISE DENTAL CLINIC", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Appointment & Patient Management System", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(220, 235, 252));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // ------------------ Form Panel ------------------
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username Label & Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblUsername, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        txtUsername = new JTextField(15);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtUsername, gbc);

        // Password Label & Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblPassword, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtPassword, gbc);

        add(formPanel, BorderLayout.CENTER);

        // ------------------ Button Panel ------------------
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 20, 15));

        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setBackground(new Color(24, 90, 157));
        btnLogin.setForeground(Color.WHITE);

        btnClear = new JButton("Clear");
        btnClear.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        btnExit = new JButton("Exit");
        btnExit.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnExit);

        add(buttonPanel, BorderLayout.SOUTH);

        // Enter key in password triggers login
        txtPassword.addActionListener(e -> performLogin());

        // Event Handlers
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtUsername.setText("");
                txtPassword.setText("");
                txtUsername.requestFocus();
            }
        });

        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                    LoginView.this,
                    "Are you sure you want to exit the application?",
                    "Exit Confirmation",
                    JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
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
                JOptionPane.showMessageDialog(
                    this,
                    "Login Successful! Welcome, " + user.getFullName(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                // Open Main Menu Dashboard
                new MainMenuView(user).setVisible(true);
                this.dispose(); // Close login window
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
