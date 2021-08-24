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
import practice.store.product.Availability;
import practice.store.product.ProductEntity;
import practice.store.product.ProductPayload;
import practice.store.product.ProductRepository;
import practice.store.utils.converter.PayloadsConverter;
import practice.store.utils.values.GenerateRandomString;

import java.math.BigDecimal;
import java.util.Set;

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
        checkIfOrderHasProduct(orderPayload.getProductsSet());

        CustomerEntity actualLoggedCustomer = actualLoggedCustomer();
        checkIfCustomerIsActive(actualLoggedCustomer);

        Set<ProductPayload> productPayloads = orderPayload.getProductsSet();
        productPayloads.forEach(
                product -> {
                    ProductEntity productEntity = productRepository.getById(product.getId());
                    saveEditedProduct(productEntity, product);
                });

        BigDecimal finalOrderPrice = addProductsPrice(productPayloads);

        OrderEntity newOrderEntity = prepareNewOrderContent(orderPayload, finalOrderPrice, actualLoggedCustomer);
        orderRepository.save(newOrderEntity);
    }


    private void saveEditedProduct(ProductEntity productEntity, ProductPayload productPayload) {
        checkIfAmountIsNotEqualZero(productPayload.getAmountInStock(), productPayload.getProductUUID())
                .checkIfAmountIsAvailable(productPayload.getAmountInStock(), productEntity.getAmountInStock())
                .setNewAmountInStock(productEntity, productEntity.getAmountInStock(), productPayload.getAmountInStock())
                .calculateAvailabilityDependsOnProductAmounts(productEntity)
                .saveProduct(productEntity);
    }

    private BigDecimal addProductsPrice(Set<ProductPayload> productPayloads) {
        return productPayloads
                .stream()
                .map(ProductPayload::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private CustomerEntity actualLoggedCustomer() {
        return customerRepository.findByEmail(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName());
    }

    private OrderEntity prepareNewOrderContent(OrderPayload orderPayload, BigDecimal finalOrderPrice, CustomerEntity actualLoggedCustomer) {
        return payloadsConverter.convertOrder(orderPayload)
                .toBuilder()
                .orderUUID(generateRandomString.generateRandomUuid())
                .customer(actualLoggedCustomer)
                .shipmentStatus(ShipmentStatus.SHIPMENT_AWAITING_FOR_ACCEPT)
                .orderPrice(finalOrderPrice)
                .isPaid(false)
                .orderStatus(OrderStatus.ORDER_AWAITING)
                .build();
    }

    private OrderService calculateAvailabilityDependsOnProductAmounts(ProductEntity productEntity) {
        if (productEntity.getAmountInStock() == 0)
            productEntity.setAvailability(Availability.NOT_AVAILABLE);
        else if (productEntity.getAmountInStock() < 5)
            productEntity.setAvailability(Availability.AWAITING_FROM_MANUFACTURE);
        return this;
    }

    private void saveProduct(ProductEntity productEntity) {
        productRepository.save(productEntity);
    }

    private void checkIfOrderHasProduct(Set<ProductPayload> productPayloads) {
        if (productPayloads.isEmpty()) {
            throw new OrderMissingProductException();
        }
    }

    private OrderService checkIfAmountIsAvailable(int amountPayload, int amountInStock) {
        if ((amountInStock < amountPayload) || (amountInStock == 0))
            throw new ProductAmountNotEnoughException();
        return this;
    }

    private OrderService checkIfAmountIsNotEqualZero(int amount, String uuid) {
        if (amount == 0)
            throw new ProductAmountInvalidParameterException(amount, uuid);
        return this;
    }

    private OrderService setNewAmountInStock(ProductEntity productEntity, int amountFromEntity, int amountFromPayload) {
        productEntity.setAmountInStock(amountFromEntity - amountFromPayload);
        return this;
    }

    private void checkIfCustomerIsActive(CustomerEntity actualLoggedCustomer) {
        if (!actualLoggedCustomer.isActive())
            throw new CustomerIsNotActiveException(actualLoggedCustomer.getEmail());
    }
}
