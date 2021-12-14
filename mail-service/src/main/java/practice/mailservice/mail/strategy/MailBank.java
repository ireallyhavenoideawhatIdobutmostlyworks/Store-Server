package practice.mailservice.mail.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import practice.mailservice.rabbit.payloads.bank.ConsumerBankPayload;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@PropertySource("classpath:mail.properties")
@RequiredArgsConstructor
@Service
@Log4j2
public
class MailBank implements MailStrategy<ConsumerBankPayload> {

    private final JavaMailSender javaMailSender;

    @Value("${mail.subject.status.order}")
    private String mailSubjectStatusOrder;
    @Value("${mail.content.status.order}")
    private String mailContentStatusOrder;
    @Value("${mail.address}")
    private String mailAddress;


    @Override
    public void sendEmail(ConsumerBankPayload consumerBankPayload) throws MessagingException {
        String content = String.format(
                mailContentStatusOrder,
                consumerBankPayload.getOrderUUID(),
                consumerBankPayload.getPaymentType().toString(),
                consumerBankPayload.getIsPaymentSuccess()
        );
        log.info("Prepared mail content");

        MimeMessage mail = new MailBuilder(javaMailSender)
                .withSender(mailAddress)
                .withRecipient(consumerBankPayload.getEmail())
                .withContent(content, false)
                .withSubject(mailSubjectStatusOrder, consumerBankPayload.getOrderUUID())
                .withAttachmentIfExist(false, null, null)
                .build();

        javaMailSender.send(mail);
        log.info("Sent email to {} with data and invoice based on {}-service", consumerBankPayload.getEmail(), MailType.BANK);
    }
}
