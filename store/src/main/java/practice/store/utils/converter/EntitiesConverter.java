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
                .amount(productEntity.getAmount())
                .categories(productEntity.getCategories())
                .availability(productEntity.getAvailability())
                .isActive(productEntity.isActive())
                .build();
    }

    public OrderProductPayload convertOrderProduct(OrderProductEntity orderProduct) {
        return OrderProductPayload.builder()
                .id(orderProduct.getId())
                .amount(orderProduct.getAmount())
                .unitPrice(orderProduct.getUnitPrice())
                .collectionPrice(orderProduct.getCollectionPrice())
                .product(orderProduct.getProduct())
                .build();
    }

    public OrderPayload convertOrder(OrderEntity orderEntity) {
        return OrderPayload.builder()
                .id(orderEntity.getId())
                .orderUUID(orderEntity.getOrderUUID())
                .accountNumber(orderEntity.getAccountNumber())
                .isPaid(orderEntity.getIsPaid())
                .paymentType(orderEntity.getPaymentType())
                .shipmentStatus(orderEntity.getShipmentStatus())
                .orderStatus(orderEntity.getOrderStatus())
                .productDetails(convertProductsList(orderEntity.getOrderProduct()))
                .orderPrice(orderEntity.getOrderPrice())
                .build();
    }


    private Set<OrderProductPayload> convertProductsList(Set<OrderProductEntity> products) {
        return products.stream()
                .map(this::convertOrderProduct)
                .collect(Collectors.toSet());
    }
}
