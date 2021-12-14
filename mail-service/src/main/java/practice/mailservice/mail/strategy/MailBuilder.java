package practice.mailservice.mail.strategy;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

public class MailBuilder {

    private MimeMessage mimeMessage;
    private MimeMessageHelper mimeMessageHelper;

    public MailBuilder(JavaMailSender javaMailSender) throws MessagingException {
        mimeMessage = javaMailSender.createMimeMessage();
        mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
    }

    MailBuilder withRecipient(String recipient) throws MessagingException {
        mimeMessageHelper.setTo(recipient);
        return this;
    }

    MailBuilder withSender(String sender) throws MessagingException {
        mimeMessageHelper.setFrom(sender);
        return this;
    }

    MailBuilder withSubject(String subject, String orderUuid) throws MessagingException {
        String mailSubject = setMailSubject(subject, orderUuid);
        mimeMessageHelper.setSubject(mailSubject);
        return this;
    }

    MailBuilder withContent(String content, boolean html) throws MessagingException {
        mimeMessageHelper.setText(content, html);
        return this;
    }

    MailBuilder withAttachmentIfExist(boolean hasAttachment, String orderUUID, String pathToInvoice) throws MessagingException {
        if (hasAttachment)
            mimeMessageHelper.addAttachment(orderUUID, new File(pathToInvoice));
        return this;
    }

    public MimeMessage build() {
        return mimeMessage;
    }


    private String setMailSubject(String subject, String orderUuid) {
        return String.format(subject, orderUuid);
    }
}
