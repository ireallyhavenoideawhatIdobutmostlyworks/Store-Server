package practice.mailservice.mail.strategy;

import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;

public abstract class MailSender {


    protected void sendEmail(JavaMailSender javaMailSender, MimeMessage mail) {
        javaMailSender.send(mail);
    }
}
