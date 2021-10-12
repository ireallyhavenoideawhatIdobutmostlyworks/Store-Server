package practice.pdfservice.pdf;

import com.spire.xls.Workbook;
import lombok.Getter;
import org.springframework.stereotype.Service;
import practice.pdfservice.rabbit.store.ConsumerStorePayload;

@Service
@Getter
public class PdfService {

    private String outputPdfPath;


    public void convertExcelToPdf(ConsumerStorePayload consumerStorePayload, String outputExcelPath) {
        outputPdfPath = String.format("pdf-service/src/main/resources/docs/%s.pdf", consumerStorePayload.getOrderPdfDetails().getOrderUUID());
        Workbook workbook = new Workbook();
        workbook.loadFromFile(outputExcelPath);
        workbook.saveToFile(outputPdfPath, com.spire.xls.FileFormat.PDF);
    }
}
