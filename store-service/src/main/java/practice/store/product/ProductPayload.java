package practice.store.product;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public final class ProductPayload {

    private final String name;
    private final String productUUID;
    private final String description;
    private final BigDecimal basePrice;
    private final BigDecimal finalPrice;
    private final int discountPercentage;
    private final boolean hasDiscount;
    private final int amount;
    private final Categories categories;
    private final Availability availability;
    private final boolean isActive;
}
