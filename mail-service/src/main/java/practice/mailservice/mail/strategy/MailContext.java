package practice.mailservice.mail.strategy;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import practice.mailservice.rabbit.payloads.ConsumerPayload;

import javax.mail.MessagingException;
import java.io.IOException;

@Component
@Log4j2
public class MailContext {

    private MailStrategy mailStrategy;


    public void setMailContext(MailStrategy mailStrategy) {
        this.mailStrategy = mailStrategy;
        log.info("Looked for new mail context");
    }

    public void sendEmail(ConsumerPayload consumerPayload) throws MessagingException, IOException {
        mailStrategy.sendEmail(consumerPayload);
    }
}
