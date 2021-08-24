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

                    checkIfAmountIsNotEqualZero(product.getAmountInStock(), product.getProductUUID());
                    checkIfAmountIsAvailable(product.getAmountInStock(), productEntity.getAmountInStock());

                    productEntity.setAmountInStock(productEntity.getAmountInStock() - product.getAmountInStock());

                    calculateAvailabilityDependsOnProductAmounts(productEntity);

                    productRepository.save(productEntity);
                });

        BigDecimal finalOrderPrice = productPayloads.stream().map(ProductPayload::getFinalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity newOrderEntity = prepareNewOrderContent(orderPayload, finalOrderPrice, actualLoggedCustomer);
        orderRepository.save(newOrderEntity);
    }


    private CustomerEntity actualLoggedCustomer() {
        return customerRepository.findByEmail(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName());
    }

    private OrderEntity prepareNewOrderContent(OrderPayload orderPayload, BigDecimal finalOrderPrice, CustomerEntity actualLoggedCustomer) {
        OrderEntity order = payloadsConverter.convertOrder(orderPayload);

        order.setOrderUUID(generateRandomString.generateRandomUuid());
        order.setCustomer(actualLoggedCustomer);
        order.setShipmentStatus(ShipmentStatus.SHIPMENT_AWAITING_FOR_ACCEPT);
        order.setOrderPrice(finalOrderPrice);
        order.setIsPaid(false);
        order.setOrderStatus(OrderStatus.ORDER_AWAITING);

        return order;
    }

    private void calculateAvailabilityDependsOnProductAmounts(ProductEntity productEntity) {
        if (productEntity.getAmountInStock() == 0)
            productEntity.setAvailability(Availability.NOT_AVAILABLE);
        else if (productEntity.getAmountInStock() < 5)
            productEntity.setAvailability(Availability.AWAITING_FROM_MANUFACTURE);
    }

    private void checkIfOrderHasProduct(Set<ProductPayload> productPayloads) {
        if (productPayloads.isEmpty()) {
            throw new OrderMissingProductException();
        }
    }

    private void checkIfAmountIsAvailable(int amountPayload, int amountInStock) {
        if ((amountInStock < amountPayload) || (amountInStock == 0))
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
}
