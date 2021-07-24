package practice.store.utils.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import practice.store.customer.CustomerEntity;
import practice.store.customer.CustomerPayload;
import practice.store.product.ProductEntity;
import practice.store.product.ProductPayload;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class PayloadsConverter {

    private final PasswordEncoder passwordEncoder;


    public CustomerEntity convertCustomer(CustomerPayload customerPayload) {
        return CustomerEntity.builder()
                .id(customerPayload.getId())
                .username(customerPayload.getUsername())
                .password(passwordEncoder.encode(customerPayload.getPassword()))
                .email(customerPayload.getEmail())
                .isActive(customerPayload.isActive())
                .isCompany(customerPayload.isCompany())
                .build();
    }

    public ProductEntity convertProduct(ProductPayload productPayload) {
        return ProductEntity.builder()
                .id(productPayload.getId())
                .name(productPayload.getName())
                .productUUID(productPayload.getProductUUID())
                .description(productPayload.getDescription())
                .basePrice(productPayload.getBasePrice())
                .finalPrice(productPayload.getFinalPrice())
                .amountPriceReduction(productPayload.getAmountPriceReduction())
                .discountPercentage(productPayload.getDiscountPercentage())
                .hasDiscount(productPayload.isHasDiscount())
                .amountInStock(productPayload.getAmountInStock())
                .categories(productPayload.getCategories())
                .availability(productPayload.getAvailability())
                .isActive(productPayload.isActive())
                .build();
    }
}
