package testdata.payload;

import practice.store.order.details.OrderProductEntity;
import practice.store.order.details.OrderProductPayload;
import practice.store.product.ProductEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class TestDataOrderProductPayload {

    public static Set<OrderProductPayload> OrderProductSet(String productUuidFirst, String productUuidSecond, int amount) {
        OrderProductPayload orderProductFirst = OrderProductPayload.builder()
                .amount(amount)
                .productUUID(productUuidFirst)
                .build();

        OrderProductPayload orderProductSecond = OrderProductPayload.builder()
                .amount(amount)
                .productUUID(productUuidSecond)
                .build();

        return new HashSet<>(Arrays.asList(orderProductFirst, orderProductSecond));
    }
}
