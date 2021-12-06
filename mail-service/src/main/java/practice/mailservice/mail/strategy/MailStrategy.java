package practice.mailservice.mail.strategy;

import practice.mailservice.rabbit.payloads.ConsumerPayload;

import javax.mail.MessagingException;
import java.io.IOException;

public interface MailStrategy {

    void sendEmail(ConsumerPayload consumerPayload) throws MessagingException, IOException;
}
