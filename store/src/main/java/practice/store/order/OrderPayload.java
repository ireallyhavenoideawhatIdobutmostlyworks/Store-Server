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
    private Boolean isPaid;
    private PaymentType paymentType;
    private String orderUUID;
    private String accountNumber;
    private ShipmentStatus shipmentStatus;
    private OrderStatus orderStatus;
    private Set<OrderProductPayload> productDetails;
    private BigDecimal orderPrice;
}
