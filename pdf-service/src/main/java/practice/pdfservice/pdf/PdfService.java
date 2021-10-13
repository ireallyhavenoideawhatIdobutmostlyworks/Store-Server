package practice.pdfservice.pdf;

import com.spire.xls.FileFormat;
import com.spire.xls.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import practice.pdfservice.rabbit.store.ConsumerStorePayload;

@PropertySource("classpath:excel.properties")
@Service
public class PdfService {

    @Value("${output.excel.path}")
    private String outputExcelPath;
    @Value("${output.pdf.path}")
    private String outputPdfPath;


    public void convertExcelToPdf(ConsumerStorePayload consumerStorePayload) {
        String orderUUID = consumerStorePayload.getOrderPdfDetails().getOrderUUID();
        String excelPath = String.format(outputExcelPath, orderUUID);
        String pdfPath = String.format(outputPdfPath, orderUUID);

        Workbook workbook = new Workbook();
        workbook.loadFromFile(excelPath);
        workbook.saveToFile(pdfPath, FileFormat.PDF);
    }
}
