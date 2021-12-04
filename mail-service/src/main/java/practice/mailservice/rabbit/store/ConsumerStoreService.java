package practice.mailservice.rabbit.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import practice.mailservice.mail.MailService;

import javax.mail.MessagingException;

@RequiredArgsConstructor
@Service
@Log4j2
public class ConsumerStoreService {

    private final MailService mailService;


    @RabbitListener(id = "store", queues = "${queue.from.store.to.email}")
    public void receivedMessage(ConsumerStorePayload consumerStorePayload) throws MessagingException {
        log.info("Consume storePayload object from store-service. Payload: {}", consumerStorePayload);
        mailService.sendEmail(consumerStorePayload);
    }
}