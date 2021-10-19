package practice.mailservice.rabbit.store;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import practice.mailservice.mail.MailService;

import javax.mail.MessagingException;

@RequiredArgsConstructor
@Service
public class ConsumerStoreService {

    private final MailService mailService;


    @RabbitListener(queues = "${queue.from.store.to.email}")
    public void receivedMessage(ConsumerStorePayload consumerStorePayload) throws MessagingException {
        mailService.sendEmail(consumerStorePayload);
    }
}