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
class ConsumerListener {

    private final String CONSUME_MESSAGE = "Consume payload from {}-service. Payload details: {}";
    private final MailStrategyFactory mailStrategyFactory;


    @RabbitListener(id = "${bank.id}", queues = "${queue.from.bank.to.email}")
    void receivedMessage(ConsumerBankPayload consumerBankPayload) throws MessagingException, IOException {
        log.info(CONSUME_MESSAGE, MailType.BANK, consumerBankPayload);

        MailStrategy mailStrategy = mailStrategyFactory.getStrategy(MailType.BANK);
        mailStrategy.sendEmail(consumerBankPayload);
    }

    @RabbitListener(id = "${store.id}", queues = "${queue.from.store.to.email}")
    void receivedMessage(ConsumerStorePayload consumerStorePayload) throws MessagingException, IOException {
        log.info(CONSUME_MESSAGE, MailType.STORE, consumerStorePayload);

        MailStrategy mailStrategy = mailStrategyFactory.getStrategy(MailType.STORE);
        mailStrategy.sendEmail(consumerStorePayload);
    }

    @RabbitListener(id = "${pdf.id}", queues = "${queue.from.pdf.to.email}")
    void receivedMessage(ConsumerPdfPayload consumerPdfPayload) throws MessagingException, IOException {
        log.info(CONSUME_MESSAGE, MailType.PDF, consumerPdfPayload);

        MailStrategy mailStrategy = mailStrategyFactory.getStrategy(MailType.PDF);
        mailStrategy.sendEmail(consumerPdfPayload);
    }
}
