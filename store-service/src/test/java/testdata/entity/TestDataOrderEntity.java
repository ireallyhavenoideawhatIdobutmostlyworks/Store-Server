package testdata.entity;

import practice.store.customer.CustomerEntity;
import practice.store.order.OrderEntity;
import practice.store.order.OrderStatus;
import practice.store.order.PaymentType;
import practice.store.order.ShipmentStatus;
import practice.store.order.details.OrderProductEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public abstract class TestDataOrderEntity {

    public static OrderEntity Order() {
        return OrderEntity.builder()
                .id(1L)
                .orderUUID("UUID1")
                .accountNumber("1111")
                .isPaid(true)
                .paymentType(PaymentType.BANK_CARD)
                .orderStatus(OrderStatus.ORDER_SENT)
                .shipmentStatus(ShipmentStatus.SHIPMENT_IN_STORAGE)
                .creationDateTime(LocalDateTime.now())
                .build();
    }

    public static List<OrderEntity> OrdersList(CustomerEntity customerFirst, Set<OrderProductEntity> orderProduct) {
        OrderEntity orderEntityFirst = OrderEntity.builder()
                .id(1L)
                .orderUUID("first test uuid")
                .accountNumber("1111")
                .isPaid(true)
                .paymentType(PaymentType.BANK_CARD)
                .orderStatus(OrderStatus.ORDER_ACCEPTED)
                .shipmentStatus(ShipmentStatus.SHIPMENT_ACCEPTED)
                .creationDateTime(LocalDateTime.now())
                .customer(customerFirst)
                .orderProduct(orderProduct)
                .build();

        OrderEntity orderEntitySecond = OrderEntity.builder()
                .id(2L)
                .orderUUID("second test uuid")
                .accountNumber("2222")
                .isPaid(true)
                .paymentType(PaymentType.BANK_CARD)
                .orderStatus(OrderStatus.ORDER_ACCEPTED)
                .shipmentStatus(ShipmentStatus.SHIPMENT_ACCEPTED)
                .creationDateTime(LocalDateTime.now())
                .customer(customerFirst)
                .orderProduct(orderProduct)
                .build();

        OrderEntity orderEntityThird = OrderEntity.builder()
                .id(3L)
                .orderUUID("third test uuid")
                .accountNumber("3333")
                .isPaid(true)
                .paymentType(PaymentType.BANK_CARD)
                .orderStatus(OrderStatus.ORDER_ACCEPTED)
                .shipmentStatus(ShipmentStatus.SHIPMENT_ACCEPTED)
                .creationDateTime(LocalDateTime.now())
                .customer(customerFirst)
                .orderProduct(orderProduct)
                .build();

        return Arrays.asList(orderEntityFirst, orderEntitySecond, orderEntityThird);
    }
}
