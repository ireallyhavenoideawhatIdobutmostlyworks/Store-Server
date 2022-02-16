package practice.mailservice.testdata;

import practice.mailservice.rabbit.payloads.bank.ConsumerBankPayload;
import practice.mailservice.rabbit.payloads.bank.PaymentType;
import practice.mailservice.rabbit.payloads.pdf.ConsumerPdfPayload;
import practice.mailservice.rabbit.payloads.store.ConsumerStorePayload;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class TestData {

    public static ConsumerStorePayload consumerStorePayload() {
        return ConsumerStorePayload.builder()
                .orderUUID("orderStoreUUID")
                .paymentUUID("paymentStoreUUID")
                .orderPrice(BigDecimal.valueOf(100).setScale(2, RoundingMode.CEILING))
                .accountNumber("storeAccountNumber")
                .email("store@test.email")
                .build();
    }

    public static ConsumerBankPayload consumerBankPayload(String orderUUID) {
        return ConsumerBankPayload.builder()
                .orderUUID(orderUUID)
                .paymentUUID("paymentBankUUID")
                .orderPrice(BigDecimal.valueOf(100).setScale(2, RoundingMode.CEILING))
                .accountNumber("bankAccountNumber")
                .email("bank@test.email")
                .paymentType(PaymentType.BLIK)
                .isPaymentSuccess(true)
                .build();
    }

    public static ConsumerPdfPayload consumerPdfPayload(String outputPdfPath, String testFileName) throws IOException {
        return ConsumerPdfPayload.builder()
                .orderUUID(testFileName)
                .email("pdf@test.email")
                .fileData(convertPdfToByte(new File("docs/testfile.pdf").getPath()))
                .build();
    }


    private static byte[] convertPdfToByte(String outputPdfPath) throws IOException {
        Path pdfPath = Paths.get(outputPdfPath);
        return Files.readAllBytes(pdfPath);
    }

    private static File createPdfFile(String outputPdfPath, String testFileName) throws IOException {
     //   File file = new File(String.format(outputPdfPath, testFileName));
   //     file.createNewFile();
        File file = new File("docs/testfile.pdf");
        return file;
    }
}
