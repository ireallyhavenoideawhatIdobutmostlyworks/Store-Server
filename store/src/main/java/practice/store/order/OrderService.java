package practice.store.order;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.store.customer.CustomerEntity;
import practice.store.customer.CustomerRepository;
import practice.store.exceptions.customer.CustomerIsNotActiveException;
import practice.store.exceptions.order.OrderMissingProductException;
import practice.store.exceptions.product.ProductAmountInvalidParameterException;
import practice.store.exceptions.product.ProductAmountNotEnoughException;
import practice.store.exceptions.product.ProductUuidExistException;
import practice.store.order.details.OrderProductPayload;
import practice.store.product.Availability;
import practice.store.product.ProductEntity;
import practice.store.product.ProductRepository;
import practice.store.utils.converter.PayloadsConverter;
import practice.store.utils.values.GenerateRandomString;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    private final PayloadsConverter payloadsConverter;

    private final GenerateRandomString generateRandomString;


    public void save(OrderPayload orderPayload) {
        checkIfOrderHasProduct(orderPayload.getProductDetails());

        orderPayload
                .getProductDetails()
                .forEach(
                        productDetails -> {
                            checkIfProductUuidExist(productDetails.getProduct().getProductUUID());
                            checkIfAmountIsNotEqualZero(productDetails.getAmount(), productDetails.getProduct().getProductUUID());
                            checkIfAmountIsAvailable(productDetails.getAmount(), productDetails.getProduct().getProductUUID());
                        }
                );

        Set<ProductEntity> productEntitiesSet = orderPayload
                .getProductDetails()
                .stream()
                .map(productDetails -> {

                    ProductEntity productEntity = productRepository.findByProductUUID(productDetails.getProduct().getProductUUID());
                    productEntity.setAmount(productEntity.getAmount() - productDetails.getAmount());

                    calculateAvailabilityDependsOnProductAmounts(productEntity);
                    saveProduct(productEntity);


                    return productEntity;
                })
                .collect(Collectors.toSet());

        BigDecimal finalOrderPrice = addProductsPrice(productEntitiesSet);
        OrderEntity newOrderEntity = prepareNewOrderContent(orderPayload, finalOrderPrice);
        orderRepository.save(newOrderEntity);
    }


    private BigDecimal addProductsPrice(Set<ProductEntity> productEntities) {
        return productEntities
                .stream()
                .map(ProductEntity::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderEntity prepareNewOrderContent(OrderPayload orderPayload, BigDecimal finalOrderPrice) {
        return payloadsConverter.convertOrder(orderPayload)
                .toBuilder()
                .id(null)
                .orderUUID(generateRandomString.generateRandomUuid())
                .customer(actualLoggedActiveCustomer())
                .shipmentStatus(ShipmentStatus.SHIPMENT_AWAITING_FOR_ACCEPT)
                .orderPrice(finalOrderPrice)
                .isPaid(false)
                .orderStatus(OrderStatus.ORDER_AWAITING)
                .build();
    }

    private CustomerEntity actualLoggedActiveCustomer() {
        CustomerEntity actualLoggedCustomer = actualLoggedCustomer();
        checkIfCustomerIsActive(actualLoggedCustomer);
        return actualLoggedCustomer;
    }

    private CustomerEntity actualLoggedCustomer() {
        return customerRepository.findByEmail(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName());
    }

    private void calculateAvailabilityDependsOnProductAmounts(ProductEntity productEntity) {
        if (productEntity.getAmount() == 0)
            productEntity.setAvailability(Availability.NOT_AVAILABLE);
        else if (productEntity.getAmount() < 5)
            productEntity.setAvailability(Availability.AWAITING_FROM_MANUFACTURE);
    }

    private void saveProduct(ProductEntity productEntity) {
        productRepository.save(productEntity);
    }

    private void checkIfOrderHasProduct(Set<OrderProductPayload> orderProductPayloads) {
        if (orderProductPayloads.isEmpty()) {
            throw new OrderMissingProductException();
        }
    }

    private void checkIfAmountIsAvailable(int amountPayload, String uuid) {
        ProductEntity productEntity = productRepository.findByProductUUID(uuid);
        if ((productEntity.getAmount() < amountPayload) || (productEntity.getAmount() == 0))
            throw new ProductAmountNotEnoughException();
    }

    private void checkIfAmountIsNotEqualZero(int amount, String uuid) {
        if (amount == 0)
            throw new ProductAmountInvalidParameterException(amount, uuid);
    }

    private void checkIfCustomerIsActive(CustomerEntity actualLoggedCustomer) {
        if (!actualLoggedCustomer.isActive())
            throw new CustomerIsNotActiveException(actualLoggedCustomer.getEmail());
    }

    private void checkIfProductUuidExist(String uuid) {
        if (!productRepository.existsByProductUUID(uuid))
            throw new ProductUuidExistException(uuid);
    }
}
