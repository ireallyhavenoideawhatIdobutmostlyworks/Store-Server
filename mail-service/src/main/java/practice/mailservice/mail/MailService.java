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

        MimeMessageHelper helper = new MimeMessageHelper(mail, true);
        helper.setTo(consumerStorePayload.getEmail());
        helper.setFrom(appMailAddress);
        helper.setSubject(String.format(appMailSubjectNewOrder, consumerStorePayload.getOrderUUID()));
        helper.setText(String.format(
                        appMailContentNewOrder,
                        consumerStorePayload.getOrderUUID(),
                        consumerStorePayload.getPaymentUUID(),
                        consumerStorePayload.getOrderPrice(),
                        consumerStorePayload.getAccountNumber()),
                false
        );

        javaMailSender.send(mail);
    }

    public void sendEmail(ConsumerBankPayload consumerBankPayload) throws MessagingException {
        MimeMessage mail = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mail, true);
        helper.setTo(consumerBankPayload.getEmail());
        helper.setFrom(appMailAddress);
        helper.setSubject(String.format(appMailSubjectStatusOrder, consumerBankPayload.getOrderUUID()));
        helper.setText(String.format(
                        appMailContentStatusOrder,
                        consumerBankPayload.getOrderUUID(),
                        consumerBankPayload.getPaymentType().toString(),
                        consumerBankPayload.getIsPaymentSuccess()),
                false
        );

        javaMailSender.send(mail);
    }

    public void sendEmail(ConsumerPdfPayload consumerPdfPayload) throws MessagingException, IOException {
        String pathToInvoice = String.format(outputPdfPath, consumerPdfPayload.getOrderUUID());
        createPdfInvoice(pathToInvoice, consumerPdfPayload.getFileData());

        MimeMessage mail = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mail, true);
        helper.setTo(consumerPdfPayload.getEmail());
        helper.setFrom(appMailAddress);
        helper.setSubject(String.format(appMailSubjectInvoice, consumerPdfPayload.getOrderUUID()));
        helper.addAttachment(consumerPdfPayload.getOrderUUID(), new File(pathToInvoice));
        helper.setText(String.format(
                        appMailContentInvoice,
                        consumerPdfPayload.getOrderUUID()),
                false
        );

        javaMailSender.send(mail);
    }


    private void createPdfInvoice(String pathToInvoice, byte[] invoiceAsByte) throws IOException {
        FileUtils.writeByteArrayToFile(new File(pathToInvoice), invoiceAsByte);
    }
}
