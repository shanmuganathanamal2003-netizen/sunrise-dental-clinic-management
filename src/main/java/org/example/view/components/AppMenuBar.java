package org.example.view.components;

import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.example.model.User;
import org.example.view.AddAppointmentView;
import org.example.view.AppointmentListView;
import org.example.view.BillView;
import org.example.view.DoctorQueueView;
import org.example.view.HelpView;
import org.example.view.LoginView;
import org.example.view.MainMenuView;
import org.example.view.ManageStaffView;
import org.example.view.PatientHistoryView;
import org.example.view.ReportsView;

/**
 * AppMenuBar - Role-Based Reusable Application Menu Bar
 * 
 * Enforces permissions for Admin, Receptionist, and Doctor roles
 * while preserving window maximize state across transitions.
 */
public class AppMenuBar extends JMenuBar {

    public static JMenuBar createMenuBar(JFrame currentFrame, User currentUser) {
        JMenuBar menuBar = new JMenuBar();
        Font menuFont = new Font("Segoe UI", Font.PLAIN, 12);

        String role = (currentUser != null && currentUser.getRole() != null) ? currentUser.getRole() : "Receptionist";
        boolean isAdmin = "Admin".equalsIgnoreCase(role);
        boolean isReceptionist = "Receptionist".equalsIgnoreCase(role);
        boolean isDoctor = "Doctor".equalsIgnoreCase(role);

        // 1. File Menu
        JMenu menuFile = new JMenu("File");
        menuFile.setFont(menuFont);

        JMenuItem itemDashboard = new JMenuItem("Dashboard / Home");
        JMenuItem itemLogout = new JMenuItem("Logout");
        JMenuItem itemExit = new JMenuItem("Exit");

        itemDashboard.setFont(menuFont);
        itemLogout.setFont(menuFont);
        itemExit.setFont(menuFont);

        itemDashboard.addActionListener(e -> {
            UIHelper.navigate(currentFrame, new MainMenuView(currentUser));
        });

        itemLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                currentFrame, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                UIHelper.navigate(currentFrame, new LoginView());
            }
        });

        itemExit.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                currentFrame, "Are you sure you want to exit Sunrise Dental System?", "Exit", JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        menuFile.add(itemDashboard);

        menuFile.addSeparator();
        menuFile.add(itemLogout);
        menuFile.add(itemExit);
        menuBar.add(menuFile);

        // 2. Appointments Menu
        JMenu menuAppt = new JMenu("Appointments");
        menuAppt.setFont(menuFont);

        if (isDoctor) {
            JMenuItem itemDoctorQueue = new JMenuItem("My Patient Schedule & Queue");
            itemDoctorQueue.setFont(menuFont);
            itemDoctorQueue.addActionListener(e -> {
                UIHelper.navigate(currentFrame, new DoctorQueueView(currentUser));
            });
            menuAppt.add(itemDoctorQueue);
        } else {
            JMenuItem itemNewAppt = new JMenuItem("Add New Appointment (New / Old Patient)");
            JMenuItem itemViewAll = new JMenuItem("View All Confirmed Appointments");

            itemNewAppt.setFont(menuFont);
            itemViewAll.setFont(menuFont);

            itemNewAppt.addActionListener(e -> {
                UIHelper.navigate(currentFrame, new AddAppointmentView(currentUser));
            });

            itemViewAll.addActionListener(e -> {
                UIHelper.navigate(currentFrame, new AppointmentListView(currentUser));
            });

            menuAppt.add(itemNewAppt);
            menuAppt.add(itemViewAll);
        }
        menuBar.add(menuAppt);

        // 3. Patients & Search Menu
        JMenu menuPatients = new JMenu("Patients & History");
        menuPatients.setFont(menuFont);

        JMenuItem itemHistory = new JMenuItem("Patient Search & Medical History");
        itemHistory.setFont(menuFont);
        itemHistory.addActionListener(e -> {
            UIHelper.navigate(currentFrame, new PatientHistoryView(currentUser));
        });

        menuPatients.add(itemHistory);
        menuBar.add(menuPatients);

        // 4. Billing Menu (Admin & Receptionist only)
        if (isAdmin || isReceptionist) {
            JMenu menuBilling = new JMenu("Billing");
            menuBilling.setFont(menuFont);

            JMenuItem itemBill = new JMenuItem("Calculate & Print Patient Bill");
            itemBill.setFont(menuFont);
            itemBill.addActionListener(e -> {
                UIHelper.navigate(currentFrame, new BillView(currentUser, -1));
            });

            menuBilling.add(itemBill);
            menuBar.add(menuBilling);
        }

        // 5. Reports Menu (Admin only)
        if (isAdmin) {
            JMenu menuReports = new JMenu("Reports");
            menuReports.setFont(menuFont);

            JMenuItem itemReports = new JMenuItem("Clinic Analytics & Financial Reports");
            itemReports.setFont(menuFont);
            itemReports.addActionListener(e -> {
                UIHelper.navigate(currentFrame, new ReportsView(currentUser));
            });

            menuReports.add(itemReports);
            menuBar.add(menuReports);

            // 6. Staff & Doctor Management Menu (Admin only)
            JMenu menuStaff = new JMenu("Staff & Doctors");
            menuStaff.setFont(menuFont);

            JMenuItem itemStaff = new JMenuItem("Manage Doctors & Receptionists");
            itemStaff.setFont(menuFont);
            itemStaff.addActionListener(e -> {
                UIHelper.navigate(currentFrame, new ManageStaffView(currentUser));
            });

            menuStaff.add(itemStaff);
            menuBar.add(menuStaff);
        }

        // 7. Help Menu
        JMenu menuHelp = new JMenu("Help");
        menuHelp.setFont(menuFont);

        JMenuItem itemGuide = new JMenuItem("User Guide & Instructions");
        JMenuItem itemAbout = new JMenuItem("About Sunrise Dental System");

        itemGuide.setFont(menuFont);
        itemAbout.setFont(menuFont);

        itemGuide.addActionListener(e -> new HelpView(currentFrame).setVisible(true));
        itemAbout.addActionListener(e -> JOptionPane.showMessageDialog(
            currentFrame,
            "Sunrise Dental Clinic - Appointment & Patient Management System\n" +
            "Version 2.0 (Unified Search & Patient History Edition)\n" +
            "Colombo, Sri Lanka\n\n" +
            "Developed with Java Swing, JDBC & MySQL.",
            "About System",
            JOptionPane.INFORMATION_MESSAGE
        ));

        menuHelp.add(itemGuide);
        menuHelp.add(itemAbout);
        menuBar.add(menuHelp);

        return menuBar;
    }
}

