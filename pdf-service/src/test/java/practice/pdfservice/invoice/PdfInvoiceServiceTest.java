package practice.pdfservice.invoice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import practice.pdfservice.files.ExcelService;
import practice.pdfservice.files.PdfService;
import practice.pdfservice.rabbit.mail.SenderMailService;
import practice.pdfservice.rabbit.store.ConsumerStorePayload;
import practice.pdfservice.testData.TestData;

import java.io.IOException;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Tests for pdf invoice service")
@ExtendWith(MockitoExtension.class)
@Tag("Unit_Test")
class PdfInvoiceServiceTest {

    @Mock
    private ExcelService excelService;
    @Mock
    private PdfService pdfService;
    @Mock
    private SenderMailService senderMailService;
    private InvoiceService invoiceService;

    private final String outputPdfPath = "src/test/java/practice/pdfservice/testfiles/%s.pdf";


    @BeforeEach
    void setUp() {
        invoiceService = new InvoiceService(excelService, pdfService, senderMailService);
        setValueForFields();
    }


    @DisplayName("Create invoice with data from Store-Service")
    @Test
    void create_basedOnDataFromStore_succeed() throws IOException {
        // given
        ConsumerStorePayload consumerStorePayload = TestData.consumerStorePayload("Order uuid");
        String orderUUID = consumerStorePayload.getOrderPdfDetails().getOrderUUID();
        String customerMailAddress = consumerStorePayload.getCustomerPdfDetails().getEmail();
        String pdfPath = String.format(outputPdfPath, orderUUID);


        // when
        invoiceService.create(consumerStorePayload);


        // then
        verify(excelService, times(1)).createExcelFile(consumerStorePayload);
        verify(pdfService, times(1)).convertExcelToPdf(consumerStorePayload);
        verify(senderMailService, times(1)).send(pdfPath, orderUUID, customerMailAddress);
    }


    private void setValueForFields() {
        ReflectionTestUtils.setField(invoiceService, "outputPdfPath", outputPdfPath);
    }
}
