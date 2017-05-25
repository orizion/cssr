package ch.fhnw.cssr.mailer;

import java.time.LocalDateTime;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ch.fhnw.cssr.domain.Email;
import ch.fhnw.cssr.domain.repository.EmailRepository;

@Component
public class MailSender {

    // After 3 days, we do not send a mail anymore, it is deprecated anyway
    public static final long MAX_HOURS_TOSEND = 3 * 24; 
    
    public static final String DEPRECATED_ERROR = "Mail is not sent as it is deprecated";

    @Value("${cssr.mail.from}")
    private String from;

    @Value("${cssr.mail.host}")
    private String host;

    @Value("${cssr.mail.password}")
    private String emailPassword;

    @Autowired
    private EmailRepository emailRepo;

    /**
     * Adds a string list of recepients.
     * @param message The emailmessage to call addRecipient on
     * @param type The type (either to or cc)
     * @param addresses The addresses
     * @throws AddressException If the address is invalid
     * @throws MessagingException If the message has an exception
     */
    private void addRecipients(Message message, RecipientType type, String addresses)
            throws AddressException, MessagingException {
        if (addresses == null) {
            return;
        }
        String[] adressList = addresses.split(";");
        for (String address : adressList) {
            message.addRecipient(type, new InternetAddress(address));
        }
    }

    /**
     * Sends all pending mails in the database.
     */
    public void sendAll() {
        Iterable<Email> mails = emailRepo.findNotSent();
        for (Email mail : mails) {
            if (mail.getInsertedAt().plusHours(MAX_HOURS_TOSEND)
                    .compareTo(LocalDateTime.now()) < 0) {
                mail.setError(DEPRECATED_ERROR);
                emailRepo.save(mail);
            } else {
                sendMail(mail);
            }
        }
    }

    /**
     * Sends a single mail.
     * @param mail The email to be sent
     */
    public void sendMail(Email mail) {
        Properties properties = new Properties();
        if (host.contains(":")) {
            properties.put("mail.smtp.host", host.substring(0, host.indexOf(":")));
            properties.put("mail.smtp.port", host.substring(host.indexOf(":" + 1)));
        } else {
            properties.setProperty("mail.smtp.host", host);
        }
        if (emailPassword != null) {
            properties.put("mail.smtp.auth", "true");
        }
        properties.put("mail.transport.protocol", "smtp");

        // Get the default Session object.
        Session session = Session.getInstance(properties,
                emailPassword == null ? null : new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, emailPassword);
                    }
                });

        Transport transport = null;
        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            addRecipients(message, Message.RecipientType.TO, mail.getTo());
            addRecipients(message, Message.RecipientType.BCC, mail.getBcc());
            addRecipients(message, Message.RecipientType.CC, mail.getCc());

            // Set Subject: header field
            message.setSubject(mail.getSubject());

            // Send the actual HTML message, as big as you like
            message.setContent(mail.getBody(), "text/html");

            transport = session.getTransport();
            if (emailPassword != null) {
                transport.connect(from, emailPassword);
            }
            // Send message
            transport.sendMessage(message, message.getAllRecipients());
            System.out.println("Sent message successfully....");
            mail.setSentDate(LocalDateTime.now());
            mail.setTryCount(mail.getTryCount() + 1);

        } catch (Exception ex) {
            mail.setError(ex.toString());
            mail.setTryCount(mail.getTryCount() + 1);
            System.err.println("Error sending mail with subject: " + mail.getSubject());
            System.err.println(ex);
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    e.printStackTrace(); // We ignore it
                }
            }
        }
        emailRepo.save(mail);
    }

}
