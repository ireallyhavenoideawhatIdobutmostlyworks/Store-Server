package practice.storage.utils.converter;

import org.springframework.stereotype.Component;
import practice.storage.product.ProductEntity;
import practice.storage.product.ProductPayload;

@Component
public class EntitiesConverter {


    public ProductPayload convertProduct(ProductEntity productEntity) {
        return ProductPayload.builder()
                .id(productEntity.getId())
                .name(productEntity.getName())
                .productUUID(productEntity.getProductUUID())
                .description(productEntity.getDescription())
                .basePrice(productEntity.getBasePrice())
                .finalPrice(productEntity.getFinalPrice())
                .amountPriceReduction(productEntity.getAmountPriceReduction())
                .discountPercentage(productEntity.getDiscountPercentage())
                .hasDiscount(productEntity.isHasDiscount())
                .amount(productEntity.getAmount())
                .categories(productEntity.getCategories())
                .availability(productEntity.getAvailability())
                .isActive(productEntity.isActive())
                .build();
    }
}
