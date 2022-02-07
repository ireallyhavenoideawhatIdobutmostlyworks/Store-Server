package practice.store.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.store.customer.CustomerEntity;
import practice.store.customer.CustomerRepository;
import practice.store.exceptions.customer.CustomerIsNotActiveException;
import practice.store.order.details.OrderProductEntity;
import practice.store.order.details.OrderProductPayload;
import practice.store.order.details.OrderProductRepository;
import practice.store.product.ProductEntity;
import practice.store.product.ProductRepository;
import practice.store.product.ProductService;
import practice.store.rabbit.services.mail.SenderMailService;
import practice.store.rabbit.services.pdf.SenderPdfService;
import practice.store.utils.converter.PayloadsConverter;
import practice.store.utils.values.GenerateRandomString;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Transactional
@Service
@Log4j2
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;

    private final PayloadsConverter payloadsConverter;

    private final GenerateRandomString generateRandomString;

    private final ProductService productService;
    private final SenderMailService senderMailService;
    private final SenderPdfService senderPdfService;


    public boolean save(OrderPayload orderPayload) throws JsonProcessingException {
        CustomerEntity customerEntity = actualLoggedActiveCustomer();

        if (!isProductUuidExist(orderPayload)) {
            return false;
        }
        if (!checkIfOrderHasProduct(orderPayload.getOrderProductPayloads())) {
            return false;
        }
        if (!isAmountOfProductCorrect(orderPayload)) {
            return false;
        }

        OrderEntity orderEntity = prepareNewOrder(orderPayload, customerEntity);
        orderRepository.save(orderEntity);
        log.info("Saved new order. Entity details: {}", orderEntity);

        List<ProductEntity> productEntityList = new ArrayList<>();
        orderPayload
                .getOrderProductPayloads()
                .forEach(orderProductPayload -> {
                    ProductEntity productEntity = findByProductUUID(orderProductPayload.getProductUUID());

                    productService.changeAmountBoughtProduct(productEntity, orderProductPayload);
                    addOrderProductIntoDatabase(productEntity, orderProductPayload, orderEntity);

                    productEntityList.add(productEntity);
                    log.info("Edited product. Entity details: {}", productEntity);
                });

        senderMailService.send(orderEntity);
        senderPdfService.send(orderEntity, productEntityList);

        return true;
    }


    private OrderEntity prepareNewOrder(OrderPayload orderPayload, CustomerEntity customerEntity) {
        return payloadsConverter.convertOrder(orderPayload)
                .toBuilder()
                .id(null)
                .orderUUID(generateRandomString.generateRandomUuid())
                .paymentUUID(generateRandomString.generateRandomUuid())
                .customer(customerEntity)
                .shipmentStatus(ShipmentStatus.SHIPMENT_AWAITING_FOR_ACCEPT)
                .isPaid(false)
                .orderStatus(OrderStatus.ORDER_AWAITING)
                .creationDateTime(LocalDateTime.now())
                .build();
    }

    private void addOrderProductIntoDatabase(ProductEntity productEntity, OrderProductPayload orderProductPayload, OrderEntity orderEntity) {
        OrderProductEntity orderProductEntity = payloadsConverter.convertOrderProduct(orderProductPayload)
                .toBuilder()
                .id(null)
                .unitPrice(productEntity.getFinalPrice())
                .collectionPrice(productEntity.getFinalPrice().multiply(BigDecimal.valueOf(orderProductPayload.getAmount())))
                .order(orderEntity)
                .product(productEntity)
                .build();

        orderProductRepository.save(orderProductEntity);
        log.info("Saved orderProduct. Entity details: {}", orderProductEntity);
    }

    private CustomerEntity actualLoggedActiveCustomer() {
        CustomerEntity actualLoggedCustomer = customerRepository
                .findByEmail(
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getName()
                );

        checkIfCustomerIsActive(actualLoggedCustomer);

        log.info("Returned actual logged customer. Entity details: {}", actualLoggedCustomer);
        return actualLoggedCustomer;
    }

    private BigDecimal allProductsPrice(OrderPayload orderPayload) {
        return orderPayload
                .getOrderProductPayloads()
                .stream()
                .map(this::calculateFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.CEILING);
    }

    private BigDecimal calculateFinalPrice(OrderProductPayload orderProductPayload) {
        BigDecimal productFinalPrice = findByProductUUID(orderProductPayload.getProductUUID()).getFinalPrice();
        BigDecimal productAmount = BigDecimal.valueOf(orderProductPayload.getAmount());
        return productFinalPrice.multiply(productAmount);
    }

    private void checkIfCustomerIsActive(CustomerEntity actualLoggedCustomer) {
        if (!actualLoggedCustomer.isActive())
            throw new CustomerIsNotActiveException(actualLoggedCustomer.getEmail());
    }

    private boolean isAmountOfProductCorrect(OrderPayload orderPayload) {
        boolean result = orderPayload
                .getOrderProductPayloads()
                .stream()
                .allMatch(productDetails -> isProductAmountCorrect(productDetails.getAmount(), productDetails.getProductUUID()));

        return logIfFalse(result, "Amount of product is incorrect");
    }

    private boolean isProductUuidExist(OrderPayload orderPayload) {
        boolean result = orderPayload
                .getOrderProductPayloads()
                .stream()
                .allMatch(product -> productRepository.existsByProductUUID(product.getProductUUID()));

        return logIfFalse(result, "Some of product UUID is not exist");
    }

    private boolean checkIfOrderHasProduct(Set<OrderProductPayload> orderProductPayloads) {
        boolean result = !orderProductPayloads.isEmpty();
        return logIfFalse(result, "Order is without products");
    }

    private ProductEntity findByProductUUID(String uuid) {
        return productRepository
                .findByProductUUID(uuid)
                .orElseThrow((() -> new EntityNotFoundException(String.format("Entity with UUID: %s not found", uuid))));
    }

    private boolean isProductAmountCorrect(int amountPayload, String uuid) {
        ProductEntity productEntity = findByProductUUID(uuid);

        log.info(String.format("Amount details: \n-entityProductUUID: %s \n-entityProductAmount: %d \n-payloadProductAmount: %d",
                productEntity.getProductUUID(), productEntity.getAmount(), amountPayload));

        return (productEntity.getAmount() >= amountPayload) && (productEntity.getAmount() > 0) && (amountPayload > 0);
    }

    private boolean logIfFalse(boolean result, String desc) {
        if (!result) {
            log.error("Result condition is false because: {}.", desc);
            return false;
        }
        return true;
    }
}
