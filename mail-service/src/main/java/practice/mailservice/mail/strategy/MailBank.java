package practice.mailservice.mail.strategy;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import practice.mailservice.rabbit.payloads.ConsumerPayload;
import practice.mailservice.rabbit.payloads.bank.ConsumerBankPayload;

import javax.mail.MessagingException;

@PropertySource("classpath:file.properties")
@Service
@Log4j2
public class MailBank extends MailHelper implements MailStrategy {

    @Value("${mail.subject.status.order}")
    private String mailSubjectStatusOrder;
    @Value("${mail.content.status.order}")
    private String mailContentStatusOrder;


    @Override
    public void sendEmail(ConsumerPayload consumerPayload) throws MessagingException {
        ConsumerBankPayload consumerBankPayload = (ConsumerBankPayload) consumerPayload;
        log.info("Casted 'ConsumerPayload'");

        String content = String.format(
                mailContentStatusOrder,
                consumerBankPayload.getOrderUUID(),
                consumerBankPayload.getPaymentType().toString(),
                consumerBankPayload.getIsPaymentSuccess()
        );
        log.info("Prepared mail content");

        setMimeMessage()
                .setMimeMessageHelper()
                .setRecipient(consumerBankPayload.getEmail())
                .setFrom()
                .setSubject(mailSubjectStatusOrder, consumerBankPayload.getOrderUUID())
                .setContent(content, false)
                .setAttachmentIfExist(false, null, null)
                .sendEmail();

        log.info("Sent email to {} with data based on bank-service", consumerBankPayload.getEmail());
    }
}
