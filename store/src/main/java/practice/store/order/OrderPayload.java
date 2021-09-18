package practice.store.order;

import lombok.Builder;
import lombok.Data;
import practice.store.order.details.OrderProductPayload;

import java.math.BigDecimal;
import java.util.Set;

@Builder
@Data
public class OrderPayload {

    private String payloadUUID;
    private String accountNumber;
    private PaymentTypeEnum paymentTypeEnum;
    private BigDecimal orderBasePrice;
    private BigDecimal orderFinalPrice;
    private boolean hasDiscount;
    private int discountPercentage;
    private Set<OrderProductPayload> orderProductPayloads;
}
