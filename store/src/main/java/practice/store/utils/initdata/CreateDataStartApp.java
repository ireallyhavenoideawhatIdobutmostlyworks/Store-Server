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
import practice.store.product.Availability;
import practice.store.product.Categories;
import practice.store.product.ProductEntity;
import practice.store.utils.numbers.CalculatePriceProduct;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Component
@Getter
public class CreateDataStartApp {

    private final PasswordEncoder passwordEncoder;
    private final CalculatePriceProduct calculateFinalPrice;

    private CustomerEntity customerFirst, customerSecond, customerThird;
    private Set<CustomerEntity> customers;

    private ProductEntity productFirst, productSecond, productThird, productFourth, productFifth, productSixth;
    private Set<ProductEntity> products;

    private OrderEntity orderFirst, orderSecond;
    private Set<OrderEntity> orders;

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

    public void createOrders() {
        orderFirst = createOrderFirst();
        orderSecond = createOrderSecond();
        orders = new HashSet<>(Arrays.asList(orderFirst, orderSecond));
    }


    private CustomerEntity createCustomerFirst() {
        customerFirst = CustomerEntity.builder()
                .id(1L)
                .username("first")
                .password(passwordEncoder.encode("first"))
                .email("first@first.first")
                .isActive(true)
                .isCompany(false)
                .build();
        return customerFirst;
    }

    private CustomerEntity createCustomerSecond() {
        customerSecond = CustomerEntity.builder()
                .id(2L)
                .username("second")
                .password(passwordEncoder.encode(passwordSecondCustomer))
                .email(emailSecondCustomer)
                .isActive(true)
                .isCompany(true)
                .build();
        return customerSecond;
    }

    private CustomerEntity createCustomerThirdInactive() {
        customerSecond = CustomerEntity.builder()
                .id(3L)
                .username("third")
                .password(passwordEncoder.encode("third"))
                .email("third@third.third")
                .isActive(false)
                .isCompany(true)
                .build();
        return customerSecond;
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
                .amountPriceReduction(basePrice.subtract(finalPrice))
                .discountPercentage(discount)
                .amountInStock(10)
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
                .amountPriceReduction(basePrice.subtract(finalPrice))
                .discountPercentage(discount)
                .amountInStock(9)
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
                .amountPriceReduction(BigDecimal.valueOf(2000D))
                .discountPercentage(0)
                .amountInStock(10)
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
                .availability(Availability.WITHDRAW_FROM_SALE)
                .basePrice(BigDecimal.valueOf(2000D))
                .finalPrice(BigDecimal.valueOf(2000D))
                .amountPriceReduction(BigDecimal.valueOf(2000D))
                .discountPercentage(0)
                .amountInStock(10)
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
                .availability(Availability.AWAITING_FROM_MANUFACTURE)
                .basePrice(BigDecimal.valueOf(2000D))
                .finalPrice(BigDecimal.valueOf(2000D))
                .amountPriceReduction(BigDecimal.valueOf(2000D))
                .discountPercentage(0)
                .amountInStock(10)
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
                .availability(Availability.NOT_AVAILABLE)
                .basePrice(BigDecimal.valueOf(2000D))
                .finalPrice(BigDecimal.valueOf(2000D))
                .amountPriceReduction(BigDecimal.valueOf(2000D))
                .discountPercentage(0)
                .amountInStock(10)
                .hasDiscount(false)
                .isActive(true)
                .build();
        return productSixth;
    }

    private OrderEntity createOrderFirst() {
        orderFirst = OrderEntity.builder()
                .id(1L)
                .orderUUID("UUID1")
                .accountNumber("1111")
                .isPaid(true)
                .paymentType(PaymentType.BANK_CARD)
                .orderStatus(OrderStatus.ORDER_SENT)
                .shipmentStatus(ShipmentStatus.SHIPMENT_IN_STORAGE)
                .orderPrice(BigDecimal.valueOf(200D))
                .build();
        return orderFirst;
    }

    private OrderEntity createOrderSecond() {
        orderSecond = OrderEntity.builder()
                .id(2L)
                .orderUUID("UUID2")
                .accountNumber("2222")
                .isPaid(true)
                .paymentType(PaymentType.BANK_TRANSFER)
                .orderStatus(OrderStatus.ORDER_RETURNED)
                .shipmentStatus(ShipmentStatus.SHIPMENT_RETURNED_TO_SENDER)
                .orderPrice(BigDecimal.valueOf(300D))
                .build();
        return orderSecond;
    }
}
