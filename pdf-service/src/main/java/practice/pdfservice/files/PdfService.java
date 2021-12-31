package practice.pdfservice.files;

import com.spire.xls.FileFormat;
import com.spire.xls.Workbook;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import practice.pdfservice.rabbit.store.ConsumerStorePayload;

@PropertySource("classpath:file.properties")
@Service
@Log4j2
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
        log.info("Convert excel file to pdf file for Order UUID: {}", orderUUID);
    }
}
