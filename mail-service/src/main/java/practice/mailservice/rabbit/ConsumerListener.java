package practice.mailservice.rabbit;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import practice.mailservice.mail.strategy.MailStrategy;
import practice.mailservice.mail.strategy.MailStrategyFactory;
import practice.mailservice.mail.strategy.MailType;
import practice.mailservice.rabbit.payloads.bank.ConsumerBankPayload;
import practice.mailservice.rabbit.payloads.pdf.ConsumerPdfPayload;
import practice.mailservice.rabbit.payloads.store.ConsumerStorePayload;

import javax.mail.MessagingException;
import java.io.IOException;

@RequiredArgsConstructor
@Service
@Log4j2
public class ConsumerListener {

    private final String CONSUME_MESSAGE = "Consume {} object from {}-service. Payload: {}";
    private final MailStrategyFactory mailStrategyFactory;


    @RabbitListener(id = "bank", queues = "${queue.from.bank.to.email}")
    public void receivedMessage(ConsumerBankPayload consumerBankPayload) throws MessagingException, IOException {
        log.info(CONSUME_MESSAGE, "bankPayload", "bank", consumerBankPayload);

        MailStrategy mailStrategy = mailStrategyFactory.getStrategy(MailType.BANK);
        mailStrategy.sendEmail(consumerBankPayload);
    }

    @RabbitListener(id = "store", queues = "${queue.from.store.to.email}")
    public void receivedMessage(ConsumerStorePayload consumerStorePayload) throws MessagingException, IOException {
        log.info(CONSUME_MESSAGE, "storePayload", "store", consumerStorePayload);

        MailStrategy mailStrategy = mailStrategyFactory.getStrategy(MailType.STORE);
        mailStrategy.sendEmail(consumerStorePayload);
    }

    @RabbitListener(id = "pdf", queues = "${queue.from.pdf.to.email}")
    public void receivedMessage(ConsumerPdfPayload consumerPdfPayload) throws MessagingException, IOException {
        log.info(CONSUME_MESSAGE, "pdfPayload", "pdf", consumerPdfPayload);

        MailStrategy mailStrategy = mailStrategyFactory.getStrategy(MailType.PDF);
        mailStrategy.sendEmail(consumerPdfPayload);
    }
}
