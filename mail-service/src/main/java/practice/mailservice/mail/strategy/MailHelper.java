package practice.mailservice.mail.strategy;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@PropertySource("classpath:mail.properties")
@Component
@Log4j2
public class MailHelper extends MailSender {

    @Autowired
    protected JavaMailSender javaMailSender;

    @Value("${mail.address}")
    private String mailAddress;

    private MimeMessage mail;
    private MimeMessageHelper helper;


    protected MailHelper setMimeMessage() {
        mail = javaMailSender.createMimeMessage();
        log.info("Created 'MimeMessage' object");
        return this;
    }

    protected MailHelper setMimeMessageHelper() throws MessagingException {
        helper = new MimeMessageHelper(mail, true);
        log.info("Created 'MimeMessageHelper' object");
        return this;
    }

    protected MailHelper setRecipient(String email) throws MessagingException {
        helper.setTo(email);
        log.info("Set recipient: {}", email);
        return this;
    }

    protected MailHelper setFrom() throws MessagingException {
        helper.setFrom(mailAddress);
        log.info("Set sender: {}", mailAddress);
        return this;
    }

    protected MailHelper setSubject(String subject, String orderUuid) throws MessagingException {
        String mailSubject = setMailSubject(subject, orderUuid);
        helper.setSubject(mailSubject);
        log.info("Set subject: {}", mailSubject);
        return this;
    }

    protected MailHelper setContent(String content, boolean html) throws MessagingException {
        helper.setText(content, html);
        log.info("Set content: {}", content);
        return this;
    }

    protected MailHelper setAttachmentIfExist(boolean hasAttachment, String orderUUID, String pathToInvoice) throws MessagingException {
        if (hasAttachment)
            helper.addAttachment(orderUUID, new File(pathToInvoice));

        log.info("Set attachment: {}", hasAttachment);
        return this;
    }

    protected void sendEmail() {
        sendEmail(javaMailSender, mail);
        log.info("Sent complete email to recipient address");
    }


    private String setMailSubject(String subject, String orderUuid) {
        return String.format(subject, orderUuid);
    }
}
