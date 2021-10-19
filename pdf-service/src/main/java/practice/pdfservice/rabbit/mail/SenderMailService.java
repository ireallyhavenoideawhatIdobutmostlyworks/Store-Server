package practice.pdfservice.rabbit.mail;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@PropertySource("classpath:rabbitPdf.properties")
@Service
public class SenderMailService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${queue.from.pdf.to.email}")
    private String queueFromPdfToEmail;


    public void send(String outputPdfPath, String orderUUID, String customerMailAddress) throws IOException {
        byte[] pdfAsBytes = convertPdfToByte(outputPdfPath);
        SenderMailPayload senderMailPayload = prepareSenderMailPayload(pdfAsBytes, orderUUID, customerMailAddress);

        rabbitTemplate.convertAndSend(queueFromPdfToEmail, senderMailPayload);
    }


    private SenderMailPayload prepareSenderMailPayload(byte[] bytes, String orderUUID, String customerMailAddress) {
        return SenderMailPayload.builder()
                .orderUUID(orderUUID)
                .fileData(bytes)
                .email(customerMailAddress)
                .build();
    }

    private byte[] convertPdfToByte(String outputPdfPath) throws IOException {
        Path pdfPath = Paths.get(outputPdfPath);
        return Files.readAllBytes(pdfPath);
    }
}
