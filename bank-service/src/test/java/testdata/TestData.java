package testdata;

import practice.bank.payment.PaymentEntity;
import practice.bank.payment.PaymentResultPayload;
import practice.bank.payment.PaymentType;
import practice.bank.rabbit.mail.SenderMailPayload;
import practice.bank.rabbit.store.SenderStorePayload;
import practice.bank.utils.GenerateRandomString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class TestData {

    public static PaymentResultPayload paymentResultPayload() {
        return PaymentResultPayload.builder()
                .orderUUID(new GenerateRandomString().generateRandomUuid())
                .orderPrice(BigDecimal.valueOf(999))
                .accountNumber("accountNumber")
                .paymentType(PaymentType.BLIK)
                .email("some@testpayload.email")
                .isPaymentSuccess(true)
                .build();
    }

    public static PaymentEntity preparePaymentEntity(PaymentResultPayload paymentResultPayload, String paymentUuid, boolean isPaymentSuccess) {
        return PaymentEntity.builder()
                .orderUUID(paymentResultPayload.getOrderUUID())
                .accountNumber(paymentResultPayload.getAccountNumber())
                .paymentUUID(paymentUuid)
                .email(paymentResultPayload.getEmail())
                .orderPrice(paymentResultPayload.getOrderPrice())
                .isPaymentSuccess(isPaymentSuccess)
                .processingDate(LocalDateTime.now())
                .paymentType(paymentResultPayload.getPaymentType())
                .build();
    }

    public static SenderMailPayload prepareSenderMailPayload(PaymentEntity paymentEntity) {
        return SenderMailPayload.builder()
                .orderUUID(paymentEntity.getOrderUUID())
                .accountNumber(paymentEntity.getAccountNumber())
                .paymentUUID(paymentEntity.getPaymentUUID())
                .email(paymentEntity.getEmail())
                .orderPrice(paymentEntity.getOrderPrice())
                .paymentType(paymentEntity.getPaymentType())
                .isPaymentSuccess(paymentEntity.getIsPaymentSuccess())
                .build();
    }

    public static SenderStorePayload prepareSenderStorePayload(PaymentEntity paymentEntity) {
        return SenderStorePayload.builder()
                .orderUUID(paymentEntity.getOrderUUID())
                .accountNumber(paymentEntity.getAccountNumber())
                .paymentUUID(paymentEntity.getPaymentUUID())
                .orderPrice(paymentEntity.getOrderPrice())
                .isPaymentSuccess(paymentEntity.getIsPaymentSuccess())
                .paymentType(paymentEntity.getPaymentType())
                .build();
    }

}
