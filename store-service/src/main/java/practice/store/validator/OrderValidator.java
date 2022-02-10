package practice.store.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import practice.store.customer.CustomerEntity;
import practice.store.exceptions.customer.CustomerIsNotActiveException;
import practice.store.order.OrderPayload;
import practice.store.product.ProductEntity;
import practice.store.product.ProductRepository;

import javax.persistence.EntityNotFoundException;

@RequiredArgsConstructor
@Transactional
@Component
@Log4j2
public class OrderValidator extends BaseValidator {

    private final ProductRepository productRepository;


    public void checkIfCustomerIsActive(CustomerEntity actualLoggedCustomer) {
        if (!actualLoggedCustomer.isActive())
            throw new CustomerIsNotActiveException(actualLoggedCustomer.getEmail());
    }

    public boolean isAmountOfProductCorrect(OrderPayload orderPayload) {
        boolean result = orderPayload
                .getOrderProductPayloads()
                .stream()
                .allMatch(productDetails -> isProductAmountCorrect(productDetails.getAmount(), productDetails.getProductUUID()));

        return logIfFalse(result, "Amount of product is incorrect");
    }

    public boolean isProductUuidExist(OrderPayload orderPayload) {
        boolean result = orderPayload
                .getOrderProductPayloads()
                .stream()
                .allMatch(product -> productRepository.existsByProductUUID(product.getProductUUID()));

        return logIfFalse(result, "Some of product UUID is not exist");
    }

    public boolean hasOrderProducts(OrderPayload orderPayload) {
        boolean result = !orderPayload.getOrderProductPayloads().isEmpty();
        return logIfFalse(result, "Order is without products");
    }

    public boolean isProductAmountCorrect(int amountPayload, String uuid) {
        ProductEntity productEntity = productRepository
                .findByProductUUID(uuid)
                .orElseThrow((() -> new EntityNotFoundException(String.format("Entity with UUID: %s not found", uuid))));

        log.info(String.format("Amount details: \n-entityProductUUID: %s \n-entityProductAmount: %d \n-payloadProductAmount: %d",
                productEntity.getProductUUID(), productEntity.getAmount(), amountPayload));

        return (productEntity.getAmount() >= amountPayload) && (productEntity.getAmount() > 0) && (amountPayload > 0);
    }


    @Override
    boolean logIfFalse(boolean result, String desc) {
        if (!result) {
            log.error("Result condition is false because: {}.", desc);
            return false;
        }
        return true;
    }
}
