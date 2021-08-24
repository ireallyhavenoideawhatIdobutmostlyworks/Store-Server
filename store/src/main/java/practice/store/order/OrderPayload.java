package practice.store.order;

import lombok.Builder;
import lombok.Data;
import practice.store.product.ProductPayload;

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
    private Set<ProductPayload> productsSet;
    private BigDecimal orderPrice;
}
