package practice.store.utils.converter;

import org.springframework.stereotype.Component;
import practice.store.customer.CustomerEntity;
import practice.store.customer.CustomerPayload;
import practice.store.product.ProductEntity;
import practice.store.product.ProductPayload;

@Component
public class EntitiesConverter {

    public CustomerPayload convertCustomer(CustomerEntity customerEntity) {
        return CustomerPayload.builder()
                .id(customerEntity.getId())
                .username(customerEntity.getUsername())
                .email(customerEntity.getEmail())
                .isActive(customerEntity.isActive())
                .isCompany(customerEntity.isCompany())
                .build();
    }

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
                .amountInStock(productEntity.getAmountInStock())
                .categories(productEntity.getCategories())
                .availability(productEntity.getAvailability())
                .isActive(productEntity.isActive())
                .build();
    }
}
