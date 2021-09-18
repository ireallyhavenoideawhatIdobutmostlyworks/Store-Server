package practice.store.utils.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import practice.store.customer.CustomerEntity;
import practice.store.customer.CustomerPayload;
import practice.store.order.OrderEntity;
import practice.store.order.OrderPayload;
import practice.store.order.details.OrderProductEntity;
import practice.store.order.details.OrderProductPayload;
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
                .amount(productPayload.getAmount())
                .categoriesEnum(productPayload.getCategoriesEnum())
                .availability(productPayload.getAvailability())
                .isActive(productPayload.isActive())
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
                .paymentTypeEnum(orderPayload.getPaymentTypeEnum())
                .orderProduct(convertProductsList(orderPayload.getOrderProductPayloads()))
                .orderBasePrice(orderPayload.getOrderBasePrice())
                .orderFinalPrice(orderPayload.getOrderFinalPrice())
                .hasDiscount(orderPayload.isHasDiscount())
                .discountPercentage(orderPayload.getDiscountPercentage())
                .build();
    }


    private Set<OrderProductEntity> convertProductsList(Set<OrderProductPayload> products) {
        return products.stream()
                .map(this::convertOrderProduct)
                .collect(Collectors.toSet());
    }
}
