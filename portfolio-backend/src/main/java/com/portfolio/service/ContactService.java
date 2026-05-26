package com.portfolio.service;

import com.portfolio.entity.ContactMessage;
import com.portfolio.repository.ContactRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final JavaMailSender mailSender;

    @Value("${portfolio.owner.email}")
    private String ownerEmail;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public ContactService(ContactRepository contactRepository, JavaMailSender mailSender) {
        this.contactRepository = contactRepository;
        this.mailSender = mailSender;
    }

    public void save(ContactMessage message) {
        contactRepository.save(message);
        sendOwnerNotification(message);
        sendAcknowledgement(message);
    }

    // ── Notification email to YOU ─────────────────────────────────────────────
    private void sendOwnerNotification(ContactMessage msg) {
        try {
            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mail, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(ownerEmail);
            helper.setReplyTo(msg.getEmail());
            helper.setSubject("New Portfolio Message from " + msg.getName());

            // NOTE: %% is used wherever CSS needs a literal % (e.g. width:100%%)
            // Only %s is a real placeholder
            String html = String.format(
                    "<div style='font-family:Segoe UI,sans-serif;max-width:600px;margin:auto;" +
                            "background:#09090f;color:#e8e8f0;border-radius:12px;overflow:hidden;'>" +

                            "  <div style='background:#0f0f1a;padding:28px 32px;border-bottom:1px solid #1e1e2e;'>" +
                            "    <span style='font-family:monospace;font-size:22px;font-weight:700;'>" +
                            "      <span style='color:#00e5a0'>&lt;</span>VT<span style='color:#00e5a0'>/&gt;</span>" +
                            "    </span>" +
                            "    <span style='color:#6b6b88;font-size:13px;margin-left:12px;font-family:monospace;'>" +
                            "      Portfolio Contact Notification" +
                            "    </span>" +
                            "  </div>" +

                            "  <div style='padding:32px;'>" +
                            "    <p style='color:#00e5a0;font-family:monospace;font-size:12px;letter-spacing:.1em;margin:0 0 20px;'>" +
                            "      // NEW_MESSAGE_RECEIVED" +
                            "    </p>" +

                            "    <table style='width:100%%;border-collapse:collapse;'>" +
                            "      <tr>" +
                            "        <td style='padding:10px 0;border-bottom:1px solid #1e1e2e;color:#6b6b88;" +
                            "                   font-family:monospace;font-size:12px;width:100px;'>NAME</td>" +
                            "        <td style='padding:10px 0;border-bottom:1px solid #1e1e2e;" +
                            "                   color:#e8e8f0;font-size:15px;font-weight:600;'>%s</td>" +
                            "      </tr>" +
                            "      <tr>" +
                            "        <td style='padding:10px 0;border-bottom:1px solid #1e1e2e;" +
                            "                   color:#6b6b88;font-family:monospace;font-size:12px;'>EMAIL</td>" +
                            "        <td style='padding:10px 0;border-bottom:1px solid #1e1e2e;'>" +
                            "          <a href='mailto:%s' style='color:#00aaff;font-size:14px;'>%s</a>" +
                            "        </td>" +
                            "      </tr>" +
                            "      <tr>" +
                            "        <td style='padding:14px 0 0;color:#6b6b88;font-family:monospace;" +
                            "                   font-size:12px;vertical-align:top;'>MESSAGE</td>" +
                            "        <td style='padding:14px 0 0;color:#e8e8f0;font-size:14px;line-height:1.7;'>%s</td>" +
                            "      </tr>" +
                            "    </table>" +

                            "    <div style='margin-top:28px;text-align:center;'>" +
                            "      <a href='mailto:%s?subject=Re: Your portfolio message'" +
                            "         style='background:#00e5a0;color:#000;font-weight:700;" +
                            "                font-family:monospace;font-size:13px;padding:12px 28px;" +
                            "                border-radius:6px;text-decoration:none;display:inline-block;'>" +
                            "        Reply to %s" +
                            "      </a>" +
                            "    </div>" +
                            "  </div>" +

                            "  <div style='background:#0f0f1a;padding:16px 32px;text-align:center;border-top:1px solid #1e1e2e;'>" +
                            "    <span style='color:#6b6b88;font-family:monospace;font-size:11px;'>" +
                            "      Sent from your portfolio · vidhantayade505@gmail.com" +
                            "    </span>" +
                            "  </div>" +
                            "</div>",
                    msg.getName(),
                    msg.getEmail(), msg.getEmail(),
                    msg.getMessage().replace("\n", "<br>"),
                    msg.getEmail(),
                    msg.getName()
            );

            helper.setText(html, true);
            mailSender.send(mail);

        } catch (MessagingException e) {
            System.err.println("Failed to send owner notification: " + e.getMessage());
        }
    }

    // ── Acknowledgement email to the PERSON who contacted you ────────────────
    private void sendAcknowledgement(ContactMessage msg) {
        try {
            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mail, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(msg.getEmail());
            helper.setSubject("Thanks for reaching out, " + msg.getName() + "!");

            String html = String.format(
                    "<div style='font-family:Segoe UI,sans-serif;max-width:600px;margin:auto;" +
                            "background:#09090f;color:#e8e8f0;border-radius:12px;overflow:hidden;'>" +

                            "  <div style='background:#0f0f1a;padding:28px 32px;border-bottom:1px solid #1e1e2e;'>" +
                            "    <span style='font-family:monospace;font-size:22px;font-weight:700;'>" +
                            "      <span style='color:#00e5a0'>&lt;</span>VT<span style='color:#00e5a0'>/&gt;</span>" +
                            "    </span>" +
                            "  </div>" +

                            "  <div style='padding:32px;'>" +
                            "    <h2 style='margin:0 0 12px;font-size:20px;'>Hey %s!</h2>" +
                            "    <p style='color:#6b6b88;line-height:1.7;margin:0 0 20px;'>" +
                            "      Thanks for getting in touch. I've received your message and will" +
                            "      get back to you as soon as possible — usually within 24 hours." +
                            "    </p>" +

                            "    <div style='background:#0f0f1a;border:1px solid #1e1e2e;border-radius:8px;" +
                            "                padding:20px;margin-bottom:24px;'>" +
                            "      <p style='color:#00e5a0;font-family:monospace;font-size:11px;" +
                            "                 letter-spacing:.1em;margin:0 0 10px;'>// YOUR_MESSAGE</p>" +
                            "      <p style='color:#e8e8f0;font-size:14px;line-height:1.7;margin:0;'>%s</p>" +
                            "    </div>" +

                            "    <p style='color:#6b6b88;font-size:13px;line-height:1.7;margin:0;'>" +
                            "      In the meantime, feel free to check out my work on " +
                            "      <a href='https://github.com/Vidhan-Tayade' style='color:#00aaff;'>GitHub</a>" +
                            "      or connect on " +
                            "      <a href='https://linkedin.com/in/vidhan-tayade-1859622a0/' style='color:#00aaff;'>LinkedIn</a>." +
                            "    </p>" +
                            "  </div>" +

                            "  <div style='background:#0f0f1a;padding:16px 32px;text-align:center;border-top:1px solid #1e1e2e;'>" +
                            "    <span style='color:#6b6b88;font-family:monospace;font-size:11px;'>" +
                            "      Vidhan Tayade · vidhantayade505@gmail.com · Indore, M.P., India" +
                            "    </span>" +
                            "  </div>" +
                            "</div>",
                    msg.getName(),
                    msg.getMessage().replace("\n", "<br>")
            );

            helper.setText(html, true);
            mailSender.send(mail);

        } catch (MessagingException e) {
            System.err.println("Failed to send acknowledgement: " + e.getMessage());
        }
    }
}