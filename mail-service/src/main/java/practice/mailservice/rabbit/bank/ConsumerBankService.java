package practice.mailservice.rabbit.bank;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import practice.mailservice.mail.MailService;

import javax.mail.MessagingException;

@RequiredArgsConstructor
@Service
public class ConsumerBankService {

    private final MailService mailService;


    @RabbitListener(queues = "${queue.from.bank.to.email}")
    public void receivedMessage(ConsumerBankPayload consumerBankPayload) throws MessagingException {
        mailService.sendEmail(consumerBankPayload);
    }
}
