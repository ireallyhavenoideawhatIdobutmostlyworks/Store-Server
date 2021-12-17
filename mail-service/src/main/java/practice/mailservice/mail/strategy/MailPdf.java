package practice.mailservice.mail.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import practice.mailservice.rabbit.payloads.pdf.ConsumerPdfPayload;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;

@PropertySource({
        "classpath:mail.properties",
        "classpath:file.properties"
})
@RequiredArgsConstructor
@Service
@Log4j2
public class MailPdf implements MailStrategy<ConsumerPdfPayload> {

    private final JavaMailSender javaMailSender;

    @Value("${output.pdf.path}")
    private String outputPdfPath;

    @Value("${mail.subject.invoice}")
    private String mailSubjectInvoice;
    @Value("${mail.content.invoice}")
    private String mailContentInvoice;
    @Value("${mail.address}")
    private String mailAddress;


    @Override
    public void sendEmail(ConsumerPdfPayload consumerPdfPayload) throws IOException, MessagingException {
        log.info("Prepare mail content, subject, invoice and MimeMessage");

        String pathToInvoice = String.format(outputPdfPath, consumerPdfPayload.getOrderUUID());
        createPdfInvoice(pathToInvoice, consumerPdfPayload.getFileData());
        log.info("Created invoice as pdf document. Invoice path: {}", pathToInvoice);

        MimeMessage mail = new MailBuilder(javaMailSender)
                .withSender(mailAddress)
                .withRecipient(consumerPdfPayload.getEmail())
                .withContent(makeContent(consumerPdfPayload))
                .withSubject(makeSubject(consumerPdfPayload))
                .withAttachmentIfExist(consumerPdfPayload.getOrderUUID(), pathToInvoice)
                .build();

        javaMailSender.send(mail);
        log.info("Sent email to {} with data and invoice based on {}-service", consumerPdfPayload.getEmail(), MailType.PDF);
    }


    private void createPdfInvoice(String pathToInvoice, byte[] invoiceAsByte) throws IOException {
        FileUtils.writeByteArrayToFile(new File(pathToInvoice), invoiceAsByte);
    }

    private String makeContent(ConsumerPdfPayload consumerPdfPayload) {
        return String.format(mailContentInvoice, consumerPdfPayload.getOrderUUID());
    }

    private String makeSubject(ConsumerPdfPayload consumerPdfPayload) {
        return String.format(mailSubjectInvoice, consumerPdfPayload.getOrderUUID());
    }
}
