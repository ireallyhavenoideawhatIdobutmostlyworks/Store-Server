package practice.mailservice.testdata;

import practice.mailservice.rabbit.bank.ConsumerBankPayload;
import practice.mailservice.rabbit.bank.PaymentType;
import practice.mailservice.rabbit.pdf.ConsumerPdfPayload;
import practice.mailservice.rabbit.store.ConsumerStorePayload;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class TestData {

    public static ConsumerStorePayload consumerStorePayload() {
        return ConsumerStorePayload.builder()
                .orderUUID("orderUUID")
                .paymentUUID("paymentUUID")
                .orderPrice(BigDecimal.valueOf(100).setScale(2, RoundingMode.CEILING))
                .accountNumber("accountNumber")
                .email("some@test.email")
                .build();
    }

    public static ConsumerBankPayload consumerBankPayload() {
        return ConsumerBankPayload.builder()
                .orderUUID("orderUUID")
                .paymentUUID("paymentUUID")
                .orderPrice(BigDecimal.valueOf(100).setScale(2, RoundingMode.CEILING))
                .accountNumber("accountNumber")
                .email("some@test.email")
                .paymentType(PaymentType.BLIK)
                .isPaymentSuccess(true)
                .build();
    }

    public static ConsumerPdfPayload consumerPdfPayload() {
        return ConsumerPdfPayload.builder()
                .orderUUID("orderUUID")
                .email("some@test.email")
                .build();
    }
}
