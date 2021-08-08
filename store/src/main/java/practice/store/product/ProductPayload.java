package practice.store.product;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class ProductPayload {

    private Long id;
    private String name;
    private String productUUID;
    private String description;
    private BigDecimal basePrice;
    private BigDecimal amountPriceReduction;
    private BigDecimal finalPrice;
    private int discountPercentage;
    private boolean hasDiscount;
    private int amountInStock;
    private Categories categories;
    private Availability availability;
    private boolean isActive;
}
