package practice.store.utils.initdata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import practice.store.customer.CustomerEntity;
import practice.store.order.OrderEntity;
import practice.store.order.OrderStatus;
import practice.store.order.PaymentType;
import practice.store.order.ShipmentStatus;
import practice.store.order.details.OrderProductEntity;
import practice.store.product.Availability;
import practice.store.product.Categories;
import practice.store.product.ProductEntity;
import practice.store.utils.numbers.CalculatePrice;

import java.math.BigDecimal;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Component
@Getter
public class CreateDataStartApp {

    private final PasswordEncoder passwordEncoder;
    private final CalculatePrice calculateFinalPrice;

    private CustomerEntity customerFirst, customerSecond, customerThird;
    private Set<CustomerEntity> customers;

    private ProductEntity productFirst, productSecond, productThird, productFourth, productFifth, productSixth;
    private Set<ProductEntity> products;

    private OrderEntity orderFirst, orderSecond;
    private Set<OrderEntity> orders;

    private OrderProductEntity orderProductFirst, orderProductSecond, orderProductThird;
    private Set<OrderProductEntity> orderProductEntities;

    @Value("${email.second.customer}")
    private String emailSecondCustomer;
    @Value("${password.second.customer}")
    private String passwordSecondCustomer;


    public void createCustomers() {
        customerFirst = createCustomerFirst();
        customerSecond = createCustomerSecond();
        customerThird = createCustomerThirdInactive();
        customers = new HashSet<>(Arrays.asList(customerFirst, customerSecond, customerThird));
    }

    public void createProducts() {
        productFirst = createProductFirst();
        productSecond = createProductSecond();
        productThird = createProductThird();
        productFourth = createProductFourth();
        productFifth = createProductFifth();
        productSixth = createProductSixth();
        products = new HashSet<>(Arrays.asList(productFirst, productSecond, productThird));
    }

    public void createOrders() throws ParseException {
        orderFirst = createOrderFirst();
        orderSecond = createOrderSecond();
        orders = new HashSet<>(Arrays.asList(orderFirst, orderSecond));
    }

    public void createOrderProductDetails() {
        orderProductFirst = createOrderProductFirst();
        orderProductSecond = createOrderProductSecond();
        orderProductThird = createOrderProductThird();
        orderProductEntities = new HashSet<>(Arrays.asList(orderProductFirst, orderProductSecond, orderProductThird));
    }


    private CustomerEntity createCustomerFirst() {
        customerFirst = CustomerEntity.builder()
                .id(1L)
                .username("Stanisław Nowak")
                .password(passwordEncoder.encode("first"))
                .email("first@first.first")
                .isActive(true)
                .isCompany(false)
                .postalCode("11-111")
                .street("Półwiejska 11/1a")
                .city("Poznań")
                .build();
        return customerFirst;
    }

    private CustomerEntity createCustomerSecond() {
        customerSecond = CustomerEntity.builder()
                .id(2L)
                .username("Stanisław Nowak")
                .password(passwordEncoder.encode(passwordSecondCustomer))
                .email(emailSecondCustomer)
                .isActive(true)
                .isCompany(true)
                .postalCode("11-112")
                .street("Półwiejska 11/1b")
                .city("Poznań")
                .build();
        return customerSecond;
    }

    private CustomerEntity createCustomerThirdInactive() {
        customerThird = CustomerEntity.builder()
                .id(3L)
                .username("Stanisław Nowak")
                .password(passwordEncoder.encode("third"))
                .email("third@third.third")
                .isActive(false)
                .isCompany(true)
                .postalCode("11-113")
                .street("Półwiejska 11/1c")
                .city("Poznań")
                .build();
        return customerThird;
    }

    private ProductEntity createProductFirst() {
        BigDecimal basePrice = BigDecimal.valueOf(500);
        int discount = 15;
        BigDecimal finalPrice = calculateFinalPrice.calculateFinalPrice(basePrice, discount);

        productFirst = ProductEntity.builder()
                .id(1L)
                .name("Nexus 6")
                .productUUID("UUID1")
                .description("Description product 1")
                .categories(Categories.PHONES)
                .basePrice(basePrice)
                .finalPrice(finalPrice)
                .discountPercentage(discount)
                .amount(100)
                .hasDiscount(true)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();
        return productFirst;
    }

    private ProductEntity createProductSecond() {
        BigDecimal basePrice = BigDecimal.valueOf(1500);
        int discount = 10;
        BigDecimal finalPrice = calculateFinalPrice.calculateFinalPrice(basePrice, discount);

        productSecond = ProductEntity.builder()
                .id(2L)
                .name("Dell")
                .productUUID("UUID2")
                .description("Description product 2")
                .categories(Categories.LAPTOP)
                .availability(Availability.AVAILABLE)
                .basePrice(basePrice)
                .finalPrice(finalPrice)
                .discountPercentage(discount)
                .amount(900)
                .hasDiscount(true)
                .isActive(true)
                .build();
        return productSecond;
    }

