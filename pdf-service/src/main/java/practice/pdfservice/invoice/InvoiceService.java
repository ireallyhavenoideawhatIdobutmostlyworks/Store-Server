package practice.pdfservice.invoice;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import practice.pdfservice.pdf.ExcelService;
import practice.pdfservice.pdf.PdfService;
import practice.pdfservice.rabbit.mail.SenderMailService;
import practice.pdfservice.rabbit.store.ConsumerStorePayload;

import java.io.IOException;

@PropertySource("classpath:excel.properties")
@RequiredArgsConstructor
@Service
public class InvoiceService {

    private final ExcelService excelService;
    private final PdfService pdfService;
    private final SenderMailService senderMailService;

    @Value("${output.excel.path}")
    private String outputExcelPath;
    @Value("${output.pdf.path}")
    private String outputPdfPath;


    public void create(ConsumerStorePayload consumerStorePayload) throws IOException {
        String orderUUID = consumerStorePayload.getOrderPdfDetails().getOrderUUID();
        String pdfPath = String.format(outputPdfPath, orderUUID);

        excelService.createExcelFile(consumerStorePayload);
        pdfService.convertExcelToPdf(consumerStorePayload);

        senderMailService.send(pdfPath, orderUUID);
    }
}
