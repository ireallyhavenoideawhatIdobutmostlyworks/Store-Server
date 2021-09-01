package practice.store.order;

import lombok.Builder;
import lombok.Data;
import practice.store.order.details.OrderProductPayload;

import java.math.BigDecimal;
import java.util.Set;

@Builder
@Data
public class OrderPayload {

    private Long id;
    private PaymentType paymentType;
    private String accountNumber;
    private Set<OrderProductPayload> orderProductPayloads;
    private BigDecimal orderPrice;
}
