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
public class MailBank implements MailStrategy<ConsumerBankPayload> {

    private final JavaMailSender javaMailSender;

    @Value("${mail.subject.status.order}")
    private String mailSubjectStatusOrder;
    @Value("${mail.content.status.order}")
    private String mailContentStatusOrder;
    @Value("${mail.address}")
    private String mailAddress;


    @Override
    public void sendEmail(ConsumerBankPayload consumerBankPayload) throws MessagingException {
        log.info("Prepare mail content, subject and MimeMessage");

        MimeMessage mail = new MailBuilder(javaMailSender)
                .withSender(mailAddress)
                .withRecipient(consumerBankPayload.getEmail())
                .withContent(prepareContent(consumerBankPayload))
                .withSubject(prepareSubject(consumerBankPayload))
                .build();

        javaMailSender.send(mail);
        log.info("Sent email to {} with data and invoice based on {}-service", consumerBankPayload.getEmail(), MailType.BANK);
    }


    private String prepareContent(ConsumerBankPayload consumerBankPayload) {
        return String.format(
                mailContentStatusOrder,
                consumerBankPayload.getOrderUUID(),
                consumerBankPayload.getPaymentType().toString(),
                consumerBankPayload.getIsPaymentSuccess()
        );
    }

    private String prepareSubject(ConsumerBankPayload consumerBankPayload) {
        return String.format(mailSubjectStatusOrder, consumerBankPayload.getOrderUUID());
    }
}
