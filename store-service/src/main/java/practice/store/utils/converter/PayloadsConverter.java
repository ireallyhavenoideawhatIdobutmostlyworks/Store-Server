package practice.store.utils.converter;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import practice.store.customer.CustomerEntity;
import practice.store.customer.CustomerPayload;
import practice.store.order.OrderEntity;
import practice.store.order.OrderPayload;
import practice.store.order.details.OrderProductEntity;
import practice.store.order.details.OrderProductPayload;
import practice.store.product.Availability;
import practice.store.product.ProductEntity;
import practice.store.product.ProductPayload;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Log4j2
public class PayloadsConverter {

    private final PasswordEncoder passwordEncoder;


    public CustomerEntity convertCustomer(CustomerPayload customerPayload, Long id) {
        return CustomerEntity.builder()
                .id(id)
                .username(customerPayload.getUsername())
                .password(passwordEncoder.encode(customerPayload.getPassword()))
                .email(customerPayload.getEmail())
                .isActive(customerPayload.getIsActive())
                .isCompany(customerPayload.getIsCompany())
                .postalCode(customerPayload.getPostalCode())
                .street(customerPayload.getStreet())
                .city(customerPayload.getCity())
                .build();
    }

    public ProductEntity convertProduct(ProductPayload productPayload, Availability calculatedAvailability) {
        return ProductEntity.builder()
                .name(productPayload.getName())
                .productUUID(productPayload.getProductUUID())
                .description(productPayload.getDescription())
                .basePrice(productPayload.getBasePrice())
                .finalPrice(productPayload.getFinalPrice())
                .discountPercentage(productPayload.getDiscountPercentage())
                .hasDiscount(productPayload.getHasDiscount())
                .amount(productPayload.getAmount())
                .categories(productPayload.getCategories())
                .availability(calculatedAvailability)
                .isActive(productPayload.getIsActive())
                .build();
    }

    public ProductEntity convertProduct(ProductPayload productPayload, Availability calculatedAvailability, long id) {
        return ProductEntity.builder()
                .id(id)
                .name(productPayload.getName())
                .productUUID(productPayload.getProductUUID())
                .description(productPayload.getDescription())
                .basePrice(productPayload.getBasePrice())
                .finalPrice(productPayload.getFinalPrice())
                .discountPercentage(productPayload.getDiscountPercentage())
                .hasDiscount(productPayload.getHasDiscount())
                .amount(productPayload.getAmount())
                .categories(productPayload.getCategories())
                .availability(calculatedAvailability)
                .isActive(productPayload.getIsActive())
                .build();
    }

    public OrderProductEntity convertOrderProduct(OrderProductPayload orderProduct) {
        return OrderProductEntity.builder()
                .amount(orderProduct.getAmount())
                .build();
    }

    public OrderEntity convertOrder(OrderPayload orderPayload) {
        return OrderEntity.builder()
                .payloadUUID(orderPayload.getPayloadUUID())
                .accountNumber(orderPayload.getAccountNumber())
                .paymentType(orderPayload.getPaymentType())
                .orderProduct(convertProductsList(orderPayload.getOrderProductPayloads()))
                .build();
    }


    private Set<OrderProductEntity> convertProductsList(Set<OrderProductPayload> products) {
        return products.stream()
                .map(this::convertOrderProduct)
                .collect(Collectors.toSet());
    }
}
