package practice.mailservice.mail;

import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

public class MimeMessageHelperBuilder {

    private MimeMessageHelper helper;


    public MimeMessageHelperBuilder prepareHelper(MimeMessage mail, boolean isMultipart) throws MessagingException {
        helper = new MimeMessageHelper(mail, isMultipart);
        return this;
    }

    public MimeMessageHelperBuilder setTo(String to) throws MessagingException {
        helper.setTo(to);
        return this;
    }

    public MimeMessageHelperBuilder setFrom(String from) throws MessagingException {
        helper.setFrom(from);
        return this;
    }

    public MimeMessageHelperBuilder setSubject(String subject) throws MessagingException {
        helper.setSubject(subject);
        return this;
    }

    public MimeMessageHelperBuilder setContent(String content, boolean html) throws MessagingException {
        helper.setText(content, html);
        return this;
    }

    public void addAttachment(String attachmentFilename, File file) throws MessagingException {
        helper.addAttachment(attachmentFilename, file);
    }
}
