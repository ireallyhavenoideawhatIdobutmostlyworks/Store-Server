package practice.mailservice.mail.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import practice.mailservice.rabbit.payloads.ConsumerPayload;
import practice.mailservice.rabbit.payloads.store.ConsumerStorePayload;

import javax.mail.MessagingException;

@PropertySource("classpath:mail.properties")
@RequiredArgsConstructor
@Service
@Log4j2
class MailStore implements MailStrategy {

    private final MailHelper mailHelper;

    @Value("${mail.subject.new.order}")
    private String mailSubjectNewOrder;
    @Value("${mail.content.new.order}")
    private String mailContentNewOrder;


    @Override
    public void sendEmail(ConsumerPayload consumerPayload) throws MessagingException {
        ConsumerStorePayload consumerStorePayload = (ConsumerStorePayload) consumerPayload;
        log.info(mailHelper.CASTED_MESSAGE, MailType.STORE);

        String content = String.format(
                mailContentNewOrder,
                consumerStorePayload.getOrderUUID(),
                consumerStorePayload.getPaymentUUID(),
                consumerStorePayload.getOrderPrice(),
                consumerStorePayload.getAccountNumber()
        );
        log.info(mailHelper.PREPARED_MESSAGE);

        mailHelper.setMimeMessage()
                .setMimeMessageHelper()
                .setRecipient(consumerStorePayload.getEmail())
                .setFrom()
                .setSubject(mailSubjectNewOrder, consumerStorePayload.getOrderUUID())
                .setContent(content, false)
                .setAttachmentIfExist(false, null, null)
                .sendEmail();

        log.info(mailHelper.SENT_MESSAGE, consumerStorePayload.getEmail(), MailType.STORE);
    }
}
