package practice.store.utils.converter;

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

@Component
public class EntitiesConverter {

    public CustomerPayload convertCustomer(CustomerEntity customerEntity) {
        return CustomerPayload.builder()
                .id(customerEntity.getId())
                .username(customerEntity.getUsername())
                .email(customerEntity.getEmail())
                .isActive(customerEntity.isActive())
                .isCompany(customerEntity.isCompany())
                .postalCode(customerEntity.getPostalCode())
                .street(customerEntity.getStreet())
                .city(customerEntity.getCity())
                .build();
    }

    public ProductPayload convertProduct(ProductEntity productEntity) {
        return ProductPayload.builder()
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

    public OrderPayload convertOrder(OrderEntity orderEntity) {
        return OrderPayload.builder()
                .payloadUUID(orderEntity.getPayloadUUID())
                .accountNumber(orderEntity.getAccountNumber())
                .paymentType(orderEntity.getPaymentType())
                .orderBasePrice(orderEntity.getOrderBasePrice())
                .orderFinalPrice(orderEntity.getOrderFinalPrice())
                .hasDiscount(orderEntity.isHasDiscount())
                .discountPercentage(orderEntity.getDiscountPercentage())
                .orderProductPayloads(convertProductsList(orderEntity.getOrderProduct()))
                .build();
    }

    public Set<OrderProductPayload> convertProductsList(Set<OrderProductEntity> products) {
        return products
                .stream()
                .map(this::convertOrderProduct)
                .collect(Collectors.toSet());
    }


    private OrderProductPayload convertOrderProduct(OrderProductEntity orderProduct) {
        return OrderProductPayload.builder()
                .amount(orderProduct.getAmount())
                .build();
    }
}
