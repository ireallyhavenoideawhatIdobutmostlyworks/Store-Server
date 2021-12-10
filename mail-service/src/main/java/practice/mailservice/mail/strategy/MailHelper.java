package practice.mailservice.mail.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@PropertySource("classpath:mail.properties")
@RequiredArgsConstructor
@Component
@Log4j2
class MailHelper {

    private final JavaMailSender javaMailSender;

    @Value("${mail.address}")
    private String mailAddress;

    final String CASTED_MESSAGE = "Casted from ConsumerPayload to: '{}' payload";
    final String PREPARED_MESSAGE = "Prepared mail content";
    final String SENT_MESSAGE = "Sent email to {} with data and invoice based on {}-service";

    private MimeMessage mail;
    private MimeMessageHelper helper;


    MailHelper setMimeMessage() {
        mail = javaMailSender.createMimeMessage();
        log.info("Created 'MimeMessage' object");
        return this;
    }

    MailHelper setMimeMessageHelper() throws MessagingException {
        helper = new MimeMessageHelper(mail, true);
        log.info("Created 'MimeMessageHelper' object");
        return this;
    }

    MailHelper setRecipient(String email) throws MessagingException {
        helper.setTo(email);
        log.info("Set recipient: {}", email);
        return this;
    }

    MailHelper setFrom() throws MessagingException {
        helper.setFrom(mailAddress);
        log.info("Set sender: {}", mailAddress);
        return this;
    }

    MailHelper setSubject(String subject, String orderUuid) throws MessagingException {
        String mailSubject = setMailSubject(subject, orderUuid);
        helper.setSubject(mailSubject);
        log.info("Set subject: {}", mailSubject);
        return this;
    }

    MailHelper setContent(String content, boolean html) throws MessagingException {
        helper.setText(content, html);
        log.info("Set content: {}", content);
        return this;
    }

    MailHelper setAttachmentIfExist(boolean hasAttachment, String orderUUID, String pathToInvoice) throws MessagingException {
        if (hasAttachment)
            helper.addAttachment(orderUUID, new File(pathToInvoice));

        log.info("Set attachment: {}", hasAttachment);
        return this;
    }

    void sendEmail() {
        javaMailSender.send(mail);
        log.info("Sent complete email to recipient address");
    }


    private String setMailSubject(String subject, String orderUuid) {
        return String.format(subject, orderUuid);
    }
}
