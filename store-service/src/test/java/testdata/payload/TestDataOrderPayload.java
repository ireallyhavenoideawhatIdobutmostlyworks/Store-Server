package testdata.payload;

import practice.store.order.OrderPayload;
import practice.store.order.PaymentType;

import java.math.BigDecimal;

public abstract class TestDataOrderPayload {

    public static OrderPayload Order(String productUuidFirst, String productUuidSecond, int amount) {
        return OrderPayload.builder()
                .payloadUUID("1111")
                .paymentType(PaymentType.BANK_CARD)
                .accountNumber("1111")
                .orderBasePrice(BigDecimal.valueOf(100))
                .orderFinalPrice(BigDecimal.valueOf(100))
                .hasDiscount(false)
                .discountPercentage(0)
                .orderProductPayloads(TestDataOrderProductPayload.OrderProductSet(productUuidFirst, productUuidSecond, amount))
                .build();
    }

    public static OrderPayload OrderWithDiscount(String productUuidFirst, String productUuidSecond) {
        return OrderPayload.builder()
                .payloadUUID("1111")
                .paymentType(PaymentType.BANK_CARD)
                .accountNumber("1111")
                .orderBasePrice(BigDecimal.valueOf(100))
                .orderFinalPrice(BigDecimal.valueOf(90))
                .hasDiscount(true)
                .discountPercentage(10)
                .orderProductPayloads(TestDataOrderProductPayload.OrderProductSet(productUuidFirst, productUuidSecond, 10))
                .build();
    }

    public static OrderPayload OrderWithoutDiscount(String productUuidFirst, String productUuidSecond) {
        return OrderPayload.builder()
                .payloadUUID("2222")
                .paymentType(PaymentType.BANK_TRANSFER)
                .accountNumber("2222")
                .orderBasePrice(BigDecimal.valueOf(5000))
                .orderFinalPrice(BigDecimal.valueOf(5000))
                .hasDiscount(false)
                .discountPercentage(0)
                .orderProductPayloads(TestDataOrderProductPayload.OrderProductSet(productUuidFirst, productUuidSecond, 5))
                .build();
    }

    public static OrderPayload Order() {
        return OrderPayload.builder()
                .payloadUUID("1111")
                .paymentType(PaymentType.BANK_CARD)
                .accountNumber("1111")
                .orderBasePrice(BigDecimal.valueOf(100))
                .orderFinalPrice(BigDecimal.valueOf(100))
                .hasDiscount(false)
                .discountPercentage(0)
                .orderProductPayloads(null)
                .build();
    }
}