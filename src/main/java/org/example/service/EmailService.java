package org.example.service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Sends email notifications to patients for appointment confirmation,
 * cancellation, and billing events. All send methods fail silently
 * (log only) so email problems never block core app functionality.
 */
public class EmailService {

    // ===== REPLACE THESE TWO LINES WITH YOUR OWN GMAIL DETAILS =====
    private static final String SENDER_EMAIL = "amalpersonal2025@gmail.com";
    private static final String SENDER_APP_PASSWORD = "pazksgwdjbmleuwz";
    // =================================================================

    private Session buildSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        return Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_APP_PASSWORD);
            }
        });
    }

    private void sendEmail(String toEmail, String subject, String body) {
        if (toEmail == null || toEmail.trim().isEmpty()) {
            System.out.println("[Email] Skipped - no email address on file for this patient.");
            return;
        }
        try {
            Session session = buildSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "Sunrise Dental Clinic"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            System.out.println("[Email] Sent successfully to " + toEmail);
        } catch (Exception e) {
            System.out.println("[Email] Failed to send (app continues normally): " + e.getMessage());
        }
    }

    public void sendConfirmationEmail(String toEmail, String patientName, int apptNo, String date, String time, String dentist) {
        String subject = "Appointment Confirmed - Sunrise Dental Clinic";
        String body = "Dear " + patientName + ",\n\n"
                + "Your appointment #" + apptNo + " has been CONFIRMED by " + dentist + ".\n\n"
                + "Date: " + date + "\n"
                + "Time: " + time + "\n\n"
                + "Thank you for choosing Sunrise Dental Clinic.\n";
        sendEmail(toEmail, subject, body);
    }

    public void sendCancellationEmail(String toEmail, String patientName, int apptNo, String reason) {
        String subject = "Appointment Cancelled - Sunrise Dental Clinic";
        String body = "Dear " + patientName + ",\n\n"
                + "Your appointment #" + apptNo + " has been CANCELLED.\n\n"
                + "Reason: " + reason + "\n\n"
                + "Please contact us to reschedule.\n\n"
                + "Sunrise Dental Clinic\n";
        sendEmail(toEmail, subject, body);
    }

    public void sendBillEmail(String toEmail, String patientName, int apptNo, double total) {
        String subject = "Bill Confirmed - Sunrise Dental Clinic";
        String body = "Dear " + patientName + ",\n\n"
                + "Your bill for appointment #" + apptNo + " has been confirmed.\n\n"
                + "Total Amount: LKR " + total + "\n\n"
                + "Thank you for choosing Sunrise Dental Clinic.\n";
        sendEmail(toEmail, subject, body);
    }
}