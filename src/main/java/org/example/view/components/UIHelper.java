package org.example.view.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;
import org.example.model.User;

/**
 * UIHelper - Centralized Design & Styling Utility
 * 
 * Provides consistent, high-contrast, beautiful Swing UI components
 * that eliminate Windows Look & Feel button rendering bugs (e.g. invisible white-on-white text).
 */

public class UIHelper {

    // Primary Brand Palette
    public static final Color COLOR_PRIMARY = new Color(24, 90, 157);       // Dental Blue
    public static final Color COLOR_PRIMARY_HOVER = new Color(18, 70, 125);
    public static final Color COLOR_SUCCESS = new Color(38, 140, 60);        // Emerald Green
    public static final Color COLOR_DANGER = new Color(195, 35, 35);         // Crimson Red
    public static final Color COLOR_WARNING = new Color(220, 110, 0);        // Amber Orange
    public static final Color COLOR_DARK_TEXT = new Color(30, 41, 59);       // Slate Dark
    public static final Color COLOR_LIGHT_BG = new Color(245, 248, 252);     // Crisp Light Blue-Gray
    public static final Color COLOR_BORDER = new Color(200, 215, 230);

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    /**
     * Creates a custom styled button guaranteed to paint with high contrast
     * and crisp readable text across all Windows Look & Feels.
     */
    public static JButton createStyledButton(String text, Color bgColor, Color fgColor, boolean isBold, Dimension prefSize) {
        JButton btn = new JButton(text);
        btn.setFont(isBold ? FONT_BOLD : FONT_REGULAR);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        if (prefSize != null) {
            btn.setPreferredSize(prefSize);
        }

        // Custom UI delegate to ensure background color and text contrast always paint correctly
        btn.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, javax.swing.JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                JButton b = (JButton) c;
                Color bg = bgColor;
                if (!b.isEnabled()) {
                    bg = new Color(220, 225, 230);
                } else if (b.getModel().isPressed()) {
                    bg = bgColor.darker();
                } else if (b.getModel().isRollover()) {
                    bg = new Color(
                        Math.min(255, bgColor.getRed() + 20),
                        Math.min(255, bgColor.getGreen() + 20),
                        Math.min(255, bgColor.getBlue() + 20)
                    );
                }

                g2.setColor(bg);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 6, 6);

                // Subtle border
                if (bgColor.equals(Color.WHITE) || bgColor.equals(COLOR_LIGHT_BG)) {
                    g2.setColor(new Color(180, 195, 210));
                } else {
                    g2.setColor(bg.darker());
                }
                g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 6, 6);

                g2.dispose();
                super.paint(g, c);
            }
        });

        btn.setForeground(fgColor);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        return btn;
    }

    public static JButton createPrimaryButton(String text, Dimension prefSize) {
        return createStyledButton(text, COLOR_PRIMARY, Color.WHITE, true, prefSize);
    }

    public static JButton createSuccessButton(String text, Dimension prefSize) {
        return createStyledButton(text, COLOR_SUCCESS, Color.WHITE, true, prefSize);
    }

    public static JButton createDangerButton(String text, Dimension prefSize) {
        return createStyledButton(text, COLOR_DANGER, Color.WHITE, true, prefSize);
    }

    public static JButton createSecondaryButton(String text, Dimension prefSize) {
        return createStyledButton(text, Color.WHITE, COLOR_DARK_TEXT, false, prefSize);
    }

    /**
     * Navigates from currentFrame to nextFrame while preserving the window's
     * maximized/minimized state, dimensions, and screen position.
     */
    public static void navigate(JFrame currentFrame, JFrame nextFrame) {
        if (currentFrame != null) {
            int state = currentFrame.getExtendedState();
            if ((state & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
                nextFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            } else {
                nextFrame.setSize(currentFrame.getSize());
                nextFrame.setLocation(currentFrame.getLocation());
            }
            currentFrame.dispose();
        }
        nextFrame.setVisible(true);
    }

    public static JButton createActionButton(String text) {
        JButton btn = createStyledButton(text, COLOR_LIGHT_BG, COLOR_PRIMARY, true, null);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 205, 230), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return btn;
    }

    /**
     * Creates a consistent, elegant Top Header Banner for all screens in the application.
     */
    public static JPanel createHeaderBanner(String title, String subtitle, User currentUser) {
        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        headerPanel.setBackground(COLOR_PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        // Top Row: Title
        JLabel lblTitle = new JLabel(title != null ? title.toUpperCase() : "SUNRISE DENTAL CLINIC", SwingConstants.CENTER);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        // Subtitle + User details
        String userDisplay = (currentUser != null) ? currentUser.getFullName() + " (" + currentUser.getRole() + ")" : "Staff Member";
        String sub = (subtitle != null && !subtitle.trim().isEmpty())
            ? subtitle + " | Logged in as: " + userDisplay
            : "Sunrise Dental Clinic Colombo | Logged in as: " + userDisplay + " | System Online";

        JLabel lblSubtitle = new JLabel(sub, SwingConstants.CENTER);
        lblSubtitle.setFont(FONT_SUBTITLE);
        lblSubtitle.setForeground(new Color(220, 235, 252));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        return headerPanel;
    }
}
