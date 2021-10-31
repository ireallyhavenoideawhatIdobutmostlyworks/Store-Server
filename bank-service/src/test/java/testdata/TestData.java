package testdata;

import practice.bank.payment.PaymentEntity;
import practice.bank.payment.PaymentResultPayload;
import practice.bank.payment.PaymentType;
import practice.bank.utils.GenerateRandomString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class TestData {

    public static PaymentResultPayload paymentResultPayload(String accountNumber, boolean isPaymentSuccess) {
        return PaymentResultPayload.builder()
                .orderUUID(new GenerateRandomString().generateRandomUuid())
                .orderPrice(BigDecimal.valueOf(999))
                .accountNumber(accountNumber)
                .paymentType(PaymentType.BLIK)
                .email("some@testpayload.email")
                .isPaymentSuccess(isPaymentSuccess)
                .build();
    }

    public static PaymentEntity paymentEntity(PaymentResultPayload paymentResultPayload) {
        return PaymentEntity.builder()
                .orderUUID(paymentResultPayload.getOrderUUID())
                .paymentUUID(new GenerateRandomString().generateRandomUuid())
                .accountNumber(paymentResultPayload.getAccountNumber())
                .email(paymentResultPayload.getEmail())
                .orderPrice(paymentResultPayload.getOrderPrice())
                .isPaymentSuccess(paymentResultPayload.getIsPaymentSuccess())
                .processingDate(LocalDateTime.now())
                .paymentType(paymentResultPayload.getPaymentType())
                .build();
    }
}
