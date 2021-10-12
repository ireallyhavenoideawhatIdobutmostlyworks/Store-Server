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


    public void send(String outputPdfPath, String orderUUID) throws IOException {
        rabbitTemplate.convertAndSend(queueFromPdfToEmail, prepareSenderMailPayload(convertPdfToByte(outputPdfPath), orderUUID));
    }


    private SenderMailPayload prepareSenderMailPayload(byte[] bytes, String orderUUID) {
        return SenderMailPayload.builder()
                .orderUUID(orderUUID)
                .fileData(bytes)
                .build();
    }

    private byte[] convertPdfToByte(String outputPdfPath) throws IOException {
        Path pdfPath = Paths.get(outputPdfPath);
        return Files.readAllBytes(pdfPath);
    }
}
