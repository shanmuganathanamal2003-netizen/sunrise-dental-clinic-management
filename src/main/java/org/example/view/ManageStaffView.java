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
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.example.dao.UserDAO;
import org.example.model.User;
import org.example.view.components.AppMenuBar;
import org.example.view.components.UIHelper;

/**
 * ManageStaffView - Admin-Only User & Staff Management Portal
 * 
 * Allows Administrators to view, search, register, update, and remove
 * Doctors, Receptionists, and Administrators in the clinic system.
 */
public class ManageStaffView extends JFrame {

    private final User currentUser;
    private final UserDAO userDAO;

    private JTable tblUsers;
    private DefaultTableModel userTableModel;
    private JComboBox<String> cmbRoleFilter;
    private JLabel lblStatusCount;

    public ManageStaffView(User currentUser) {
        this.currentUser = currentUser;
        this.userDAO = new UserDAO();

        // Security check: Only Admin can access this view
        if (currentUser == null || !"Admin".equalsIgnoreCase(currentUser.getRole())) {
            JOptionPane.showMessageDialog(
                null,
                "ACCESS DENIED!\nOnly Administrators have permission to manage staff, doctors, and receptionists.",
                "Unauthorized Access",
                JOptionPane.ERROR_MESSAGE
            );
            SwingUtilities.invokeLater(() -> UIHelper.navigate(this, new MainMenuView(currentUser)));
            return;
        }

        initializeUI();
        loadUsers();
    }

    private void initializeUI() {
        setTitle("Sunrise Dental Clinic - Staff & Doctor Management (Admin Only)");
        setSize(1000, 680);
        setMinimumSize(new Dimension(850, 550));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Reusable Menu Bar
        setJMenuBar(AppMenuBar.createMenuBar(this, currentUser));

        // Top Header Banner
        JPanel headerPanel = UIHelper.createHeaderBanner(
            "STAFF & DOCTOR MANAGEMENT PORTAL",
            "Admin Control Panel: Register, Update, and Manage Doctors & Receptionists",
            currentUser
        );
        add(headerPanel, BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Role Filter Toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        toolbarPanel.setBackground(Color.WHITE);
        toolbarPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIHelper.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        JLabel lblFilter = new JLabel("Filter by Role:");
        lblFilter.setFont(UIHelper.FONT_BOLD);
        cmbRoleFilter = new JComboBox<>(new String[]{"All Roles", "Doctor", "Receptionist", "Admin"});
        cmbRoleFilter.setFont(UIHelper.FONT_REGULAR);
        cmbRoleFilter.setPreferredSize(new Dimension(160, 30));

        toolbarPanel.add(lblFilter);
        toolbarPanel.add(cmbRoleFilter);

        cmbRoleFilter.addActionListener(e -> loadUsers());

        centerPanel.add(toolbarPanel, BorderLayout.NORTH);

        // Table Model & View
        String[] cols = {"User ID", "Username", "Full Name", "Assigned Role", "Password"};
        userTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblUsers = new JTable(userTableModel);
        tblUsers.setFont(UIHelper.FONT_REGULAR);
        tblUsers.setRowHeight(28);
        tblUsers.getTableHeader().setFont(UIHelper.FONT_BOLD);
        tblUsers.getTableHeader().setBackground(new Color(235, 242, 250));

        // Column Widths
        tblUsers.getColumnModel().getColumn(0).setPreferredWidth(70);
        tblUsers.getColumnModel().getColumn(1).setPreferredWidth(120);
        tblUsers.getColumnModel().getColumn(2).setPreferredWidth(220);
        tblUsers.getColumnModel().getColumn(3).setPreferredWidth(120);
        tblUsers.getColumnModel().getColumn(4).setPreferredWidth(100);

        // Role Column Badge Color Renderer
        tblUsers.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                String role = String.valueOf(value);
                if ("Admin".equalsIgnoreCase(role)) {
                    setForeground(UIHelper.COLOR_PRIMARY);
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if ("Doctor".equalsIgnoreCase(role)) {
                    setForeground(UIHelper.COLOR_SUCCESS);
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if ("Receptionist".equalsIgnoreCase(role)) {
                    setForeground(UIHelper.COLOR_WARNING);
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                return c;
            }
        });

        // Mask password column with asterisks
        tblUsers.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, "••••••••", isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setForeground(Color.GRAY);
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblUsers);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Status bar below table
        lblStatusCount = new JLabel("Total Staff Registered: 0");
        lblStatusCount.setFont(UIHelper.FONT_SMALL);
        lblStatusCount.setForeground(new Color(100, 110, 125));
        lblStatusCount.setBorder(BorderFactory.createEmptyBorder(4, 5, 4, 5));
        centerPanel.add(lblStatusCount, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // Right / Bottom Actions Panel
        JPanel bottomActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        bottomActionPanel.setBackground(new Color(245, 248, 252));
        bottomActionPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIHelper.COLOR_BORDER));

