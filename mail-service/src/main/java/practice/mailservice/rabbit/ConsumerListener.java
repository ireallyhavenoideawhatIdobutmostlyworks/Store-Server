package practice.mailservice.rabbit;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import practice.mailservice.mail.MailService;
import practice.mailservice.mail.strategy.MailBank;
import practice.mailservice.mail.strategy.MailContext;
import practice.mailservice.mail.strategy.MailPdf;
import practice.mailservice.mail.strategy.MailStore;
import practice.mailservice.rabbit.payloads.bank.ConsumerBankPayload;
import practice.mailservice.rabbit.payloads.pdf.ConsumerPdfPayload;
import practice.mailservice.rabbit.payloads.store.ConsumerStorePayload;

import javax.mail.MessagingException;
import java.io.IOException;

@RequiredArgsConstructor
@Service
@Log4j2
public class ConsumerListener {

    private final MailContext mailContext;
    private final MailBank mailBank;
    private final MailStore mailStore;
    private final MailPdf mailPdf;


    @RabbitListener(id = "bank", queues = "${queue.from.bank.to.email}")
    public void receivedMessage(ConsumerBankPayload consumerBankPayload) throws MessagingException, IOException {
        log.info("Consume bankPayload object from bank-service. Payload: {}", consumerBankPayload);
        mailContext.setMailContext(mailBank);
        mailContext.sendEmail(consumerBankPayload);
    }

    @RabbitListener(id = "store", queues = "${queue.from.store.to.email}")
    public void receivedMessage(ConsumerStorePayload consumerStorePayload) throws MessagingException, IOException {
        log.info("Consume storePayload object from store-service. Payload: {}", consumerStorePayload);
        mailContext.setMailContext(mailStore);
        mailContext.sendEmail(consumerStorePayload);
    }

    @RabbitListener(id = "pdf", queues = "${queue.from.pdf.to.email}")
    public void receivedMessage(ConsumerPdfPayload consumerPdfPayload) throws MessagingException, IOException {
        log.info("Consume pdfPayload object from pdf-service. Payload: {}", consumerPdfPayload);
        mailContext.setMailContext(mailPdf);
        mailContext.sendEmail(consumerPdfPayload);
    }
}
