package org.example.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.example.dao.UserDAO;
import org.example.model.User;
import org.example.view.components.UIHelper;

/**
 * RegisterUserView - New Staff & User Registration Screen
 * 
 * Allows new clinic staff and receptionists to create an account.
 * Supports window maximize/minimize.
 */
public class RegisterUserView extends JFrame {

    private final User currentUser;
    private final UserDAO userDAO;

    private JTextField txtFullName;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JComboBox<String> cmbRole;

    private JButton btnRegister;
    private JButton btnCancel;

    public RegisterUserView() {
        this(null);
    }

    public RegisterUserView(User currentUser) {
        this.currentUser = currentUser;
        this.userDAO = new UserDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Sunrise Dental Clinic - Register Staff Account");
        setSize(560, 530);
        setMinimumSize(new Dimension(500, 460));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true); // Supports maximize and minimize
        setLayout(new BorderLayout());

        // ------------------ Top Header Banner ------------------
        JPanel headerPanel = UIHelper.createHeaderBanner(
            "MANAGE & REGISTER STAFF ACCOUNTS",
            "Create authorized login credentials for Receptionists, Doctors, or Administrators",
            currentUser
        );
        add(headerPanel, BorderLayout.NORTH);

        // ------------------ Centered Content Container ------------------
        JPanel centeringWrapper = new JPanel(new GridBagLayout());
        centeringWrapper.setBackground(new Color(240, 244, 250));

        JPanel cardPanel = new JPanel(new BorderLayout(10, 10));
        cardPanel.setPreferredSize(new Dimension(480, 360));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 215, 230), 1),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Full Name
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        JLabel lblFullName = new JLabel("Full Name *:");
        lblFullName.setFont(UIHelper.FONT_BOLD);
        formPanel.add(lblFullName, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        txtFullName = new JTextField(15);
        txtFullName.setFont(UIHelper.FONT_REGULAR);
        formPanel.add(txtFullName, gbc);
        row++;

        // Username
        gbc.gridx = 0; gbc.gridy = row;

        JLabel lblUser = new JLabel("Username *:");
        lblUser.setFont(UIHelper.FONT_BOLD);
        formPanel.add(lblUser, gbc);

        gbc.gridx = 1;
        txtUsername = new JTextField(15);
        txtUsername.setFont(UIHelper.FONT_REGULAR);
        formPanel.add(txtUsername, gbc);
        row++;

        // Role
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblRole = new JLabel("Staff Role *:");
        lblRole.setFont(UIHelper.FONT_BOLD);
        formPanel.add(lblRole, gbc);

        gbc.gridx = 1;
        String[] roles = {"Receptionist", "Doctor", "Admin"};
        cmbRole = new JComboBox<>(roles);
        cmbRole.setFont(UIHelper.FONT_REGULAR);
        formPanel.add(cmbRole, gbc);
        row++;

        // Password
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblPass = new JLabel("Password *:");
        lblPass.setFont(UIHelper.FONT_BOLD);
        formPanel.add(lblPass, gbc);

        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        txtPassword.setFont(UIHelper.FONT_REGULAR);
        formPanel.add(txtPassword, gbc);
        row++;

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblConfirm = new JLabel("Confirm Password *:");
        lblConfirm.setFont(UIHelper.FONT_BOLD);
        formPanel.add(lblConfirm, gbc);

        gbc.gridx = 1;
        txtConfirmPassword = new JPasswordField(15);
        txtConfirmPassword.setFont(UIHelper.FONT_REGULAR);
        formPanel.add(txtConfirmPassword, gbc);
        row++;

        cardPanel.add(formPanel, BorderLayout.CENTER);

        // ------------------ Buttons Panel ------------------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);

        btnRegister = UIHelper.createPrimaryButton("Create Account", new Dimension(150, 36));
        btnCancel = UIHelper.createSecondaryButton(currentUser != null ? "Back to Dashboard" : "Back to Login", new Dimension(150, 36));

        buttonPanel.add(btnRegister);
        buttonPanel.add(btnCancel);
        cardPanel.add(buttonPanel, BorderLayout.SOUTH);

        centeringWrapper.add(cardPanel);
        add(centeringWrapper, BorderLayout.CENTER);

        // ------------------ Listeners ------------------
        btnRegister.addActionListener(e -> performRegistration());

        btnCancel.addActionListener(e -> {
            if (currentUser != null) {
                UIHelper.navigate(this, new MainMenuView(currentUser));
            } else {
                UIHelper.navigate(this, new LoginView());
            }
        });
    }

    private void performRegistration() {
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String confirmPassword = new String(txtConfirmPassword.getPassword()).trim();
        String role = (String) cmbRole.getSelectedItem();

        // 1. Validation Checks
        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your Full Name.", "Input Required", JOptionPane.WARNING_MESSAGE);
            txtFullName.requestFocus();
            return;
        }

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Username.", "Input Required", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }

        if (username.length() < 3) {
            JOptionPane.showMessageDialog(this, "Username must be at least 3 characters long.", "Invalid Username", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Password.", "Input Required", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this, "Password must be at least 4 characters long.", "Weak Password", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match! Please re-type.", "Password Mismatch", JOptionPane.ERROR_MESSAGE);
            txtConfirmPassword.setText("");
            txtConfirmPassword.requestFocus();
            return;
        }

        // 2. Check if username is taken
        try {
            if (userDAO.isUsernameTaken(username)) {
                JOptionPane.showMessageDialog(
                    this,
                    "Username '" + username + "' is already taken.\nPlease choose a different username.",
                    "Username Unavailable",
                    JOptionPane.WARNING_MESSAGE
                );
                txtUsername.selectAll();
                txtUsername.requestFocus();
                return;
            }

            // 3. Register user
            User newUser = new User(0, username, password, fullName, role);
            int newId = userDAO.registerUser(newUser);

            if (newId > 0) {
                System.out.println("[Auth] New user registered successfully:");
                newUser.printDetails();

                JOptionPane.showMessageDialog(
                    this,
                    "Staff Account Successfully Created!\n\n" +
                    "Username : " + username + "\n" +
                    "Full Name: " + fullName + "\n" +
                    "Role     : " + role,
                    "Registration Successful",
                    JOptionPane.INFORMATION_MESSAGE
                );

                if (currentUser != null) {
                    // Clear form for potential next registration
                    txtFullName.setText("");
                    txtUsername.setText("");
                    txtPassword.setText("");
                    txtConfirmPassword.setText("");
                    if (cmbRole.getItemCount() > 0) cmbRole.setSelectedIndex(0);
                    txtFullName.requestFocus();
                } else {
                    LoginView loginView = new LoginView();
                    loginView.setPrefilledUsername(username);
                    UIHelper.navigate(this, loginView);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Could not register account. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Database error during registration:\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

