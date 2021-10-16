package testdata.entity;

import practice.store.order.OrderEntity;
import practice.store.order.details.OrderProductEntity;
import practice.store.product.ProductEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class TestDataOrderProductEntity {

    public static OrderProductEntity orderProductEntity(ProductEntity product, OrderEntity order) {
        return OrderProductEntity.builder()
                .id(1L)
                .amount(100)
                .unitPrice(BigDecimal.valueOf(100))
                .collectionPrice(BigDecimal.valueOf(100))
                .product(product)
                .order(order)
                .build();
    }

    public static Set<OrderProductEntity> orderProductEntityList(ProductEntity product) {
        OrderProductEntity orderProductFirst = OrderProductEntity.builder()
                .id(1L)
                .amount(100)
                .unitPrice(BigDecimal.valueOf(100))
                .collectionPrice(BigDecimal.valueOf(100))
                .product(product)
           //     .order(order)
                .build();

        OrderProductEntity orderProductSecond = OrderProductEntity.builder()
                .id(2L)
                .amount(200)
                .unitPrice(BigDecimal.valueOf(1200))
                .collectionPrice(BigDecimal.valueOf(200))
                .product(product)
            //    .order(order)
                .build();

        OrderProductEntity orderProductThird = OrderProductEntity.builder()
                .id(3L)
                .amount(300)
                .unitPrice(BigDecimal.valueOf(300))
                .collectionPrice(BigDecimal.valueOf(300))
                .product(product)
             //   .order(order)
                .build();

        return new HashSet<>(Arrays.asList(orderProductFirst, orderProductSecond, orderProductThird));
    }
}
