package practice.pdfservice.invoice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practice.pdfservice.pdf.ExcelService;
import practice.pdfservice.pdf.PdfService;
import practice.pdfservice.rabbit.mail.SenderMailService;
import practice.pdfservice.rabbit.store.ConsumerStorePayload;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class InvoiceService {

    private final ExcelService excelService;
    private final PdfService pdfService;
    private final SenderMailService senderMailService;


    public void create(ConsumerStorePayload consumerStorePayload) throws IOException {
        excelService.createExcelFile(consumerStorePayload);
        pdfService.convertExcelToPdf(consumerStorePayload, excelService.getOutputExcelPath());
        senderMailService.send(pdfService.getOutputPdfPath(), consumerStorePayload.getOrderPdfDetails().getOrderUUID());
    }
}
