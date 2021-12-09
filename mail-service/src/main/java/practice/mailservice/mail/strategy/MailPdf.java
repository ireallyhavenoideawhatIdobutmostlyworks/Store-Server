package practice.mailservice.mail.strategy;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import practice.mailservice.rabbit.payloads.ConsumerPayload;
import practice.mailservice.rabbit.payloads.pdf.ConsumerPdfPayload;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;

@PropertySource({
        "classpath:mail.properties",
        "classpath:file.properties"
})
@Service
@Log4j2
public class MailPdf extends MailHelper implements MailStrategy {

    @Value("${output.pdf.path}")
    private String outputPdfPath;

    @Value("${mail.subject.invoice}")
    private String mailSubjectInvoice;
    @Value("${mail.content.invoice}")
    private String mailContentInvoice;


    @Override
    public void sendEmail(ConsumerPayload consumerPayload) throws MessagingException, IOException {
        ConsumerPdfPayload consumerPdfPayload = (ConsumerPdfPayload) consumerPayload;
        log.info(CASTED_MESSAGE, "consumerPdfPayload");

        String content = String.format(
                mailContentInvoice,
                consumerPdfPayload.getOrderUUID()
        );
        log.info(PREPARED_MESSAGE);

        String pathToInvoice = String.format(outputPdfPath, consumerPdfPayload.getOrderUUID());
        createPdfInvoice(pathToInvoice, consumerPdfPayload.getFileData());
        log.info("Created invoice as pdf document. Invoice path: {}", pathToInvoice);

        setMimeMessage()
                .setMimeMessageHelper()
                .setRecipient(consumerPdfPayload.getEmail())
                .setFrom()
                .setSubject(mailSubjectInvoice, consumerPdfPayload.getOrderUUID())
                .setContent(content, false)
                .setAttachmentIfExist(true, consumerPdfPayload.getOrderUUID(), pathToInvoice)
                .sendEmail();

        log.info(SENT_MESSAGE, consumerPdfPayload.getEmail(), "pdf");
    }


    private void createPdfInvoice(String pathToInvoice, byte[] invoiceAsByte) throws IOException {
        FileUtils.writeByteArrayToFile(new File(pathToInvoice), invoiceAsByte);
    }
}
