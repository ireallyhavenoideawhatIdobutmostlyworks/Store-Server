package practice.store.order;

import lombok.Builder;
import lombok.Data;
import practice.store.customer.CustomerEntity;
import practice.store.order.details.OrderProductPayload;

import java.math.BigDecimal;
import java.util.Set;

@Builder
@Data
public class OrderPayload {

    private Long id;
    private PaymentType paymentType;
    private String accountNumber;
    private BigDecimal orderBasePrice;
    private BigDecimal orderFinalPrice;
    private boolean hasDiscount;
    private int discountPercentage;
    private CustomerEntity customer;
    private Set<OrderProductPayload> orderProductPayloads;
}
