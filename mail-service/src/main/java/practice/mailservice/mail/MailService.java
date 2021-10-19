package practice.mailservice.mail;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

    @Value("${mail.address}")
    private String appMailAddress;

    @Value("${mail.subject.new.order}")
    private String appMailSubjectNewOrder;
    @Value("${mail.content.new.order}")
    private String appMailContentNewOrder;

    @Value("${mail.subject.status.order}")
    private String appMailSubjectStatusOrder;
    @Value("${mail.content.status.order}")
    private String appMailContentStatusOrder;

    @Value("${mail.subject.invoice}")
    private String appMailSubjectInvoice;
    @Value("${mail.content.invoice}")
    private String appMailContentInvoice;

    @Value("${output.pdf.path}")
    private String outputPdfPath;


    public void sendEmail(ConsumerStorePayload consumerStorePayload) throws MessagingException {
        MimeMessage mail = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mail, true);
        String subject = String.format(appMailSubjectNewOrder, consumerStorePayload.getOrderUUID());
        String content = String.format(appMailContentNewOrder, consumerStorePayload.getOrderUUID(), consumerStorePayload.getPaymentUUID(), consumerStorePayload.getOrderPrice(), consumerStorePayload.getAccountNumber());
        setEmailDetails(helper, consumerStorePayload.getEmail(), subject, content);

        javaMailSender.send(mail);
    }

    public void sendEmail(ConsumerBankPayload consumerBankPayload) throws MessagingException {
        MimeMessage mail = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mail, true);
        String subject = String.format(appMailSubjectStatusOrder, consumerBankPayload.getOrderUUID());
        String content = String.format(appMailContentStatusOrder, consumerBankPayload.getOrderUUID(), consumerBankPayload.getPaymentType().toString(), consumerBankPayload.getIsPaymentSuccess());
        setEmailDetails(helper, consumerBankPayload.getEmail(), subject, content);

        javaMailSender.send(mail);
    }

    public void sendEmail(ConsumerPdfPayload consumerPdfPayload) throws MessagingException, IOException {
        String pathToInvoice = String.format(outputPdfPath, consumerPdfPayload.getOrderUUID());
        createPdfInvoice(pathToInvoice, consumerPdfPayload.getFileData());

        MimeMessage mail = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mail, true);
        helper.addAttachment(consumerPdfPayload.getOrderUUID(), new File(pathToInvoice));
        String subject = String.format(appMailSubjectInvoice, consumerPdfPayload.getOrderUUID());
        String content = String.format(appMailContentInvoice, consumerPdfPayload.getOrderUUID());
        setEmailDetails(helper, consumerPdfPayload.getEmail(), subject, content);

        javaMailSender.send(mail);
    }


    private void createPdfInvoice(String pathToInvoice, byte[] invoiceAsByte) throws IOException {
        FileUtils.writeByteArrayToFile(new File(pathToInvoice), invoiceAsByte);
    }

    private void setEmailDetails(MimeMessageHelper helper, String email, String subject, String content) throws MessagingException {
        helper.setTo(email);
        helper.setFrom(appMailAddress);
        helper.setSubject(subject);
        helper.setText(content, false);
    }
}