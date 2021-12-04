package practice.mailservice.rabbit.bank;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import practice.mailservice.mail.MailService;

import javax.mail.MessagingException;

@RequiredArgsConstructor
@Service
@Log4j2
public class ConsumerBankService {

    private final MailService mailService;


    @RabbitListener(id = "bank", queues = "${queue.from.bank.to.email}")
    public void receivedMessage(ConsumerBankPayload consumerBankPayload) throws MessagingException {
        log.info("Consume bankPayload object from bank-service. Payload: {}", consumerBankPayload);
        mailService.sendEmail(consumerBankPayload);
    }
}
