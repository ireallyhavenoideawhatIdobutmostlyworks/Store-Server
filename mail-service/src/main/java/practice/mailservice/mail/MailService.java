package practice.mailservice.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import practice.mailservice.rabbit.payloads.bank.ConsumerBankPayload;
import practice.mailservice.rabbit.payloads.pdf.ConsumerPdfPayload;
import practice.mailservice.rabbit.payloads.store.ConsumerStorePayload;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;

@RequiredArgsConstructor
@PropertySource({
        "classpath:mail.properties",
        "classpath:file.properties"
})
@Service
@Log4j2
public class MailService {

    private final JavaMailSender javaMailSender;

    @Value("${mail.address}")
    private String mailAddress;

    @Value("${mail.subject.new.order}")
    private String mailSubjectNewOrder;
    @Value("${mail.content.new.order}")
    private String mailContentNewOrder;

    @Value("${mail.subject.status.order}")
    private String mailSubjectStatusOrder;
    @Value("${mail.content.status.order}")
    private String mailContentStatusOrder;

    @Value("${mail.subject.invoice}")
    private String mailSubjectInvoice;
    @Value("${mail.content.invoice}")
    private String mailContentInvoice;

    @Value("${output.pdf.path}")
    private String outputPdfPath;


    public void sendEmail(ConsumerStorePayload consumerStorePayload) throws MessagingException {
        MimeMessage mail = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mail, true);
        String subject = String.format(mailSubjectNewOrder, consumerStorePayload.getOrderUUID());
        String content = String.format(mailContentNewOrder, consumerStorePayload.getOrderUUID(), consumerStorePayload.getPaymentUUID(), consumerStorePayload.getOrderPrice(), consumerStorePayload.getAccountNumber());
        setEmailDetails(helper, consumerStorePayload.getEmail(), subject, content);

        javaMailSender.send(mail);
        log.info("Send email to {} with data based on store-service", consumerStorePayload.getEmail());
    }

    public void sendEmail(ConsumerBankPayload consumerBankPayload) throws MessagingException {
        MimeMessage mail = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mail, true);
        String subject = String.format(mailSubjectStatusOrder, consumerBankPayload.getOrderUUID());
        String content = String.format(mailContentStatusOrder, consumerBankPayload.getOrderUUID(), consumerBankPayload.getPaymentType().toString(), consumerBankPayload.getIsPaymentSuccess());
        setEmailDetails(helper, consumerBankPayload.getEmail(), subject, content);

        javaMailSender.send(mail);
        log.info("Send email to {} with data based on bank-service", consumerBankPayload.getEmail());
    }

    public void sendEmail(ConsumerPdfPayload consumerPdfPayload) throws MessagingException, IOException {
        String pathToInvoice = String.format(outputPdfPath, consumerPdfPayload.getOrderUUID());
        createPdfInvoice(pathToInvoice, consumerPdfPayload.getFileData());

        MimeMessage mail = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mail, true);
        helper.addAttachment(consumerPdfPayload.getOrderUUID(), new File(pathToInvoice));
        String subject = String.format(mailSubjectInvoice, consumerPdfPayload.getOrderUUID());
        String content = String.format(mailContentInvoice, consumerPdfPayload.getOrderUUID());
        setEmailDetails(helper, consumerPdfPayload.getEmail(), subject, content);

        javaMailSender.send(mail);
        log.info("Send email to {} with data and invoice based on pdf-service", consumerPdfPayload.getEmail());
    }


    private void createPdfInvoice(String pathToInvoice, byte[] invoiceAsByte) throws IOException {
        FileUtils.writeByteArrayToFile(new File(pathToInvoice), invoiceAsByte);
        log.info("Create invoice as pdf file. Path: {}", pathToInvoice);
    }

    private void setEmailDetails(MimeMessageHelper helper, String email, String subject, String content) throws MessagingException {
        helper.setTo(email);
        helper.setFrom(mailAddress);
        helper.setSubject(subject);
        helper.setText(content, false);
    }
}