package practice.mailservice.mail.strategy;

import javax.mail.MessagingException;
import java.io.IOException;

public interface MailStrategy<T> {

    void sendEmail(T t) throws MessagingException, IOException;
}
