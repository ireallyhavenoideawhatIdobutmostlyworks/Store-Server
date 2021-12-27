package practice.mailservice.mail.strategy;

import javax.mail.MessagingException;
import java.io.IOException;

public interface MailStrategy<PAYLOAD> {

    void sendEmail(PAYLOAD payload) throws MessagingException, IOException;
}
