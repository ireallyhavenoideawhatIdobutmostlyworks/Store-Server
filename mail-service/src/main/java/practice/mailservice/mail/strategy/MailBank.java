package practice.mailservice.mail.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import practice.mailservice.rabbit.payloads.ConsumerPayload;
import practice.mailservice.rabbit.payloads.bank.ConsumerBankPayload;

import javax.mail.MessagingException;

@PropertySource("classpath:mail.properties")
@RequiredArgsConstructor
@Service
@Log4j2
class MailBank implements MailStrategy {

    private final MailHelper mailHelper;

    @Value("${mail.subject.status.order}")
    private String mailSubjectStatusOrder;
    @Value("${mail.content.status.order}")
    private String mailContentStatusOrder;


    @Override
    public void sendEmail(ConsumerPayload consumerPayload) throws MessagingException {
        ConsumerBankPayload consumerBankPayload = (ConsumerBankPayload) consumerPayload;
        log.info(mailHelper.CASTED_MESSAGE, MailType.BANK);

        String content = String.format(
                mailContentStatusOrder,
                consumerBankPayload.getOrderUUID(),
                consumerBankPayload.getPaymentType().toString(),
                consumerBankPayload.getIsPaymentSuccess()
        );
        log.info(mailHelper.PREPARED_MESSAGE);

        mailHelper.setMimeMessage()
                .setMimeMessageHelper()
                .setRecipient(consumerBankPayload.getEmail())
                .setFrom()
                .setSubject(mailSubjectStatusOrder, consumerBankPayload.getOrderUUID())
                .setContent(content, false)
                .setAttachmentIfExist(false, null, null)
                .sendEmail();

        log.info(mailHelper.SENT_MESSAGE, consumerBankPayload.getEmail(), MailType.BANK);
    }
}
