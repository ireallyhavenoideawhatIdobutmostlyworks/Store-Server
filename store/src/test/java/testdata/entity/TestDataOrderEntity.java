package testdata.entity;

import org.junit.jupiter.api.Order;
import practice.store.customer.CustomerEntity;
import practice.store.order.OrderEntity;
import practice.store.order.OrderStatusEnum;
import practice.store.order.PaymentTypeEnum;
import practice.store.order.ShipmentStatusEnum;
import practice.store.order.details.OrderProductEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

public abstract class TestDataOrderEntity {

    public static OrderEntity Order() {
        return OrderEntity.builder()
                .id(1L)
                .orderUUID("UUID1")
                .accountNumber("1111")
                .isPaid(true)
                .paymentTypeEnum(PaymentTypeEnum.BANK_CARD)
                .orderStatusEnum(OrderStatusEnum.ORDER_SENT)
                .shipmentStatusEnum(ShipmentStatusEnum.SHIPMENT_IN_STORAGE)
                .orderBasePrice(BigDecimal.valueOf(200D))
                .orderFinalPrice(BigDecimal.valueOf(200D))
                .hasDiscount(false)
                .discountPercentage(0)
                .creationDateTime(new Date())
                .build();
    }

    public static List<OrderEntity> OrdersList(CustomerEntity customerFirst, Set<OrderProductEntity> orderProduct) {
        OrderEntity orderEntityFirst = OrderEntity.builder()
                .id(1L)
                .orderUUID("first test uuid")
                .accountNumber("1111")
                .isPaid(true)
                .paymentTypeEnum(PaymentTypeEnum.BANK_CARD)
                .orderStatusEnum(OrderStatusEnum.ORDER_ACCEPTED)
                .shipmentStatusEnum(ShipmentStatusEnum.SHIPMENT_ACCEPTED)
                .orderBasePrice(BigDecimal.valueOf(1000))
                .orderFinalPrice(BigDecimal.valueOf(1000))
                .hasDiscount(false)
                .discountPercentage(0)
                .creationDateTime(new Date())
                .customer(customerFirst)
                .orderProduct(orderProduct)
                .build();

        OrderEntity orderEntitySecond = OrderEntity.builder()
                .id(2L)
                .orderUUID("second test uuid")
                .accountNumber("2222")
                .isPaid(true)
                .paymentTypeEnum(PaymentTypeEnum.BANK_CARD)
                .orderStatusEnum(OrderStatusEnum.ORDER_ACCEPTED)
                .shipmentStatusEnum(ShipmentStatusEnum.SHIPMENT_ACCEPTED)
                .orderBasePrice(BigDecimal.valueOf(2000))
                .orderFinalPrice(BigDecimal.valueOf(2000))
                .hasDiscount(false)
                .discountPercentage(0)
                .creationDateTime(new Date())
                .customer(customerFirst)
                .orderProduct(orderProduct)
                .build();

        OrderEntity orderEntityThird = OrderEntity.builder()
                .id(3L)
                .orderUUID("third test uuid")
                .accountNumber("3333")
                .isPaid(true)
                .paymentTypeEnum(PaymentTypeEnum.BANK_CARD)
                .orderStatusEnum(OrderStatusEnum.ORDER_ACCEPTED)
                .shipmentStatusEnum(ShipmentStatusEnum.SHIPMENT_ACCEPTED)
                .orderBasePrice(BigDecimal.valueOf(3000))
                .orderFinalPrice(BigDecimal.valueOf(3000))
                .hasDiscount(false)
                .discountPercentage(0)
                .creationDateTime(new Date())
                .customer(customerFirst)
                .orderProduct(orderProduct)
                .build();

        return Arrays.asList(orderEntityFirst, orderEntitySecond, orderEntityThird);
    }
}