    private ProductEntity createProductThird() {
        productThird = ProductEntity.builder()
                .id(3L)
                .name("PANASONIC")
                .productUUID("UUID3")
                .description("Description product 3")
                .categories(Categories.MONITOR)
                .availability(Availability.AVAILABLE)
                .basePrice(BigDecimal.valueOf(2000D))
                .finalPrice(BigDecimal.valueOf(2000D))
                .discountPercentage(0)
                .amount(100)
                .hasDiscount(false)
                .isActive(true)
                .build();
        return productThird;
    }

    private ProductEntity createProductFourth() {
        productFourth = ProductEntity.builder()
                .id(4L)
                .name("XIAOMI")
                .productUUID("UUID4")
                .description("Description product 4")
                .categories(Categories.MONITOR)
                .availability(Availability.AVAILABLE)
                .basePrice(BigDecimal.valueOf(2000D))
                .finalPrice(BigDecimal.valueOf(2000D))
                .discountPercentage(0)
                .amount(100)
                .hasDiscount(false)
                .isActive(false)
                .build();
        return productFourth;
    }

    private ProductEntity createProductFifth() {
        productFifth = ProductEntity.builder()
                .id(5L)
                .name("APPLE")
                .productUUID("UUID5")
                .description("Description product 5")
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .basePrice(BigDecimal.valueOf(2000D))
                .finalPrice(BigDecimal.valueOf(2000D))
                .discountPercentage(0)
                .amount(100)
                .hasDiscount(false)
                .isActive(true)
                .build();
        return productFifth;
    }

    private ProductEntity createProductSixth() {
        productSixth = ProductEntity.builder()
                .id(6L)
                .name("SAMSUNG")
                .productUUID("UUID6")
                .description("Description product 6")
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .basePrice(BigDecimal.valueOf(2000D))
                .finalPrice(BigDecimal.valueOf(2000D))
                .discountPercentage(0)
                .amount(100)
                .hasDiscount(false)
                .isActive(true)
                .build();
        return productSixth;
    }

    private OrderEntity createOrderFirst() {
        orderFirst = OrderEntity.builder()
                .id(1L)
                .orderUUID("UUID1")
                .payloadUUID("UUID1")
                .paymentUUID("UUID1")
                .accountNumber("KW81CBKU0000000000001234560101")
                .isPaid(true)
                .paymentType(PaymentType.BANK_CARD)
                .orderStatus(OrderStatus.ORDER_SENT)
                .shipmentStatus(ShipmentStatus.SHIPMENT_IN_STORAGE)
                .orderBasePrice(BigDecimal.valueOf(200D))
                .orderFinalPrice(BigDecimal.valueOf(200D))
                .hasDiscount(false)
                .discountPercentage(0)
                .isCancelled(false)
                .creationDateTime(LocalDateTime.now())
                .build();
        return orderFirst;
    }

    private OrderEntity createOrderSecond() {
        orderSecond = OrderEntity.builder()
                .id(2L)
                .orderUUID("UUID2")
                .payloadUUID("UUID2")
                .paymentUUID("UUID2")
                .accountNumber("KW81CBKU0000000000001234560101")
                .isPaid(true)
                .paymentType(PaymentType.BANK_TRANSFER)
                .orderStatus(OrderStatus.ORDER_RETURNED)
                .shipmentStatus(ShipmentStatus.SHIPMENT_RETURNED_TO_SENDER)
                .orderBasePrice(BigDecimal.valueOf(300D))
                .orderFinalPrice(BigDecimal.valueOf(300D))
                .hasDiscount(false)
                .discountPercentage(0)
                .isCancelled(false)
                .creationDateTime(LocalDateTime.now())
                .build();
        return orderSecond;
    }

    private OrderProductEntity createOrderProductFirst() {
        int amount = 10;
        orderProductFirst = OrderProductEntity.builder()
                .id(1L)
                .unitPrice(productFirst.getFinalPrice())
                .collectionPrice(productFirst.getFinalPrice().multiply(BigDecimal.valueOf(amount)))
                .order(orderFirst)
                .product(productFirst)
                .amount(amount)
                .build();
        return orderProductFirst;
    }

    private OrderProductEntity createOrderProductSecond() {
        int amount = 20;
        orderProductSecond = OrderProductEntity.builder()
                .id(2L)
                .unitPrice(productSecond.getFinalPrice())
                .collectionPrice(productSecond.getFinalPrice().multiply(BigDecimal.valueOf(amount)))
                .order(orderFirst)
                .product(productSecond)
                .amount(amount)
                .build();
        return orderProductSecond;
    }

    private OrderProductEntity createOrderProductThird() {
        int amount = 30;
        orderProductThird = OrderProductEntity.builder()
                .id(3L)
                .unitPrice(productThird.getFinalPrice())
                .collectionPrice(productThird.getFinalPrice().multiply(BigDecimal.valueOf(amount)))
                .order(orderFirst)
                .product(productThird)
                .amount(amount)
                .build();
        return orderProductThird;
    }
}
