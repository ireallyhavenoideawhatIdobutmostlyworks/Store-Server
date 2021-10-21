package practice.mailservice.rabbit.pdf;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import practice.mailservice.mail.MailService;

import javax.mail.MessagingException;
import java.io.IOException;

@RequiredArgsConstructor
@Service
@Log4j2
public class ConsumerPdfService {

    private final MailService mailService;


    @RabbitListener(queues = "${queue.from.pdf.to.email}")
    public void receivedMessage(ConsumerPdfPayload consumerPdfPayload) throws MessagingException, IOException {
        log.info("Consume pdfPayload object from pdf-service. Payload: {}", consumerPdfPayload);
        mailService.sendEmail(consumerPdfPayload);
    }
}
