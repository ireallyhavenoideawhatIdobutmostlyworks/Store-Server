package practice.pdfservice.testData;

import practice.pdfservice.rabbit.mail.SenderMailPayload;
import practice.pdfservice.rabbit.store.ConsumerStorePayload;
import practice.pdfservice.rabbit.store.detaills.CustomerPdfDetails;
import practice.pdfservice.rabbit.store.detaills.OrderPdfDetails;
import practice.pdfservice.rabbit.store.detaills.ProductPdfDetails;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class TestData {

    public static CustomerPdfDetails customerPdfDetails() {
        return CustomerPdfDetails
                .builder()
                .username("username")
                .email("test@email.email")
                .postalCode("12345")
                .street("street")
                .city("city")
                .build();
    }

    public static OrderPdfDetails orderPdfDetails(String orderUUID) {
        return OrderPdfDetails
                .builder()
                .orderUUID(orderUUID)
                .paymentUUID("paymentUuid")
                .orderPrice(BigDecimal.valueOf(100).setScale(2, RoundingMode.CEILING))
                .accountNumber("account number")
                .build();
    }

    public static ProductPdfDetails productPdfDetails() {
        return ProductPdfDetails
                .builder()
                .name("name")
                .productUUID("uuid")
                .description("description")
                .finalPrice(BigDecimal.valueOf(100).setScale(2, RoundingMode.CEILING))
                .build();
    }

    public static ConsumerStorePayload consumerStorePayload(String orderUUID) {
        return ConsumerStorePayload
                .builder()
                .customerPdfDetails(customerPdfDetails())
                .orderPdfDetails(orderPdfDetails(orderUUID))
                .productPdfDetailsList(new ArrayList<>(Arrays.asList(productPdfDetails(), productPdfDetails(), productPdfDetails())))
                .build();
    }

    public static SenderMailPayload senderMailPayload(String outputPdfPath, String testFileName) throws IOException {
        return SenderMailPayload
                .builder()
                .orderUUID("orderPdfUUID")
                .email("pdf@test.email")
                .fileData(convertPdfToByte(createPdfFile(outputPdfPath, testFileName).getPath()))
                .build();
    }


    private static byte[] convertPdfToByte(String outputPdfPath) throws IOException {
        Path pdfPath = Paths.get(outputPdfPath);
        return Files.readAllBytes(pdfPath);
    }

    private static File createPdfFile(String outputPdfPath, String testFileName) throws IOException {
        File file = new File(String.format(outputPdfPath, testFileName));
        file.createNewFile();
        return file;
    }
}
