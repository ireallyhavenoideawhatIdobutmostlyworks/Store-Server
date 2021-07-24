package practice.store.product;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProductPayload {

    private Long id;
    private String name;
    private String productUUID;
    private String description;
    private double basePrice;
    private double amountPriceReduction;
    private double finalPrice;
    private int discountPercentage;
    private boolean hasDiscount;
    private int amountInStock;
    private Categories categories;
    private Availability availability;
    private boolean isActive;
}
