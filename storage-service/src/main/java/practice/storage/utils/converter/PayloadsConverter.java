package practice.storage.utils.converter;

import org.springframework.stereotype.Component;
import practice.storage.product.ProductEntity;
import practice.storage.product.ProductPayload;

@Component
public class PayloadsConverter {


    public ProductEntity convertProduct(ProductPayload productPayload) {
        return ProductEntity.builder()
                .name(productPayload.getName())
                .productUUID(productPayload.getProductUUID())
                .description(productPayload.getDescription())
                .basePrice(productPayload.getBasePrice())
                .finalPrice(productPayload.getFinalPrice())
                .amountPriceReduction(productPayload.getAmountPriceReduction())
                .discountPercentage(productPayload.getDiscountPercentage())
                .hasDiscount(productPayload.isHasDiscount())
                .amount(productPayload.getAmount())
                .categories(productPayload.getCategories())
                .availability(productPayload.getAvailability())
                .isActive(productPayload.isActive())
                .build();
    }
}
