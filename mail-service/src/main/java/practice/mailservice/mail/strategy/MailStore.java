package practice.mailservice.mail.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import practice.mailservice.rabbit.payloads.store.ConsumerStorePayload;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@PropertySource("classpath:mail.properties")
@RequiredArgsConstructor
@Service
@Log4j2
public class MailStore implements MailStrategy<ConsumerStorePayload> {

    private final JavaMailSender javaMailSender;

    @Value("${mail.subject.new.order}")
    private String mailSubjectNewOrder;
    @Value("${mail.content.new.order}")
    private String mailContentNewOrder;
    @Value("${mail.address}")
    private String mailAddress;


    @Override
    public void sendEmail(ConsumerStorePayload consumerStorePayload) throws MessagingException {
        String content = String.format(
                mailContentNewOrder,
                consumerStorePayload.getOrderUUID(),
                consumerStorePayload.getPaymentUUID(),
                consumerStorePayload.getOrderPrice(),
                consumerStorePayload.getAccountNumber()
        );
        String subject = String.format(mailSubjectNewOrder, consumerStorePayload.getOrderUUID());
        log.info("Prepared mail content");

        MimeMessage mail = new MailBuilder(javaMailSender)
                .withSender(mailAddress)
                .withRecipient(consumerStorePayload.getEmail())
                .withContent(content, false)
                .withSubject(subject)
                .build();

        javaMailSender.send(mail);
        log.info("Sent email to {} with data and invoice based on {}-service", consumerStorePayload.getEmail(), MailType.STORE);
    }
}
