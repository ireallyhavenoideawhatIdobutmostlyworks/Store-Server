package practice.mailservice.mail;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import practice.mailservice.rabbit.bank.ConsumerBankPayload;
import practice.mailservice.rabbit.pdf.ConsumerPdfPayload;
import practice.mailservice.rabbit.store.ConsumerStorePayload;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;

@PropertySource({
        "classpath:mail.properties",
        "classpath:file.properties"
})
@Service
public class MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${app.mail.address}")
    private String appMailAddress;

    @Value("${app.mail.subject.new.order}")
    private String appMailSubjectNewOrder;
    @Value("${app.mail.content.new.order}")
    private String appMailContentNewOrder;

    @Value("${app.mail.subject.status.order}")
    private String appMailSubjectStatusOrder;
    @Value("${app.mail.content.status.order}")
    private String appMailContentStatusOrder;

    @Value("${app.mail.subject.invoice}")
    private String appMailSubjectInvoice;
    @Value("${app.mail.content.invoice}")
    private String appMailContentInvoice;

    @Value("${output.pdf.path}")
    private String outputPdfPath;


    public void sendEmail(ConsumerStorePayload consumerStorePayload) throws MessagingException {
        MimeMessage mail = javaMailSender.createMimeMessage();

        String content = String.format(appMailContentNewOrder, consumerStorePayload.getOrderUUID(), consumerStorePayload.getPaymentUUID(), consumerStorePayload.getOrderPrice(), consumerStorePayload.getAccountNumber());
        String subject = String.format(appMailSubjectNewOrder, consumerStorePayload.getOrderUUID());
        mimeMessageHelperBuilderWithoutAttachment(mail, consumerStorePayload.getEmail(), appMailAddress, subject, content);

        javaMailSender.send(mail);
    }

    public void sendEmail(ConsumerBankPayload consumerBankPayload) throws MessagingException {
        MimeMessage mail = javaMailSender.createMimeMessage();

        String content = String.format(appMailContentStatusOrder, consumerBankPayload.getOrderUUID(), consumerBankPayload.getPaymentType().toString(), consumerBankPayload.getIsPaymentSuccess());
        String subject = String.format(appMailSubjectStatusOrder, consumerBankPayload.getOrderUUID());
        mimeMessageHelperBuilderWithoutAttachment(mail, consumerBankPayload.getEmail(), appMailAddress, subject, content);

        javaMailSender.send(mail);
    }

    public void sendEmail(ConsumerPdfPayload consumerPdfPayload) throws MessagingException, IOException {
        String pathToInvoice = String.format(outputPdfPath, consumerPdfPayload.getOrderUUID());
        createPdfInvoice(pathToInvoice, consumerPdfPayload.getFileData());

        MimeMessage mail = javaMailSender.createMimeMessage();

        String content = String.format(appMailContentInvoice, consumerPdfPayload.getOrderUUID());
        String subject = String.format(appMailSubjectInvoice, consumerPdfPayload.getOrderUUID());
        mimeMessageHelperBuilderWithAttachment(mail, consumerPdfPayload.getEmail(), appMailAddress, subject, content, consumerPdfPayload.getOrderUUID(), new File(pathToInvoice));

        javaMailSender.send(mail);
    }


    private void createPdfInvoice(String pathToInvoice, byte[] invoiceAsByte) throws IOException {
        FileUtils.writeByteArrayToFile(new File(pathToInvoice), invoiceAsByte);
    }

    private void mimeMessageHelperBuilderWithoutAttachment(MimeMessage mail, String to, String from, String subject, String content) throws MessagingException {
        new MimeMessageHelperBuilder()
                .prepareHelper(mail, true)
                .setTo(to)
                .setFrom(from)
                .setSubject(subject)
                .setContent(content, false);
    }

    private void mimeMessageHelperBuilderWithAttachment(MimeMessage mail, String to, String from, String subject, String content, String attachmentFilename, File file) throws MessagingException {
        new MimeMessageHelperBuilder()
                .prepareHelper(mail, true)
                .setTo(to)
                .setFrom(from)
                .setSubject(subject)
                .setContent(content, false)
                .addAttachment(attachmentFilename, file);
    }
}
