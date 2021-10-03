package testdata.payload;

import practice.store.order.details.OrderProductPayload;

import java.util.Arrays;
import java.util.HashSet;
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