        JButton btnAdd = UIHelper.createSuccessButton("+ Add New Staff", new Dimension(170, 36));
        JButton btnEdit = UIHelper.createPrimaryButton("Edit Selected Staff", new Dimension(160, 36));
        JButton btnDelete = UIHelper.createDangerButton("Delete Staff", new Dimension(130, 36));
        JButton btnBack = UIHelper.createSecondaryButton("Back to Dashboard", new Dimension(150, 36));

        bottomActionPanel.add(btnAdd);
        bottomActionPanel.add(btnEdit);
        bottomActionPanel.add(btnDelete);
        bottomActionPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        bottomActionPanel.add(btnBack);

        add(bottomActionPanel, BorderLayout.SOUTH);

        // Action Listeners
        btnAdd.addActionListener(e -> showAddEditStaffDialog(null));
        btnEdit.addActionListener(e -> editSelectedStaff());
        btnDelete.addActionListener(e -> deleteSelectedStaff());
        btnBack.addActionListener(e -> UIHelper.navigate(this, new MainMenuView(currentUser)));
    }

    private void loadUsers() {
        userTableModel.setRowCount(0);
        String roleFilter = (String) cmbRoleFilter.getSelectedItem();

        try {
            List<User> list;
            if (roleFilter == null || "All Roles".equalsIgnoreCase(roleFilter) || "All".equalsIgnoreCase(roleFilter)) {
                list = userDAO.getAllUsers();
            } else {
                list = userDAO.getUsersByRole(roleFilter);
            }
            for (User u : list) {
                userTableModel.addRow(new Object[]{
                    u.getUserId(),
                    u.getUsername(),
                    u.getFullName(),
                    u.getRole(),
                    u.getPassword()
                });
            }
            lblStatusCount.setText("Total Staff Members Listed: " + list.size());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading staff records: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelectedStaff() {
        int selectedRow = tblUsers.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a staff member from the table to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) userTableModel.getValueAt(selectedRow, 0);
        String username = (String) userTableModel.getValueAt(selectedRow, 1);
        String fullName = (String) userTableModel.getValueAt(selectedRow, 2);
        String role = (String) userTableModel.getValueAt(selectedRow, 3);
        String password = (String) userTableModel.getValueAt(selectedRow, 4);

        User selectedUser = new User(userId, username, password, fullName, role);
        showAddEditStaffDialog(selectedUser);
    }

    private void deleteSelectedStaff() {
        int selectedRow = tblUsers.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a staff member to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) userTableModel.getValueAt(selectedRow, 0);
        String username = (String) userTableModel.getValueAt(selectedRow, 1);
        String fullName = (String) userTableModel.getValueAt(selectedRow, 2);

        // Prevent admin from deleting their own currently logged-in account
        if (currentUser.getUserId() == userId || currentUser.getUsername().equalsIgnoreCase(username)) {
            JOptionPane.showMessageDialog(
                this,
                "Action Prohibited: You cannot delete your own active Administrator account!",
                "Operation Not Allowed",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to permanently delete the following staff record?\n\n" +
            "• Full Name : " + fullName + "\n" +
            "• Username  : " + username + "\n" +
            "• User ID   : " + userId + "\n\n" +
            "This action cannot be undone.",
            "Confirm Delete Staff Member",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = userDAO.deleteUser(userId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Staff member '" + fullName + "' deleted successfully.", "Staff Removed", JOptionPane.INFORMATION_MESSAGE);
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(this, "Could not delete staff member. The record may no longer exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error deleting staff: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddEditStaffDialog(User userToEdit) {
        boolean isEdit = (userToEdit != null);
        JDialog dialog = new JDialog(this, isEdit ? "Edit Staff Details" : "Register New Staff", true);
        dialog.setSize(480, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 15, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtUsername = new JTextField(15);
        txtUsername.setFont(UIHelper.FONT_REGULAR);

        JPasswordField txtPassword = new JPasswordField(15);
        txtPassword.setFont(UIHelper.FONT_REGULAR);

        JTextField txtFullName = new JTextField(15);
        txtFullName.setFont(UIHelper.FONT_REGULAR);

        JComboBox<String> cmbRole = new JComboBox<>(new String[]{"Doctor", "Receptionist", "Admin"});
        cmbRole.setFont(UIHelper.FONT_REGULAR);

        if (isEdit) {
            txtUsername.setText(userToEdit.getUsername());
            txtFullName.setText(userToEdit.getFullName());
            cmbRole.setSelectedItem(userToEdit.getRole());
            txtPassword.setText(userToEdit.getPassword());
        }

        // Row 0: Full Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblName = new JLabel("Full Name *:");
        lblName.setFont(UIHelper.FONT_BOLD);
        formPanel.add(lblName, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(txtFullName, gbc);

        // Row 1: Role
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblRole = new JLabel("System Role *:");
        lblRole.setFont(UIHelper.FONT_BOLD);
        formPanel.add(lblRole, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(cmbRole, gbc);

        // Row 2: Username
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel lblUser = new JLabel("Username *:");
        lblUser.setFont(UIHelper.FONT_BOLD);
        formPanel.add(lblUser, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(txtUsername, gbc);

        // Row 3: Password
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        JLabel lblPass = new JLabel(isEdit ? "Password (or new):" : "Password *:");
        lblPass.setFont(UIHelper.FONT_BOLD);
        formPanel.add(lblPass, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(txtPassword, gbc);

        dialog.add(formPanel, BorderLayout.CENTER);

        // Dialog Bottom Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnSave = UIHelper.createSuccessButton(isEdit ? "Update Staff" : "Register Staff", new Dimension(130, 32));
        JButton btnCancel = UIHelper.createSecondaryButton("Cancel", new Dimension(90, 32));

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String fullName = txtFullName.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();
            String role = (String) cmbRole.getSelectedItem();

            // Validation
            if (fullName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Full Name cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Username cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (username.length() < 3) {
                JOptionPane.showMessageDialog(dialog, "Username must be at least 3 characters.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!isEdit && password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Password cannot be empty for a new user.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int excludeId = isEdit ? userToEdit.getUserId() : -1;
                if (userDAO.isUsernameTaken(username, excludeId)) {
                    JOptionPane.showMessageDialog(dialog, "Username '" + username + "' is already registered to another staff member. Please choose a different username.", "Duplicate Username", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (isEdit) {
                    userToEdit.setUsername(username);
                    userToEdit.setFullName(fullName);
                    userToEdit.setRole(role);
                    if (!password.isEmpty()) {
                        userToEdit.setPassword(password);
                    }
                    boolean updated = userDAO.updateUser(userToEdit);
                    if (updated) {
                        JOptionPane.showMessageDialog(dialog, "Staff member details updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        loadUsers();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Could not update user record.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    User newUser = new User(0, username, password, fullName, role);
                    int newId = userDAO.createUser(newUser);
                    if (newId > 0) {
                        JOptionPane.showMessageDialog(dialog, "New staff member '" + fullName + "' (" + role + ") registered successfully with User ID: " + newId, "Success", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        loadUsers();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Could not register user record.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }
}
