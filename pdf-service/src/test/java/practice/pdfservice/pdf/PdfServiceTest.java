package practice.pdfservice.pdf;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import practice.pdfservice.files.PdfService;
import practice.pdfservice.rabbit.store.ConsumerStorePayload;
import practice.pdfservice.testData.TestData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Tests for pdf service")
@ExtendWith(MockitoExtension.class)
class PdfServiceTest {

    private PdfService pdfService;

    private final String pathToDirectory = "src/test/java/practice/pdfservice/testfiles";
    private final String outputExcelPath = pathToDirectory + "/%s.xls";

    @BeforeEach
    void setUp() throws IOException {
        pdfService = new PdfService();
        initializeServiceProperties();
        FileUtils.cleanDirectory(new File(pathToDirectory));
    }


    @DisplayName("Convert excel file to pdf file")
    @Test
    void convertExcelToPdf_basedOnDataFromStore_succeed() throws IOException {
        // given
        String fileName = UUID.randomUUID().toString();
        createEmptyExcel(outputExcelPath, fileName);
        ConsumerStorePayload consumerStorePayload = TestData.consumerStorePayload(fileName);


        // when
        String outputPath = pdfService.convertExcelToPdf(consumerStorePayload);


        // then
        String expectedName = String.format("%s.pdf", fileName);
        assertEquals(expectedName, new File(outputPath).getName());
    }


    private void createEmptyExcel(String path, String excelName) throws IOException {
        Workbook invoiceAsExcel = new HSSFWorkbook();
        OutputStream os = new FileOutputStream(String.format(path, excelName));
        invoiceAsExcel.write(os);
    }

    private void initializeServiceProperties() {
        ReflectionTestUtils.setField(pdfService, "outputExcelPath", outputExcelPath);
        String outputPdfPath = pathToDirectory + "/%s.pdf";
        ReflectionTestUtils.setField(pdfService, "outputPdfPath", outputPdfPath);
    }
}
