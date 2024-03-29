package ge.bestline.delivery.ws.services;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import javax.naming.ConfigurationException;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Log4j2
@Service
public class MailService {

    private final FilesStorageService storageService;
    private Properties props = new Properties();

    @Value("${sender.email}")
    private String senderEmail;

    @Value("${sender.email.password}")
    private String senderEmailPass;

    public MailService(FilesStorageService storageService) {
        this.storageService = storageService;
    }

    private void addAttachment(Multipart multipart, String filePath) throws MessagingException {
        DataSource source = new FileDataSource(filePath);
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(source.getName());
        multipart.addBodyPart(messageBodyPart);
    }

    public void sendEmail(String Bcc, String subject, String text, List<String> attachments) throws MessagingException, IOException, ConfigurationException {
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.office365.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        InternetAddress[] myBccList = InternetAddress.parse(Bcc);

        Session session = Session.getDefaultInstance(props,
                new jakarta.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(senderEmail, senderEmailPass);
                    }
                });
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipients(Message.RecipientType.TO, myBccList); // BCC -ში ყრია კლიენტების მეილები

        message.setSubject(MimeUtility.encodeText(subject, "utf-8", "B"));
        message.setHeader("Content-Type", "text/plain; charset=UTF-8");
        Multipart multipart = new MimeMultipart();
        BodyPart textBodyPart = new MimeBodyPart();
//        textBodyPart.setText(text);
        textBodyPart.setContent(text, "text/plain; charset=UTF-8");
        multipart.addBodyPart(textBodyPart);
        if (attachments != null) {
            for (String fileName : attachments) {
                addAttachment(multipart, fileName);
            }
        }
        message.setContent(multipart);

        Transport.send(message);
    }
}
