package practice.store.order.details;

import lombok.Builder;
import lombok.Data;
import practice.store.product.ProductEntity;

import java.math.BigDecimal;

@Builder
@Data
public class OrderProductPayload {

    private Long id;
    private int amount;
    private BigDecimal unitPrice;
    private BigDecimal collectionPrice;
    private ProductEntity product;
}
