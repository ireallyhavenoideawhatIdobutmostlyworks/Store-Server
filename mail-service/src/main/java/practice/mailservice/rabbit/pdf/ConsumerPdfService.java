package practice.mailservice.rabbit.pdf;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import practice.mailservice.mail.MailService;

import javax.mail.MessagingException;
import java.io.IOException;

@RequiredArgsConstructor
@Service
public class ConsumerPdfService {

    private final MailService mailService;


    @RabbitListener(queues = "${queue.from.pdf.to.email}")
    public void receivedMessage(ConsumerPdfPayload consumerPdfPayload) throws MessagingException, IOException {
        mailService.sendEmail(consumerPdfPayload);
    }
}
