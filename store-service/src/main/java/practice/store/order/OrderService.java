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
import practice.store.exceptions.order.OrderDiscountException;
import practice.store.exceptions.order.OrderDiscountPercentageException;
import practice.store.exceptions.order.OrderFinalPriceException;
import practice.store.exceptions.order.OrderMissingProductException;
import practice.store.exceptions.product.ProductAmountInvalidParameterException;
import practice.store.exceptions.product.ProductAmountNotEnoughException;
import practice.store.exceptions.product.ProductUuidNotExistException;
import practice.store.order.details.OrderProductEntity;
import practice.store.order.details.OrderProductPayload;
import practice.store.order.details.OrderProductRepository;
import practice.store.rabbit.services.mail.SenderMailService;
import practice.store.rabbit.services.pdf.SenderPdfService;
import practice.store.product.ProductEntity;
import practice.store.product.ProductRepository;
import practice.store.product.ProductService;
import practice.store.utils.converter.EntitiesConverter;
import practice.store.utils.converter.PayloadsConverter;
import practice.store.utils.numbers.CalculatePrice;
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
    private final EntitiesConverter entitiesConverter;

    private final GenerateRandomString generateRandomString;
    private final CalculatePrice calculateFinalPrice;

    private final ProductService productService;

    private final SenderMailService senderMailService;
    private final SenderPdfService senderPdfService;

    // ToDo refactor 'flow control by exceptions'
    // ToDo refactor 'flow control by exceptions'
    // ToDo refactor 'flow control by exceptions'
    // ToDo refactor 'flow control by exceptions'
    // ToDo refactor 'flow control by exceptions'

    public void save(OrderPayload orderPayload) throws JsonProcessingException {
        checkIfOrderHasProduct(orderPayload.getOrderProductPayloads());
        checkProductExceptions(orderPayload);
        checkIfPriceDiscountCase(orderPayload);
        checkDiscountPercentage(orderPayload);
        checkFinalPriceIfOrderHasDiscount(orderPayload);

        OrderEntity orderEntity = prepareNewOrder(orderPayload);
        orderRepository.save(orderEntity);
        log.info("Saved new order. Entity details: {}", orderEntity);

        List<ProductEntity> productEntityList = new ArrayList<>();

        orderPayload
                .getOrderProductPayloads()
                .forEach(orderProductPayload -> {
                    ProductEntity productEntity = productRepository.findByProductUUID(orderProductPayload.getProductUUID()).get();

                    productService.changeAmountBoughtProduct(productEntity, orderProductPayload);
                    addOrderProductIntoDatabase(productEntity, orderProductPayload, orderEntity);

                    productEntityList.add(productEntity);
                    log.info("Edited product. Entity details: {}", productEntity);
                });

        senderMailService.send(orderEntity);
        senderPdfService.send(orderEntity, productEntityList);
    }


    private OrderEntity prepareNewOrder(OrderPayload orderPayload) {
        return payloadsConverter.convertOrder(orderPayload)
                .toBuilder()
                .id(null)
                .orderUUID(generateRandomString.generateRandomUuid())
                .paymentUUID(generateRandomString.generateRandomUuid())
                .customer(actualLoggedActiveCustomer())
                .shipmentStatus(ShipmentStatus.SHIPMENT_AWAITING_FOR_ACCEPT)
                .isPaid(false)
                .orderStatus(OrderStatus.ORDER_AWAITING)
                .creationDateTime(LocalDateTime.now())
                .isCancelled(false)
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
        CustomerEntity actualLoggedCustomer = actualLoggedCustomer();
        checkIfCustomerIsActive(actualLoggedCustomer);
        log.info("Returned actual logged customer. Entity details: {}", actualLoggedCustomer);
        return actualLoggedCustomer;
    }

    private CustomerEntity actualLoggedCustomer() {
        return customerRepository.findByEmail(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName());
    }

    private BigDecimal allProductsPrice(OrderPayload orderPayload) {
        return orderPayload
                .getOrderProductPayloads()
                .stream()
                .map(this::multiplyAmountFromOrderProductWithProductFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.CEILING);
    }

    private void checkProductExceptions(OrderPayload orderPayload) {
        orderPayload
                .getOrderProductPayloads()
                .forEach(
                        productDetails -> {
                            checkIfProductUuidExist(productDetails.getProductUUID());
                            checkIfAmountIsNotEqualZero(productDetails.getAmount(), productDetails.getProductUUID());
                            checkIfAmountIsAvailable(productDetails.getAmount(), productDetails.getProductUUID());
                        }
                );
    }

    private BigDecimal multiplyAmountFromOrderProductWithProductFinalPrice(OrderProductPayload orderProductPayload) {
        BigDecimal productFinalPrice = productRepository.findByProductUUID(orderProductPayload.getProductUUID()).get().getFinalPrice();
        BigDecimal productAmount = BigDecimal.valueOf(orderProductPayload.getAmount());
        return productFinalPrice.multiply(productAmount);
    }


    private void checkDiscountPercentage(OrderPayload orderPayload) {
        if (orderPayload.isHasDiscount() && orderPayload.getDiscountPercentage() == 0)
            throw new OrderDiscountPercentageException();
        else if (!orderPayload.isHasDiscount() && orderPayload.getDiscountPercentage() > 0)
            throw new OrderDiscountPercentageException(orderPayload.getDiscountPercentage());
    }

    private void checkFinalPriceIfOrderHasDiscount(OrderPayload orderPayload) {
        BigDecimal orderFinalPrice = orderPayload.getOrderFinalPrice().setScale(2, RoundingMode.CEILING);
        BigDecimal finalPriceCalculate = calculateFinalPrice.calculateFinalPrice(orderPayload.getOrderBasePrice(), orderPayload.getDiscountPercentage());

        if (orderPayload.isHasDiscount() && (!finalPriceCalculate.equals(orderFinalPrice)))
            throw new OrderFinalPriceException(orderFinalPrice, orderPayload.getOrderBasePrice(), orderPayload.getDiscountPercentage(), finalPriceCalculate);
    }

    private void checkIfPriceDiscountCase(OrderPayload orderPayload) {
        BigDecimal orderFinalPrice = orderPayload.getOrderFinalPrice().setScale(2, RoundingMode.CEILING);
        BigDecimal orderBasePrice = orderPayload.getOrderBasePrice().setScale(2, RoundingMode.CEILING);
        BigDecimal allProductPriceMultiplyByAmount = allProductsPrice(orderPayload).setScale(2, RoundingMode.CEILING);

        if (!orderPayload.isHasDiscount() && (!orderFinalPrice.equals(allProductPriceMultiplyByAmount))) {
            throw new OrderFinalPriceException(orderFinalPrice, allProductPriceMultiplyByAmount);
        } else if ((orderPayload.isHasDiscount()) && (orderBasePrice.equals(orderFinalPrice))) {
            throw new OrderDiscountException(orderBasePrice, orderFinalPrice);
        }
    }

    private void checkIfOrderHasProduct(Set<OrderProductPayload> orderProductPayloads) {
        if (orderProductPayloads.isEmpty()) {
            throw new OrderMissingProductException();
        }
    }

    private void checkIfAmountIsAvailable(int amountPayload, String uuid) {
        ProductEntity productEntity = productRepository
                .findByProductUUID(uuid)
                .orElseThrow((() -> new EntityNotFoundException(String.format("Entity with UUID: %s not found", uuid))));

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
            throw new ProductUuidNotExistException(uuid);
    }
}
