package practice.store.utils.initdata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import practice.store.customer.CustomerEntity;
import practice.store.order.OrderEntity;
import practice.store.order.OrderStatusEnum;
import practice.store.order.PaymentTypeEnum;
import practice.store.order.ShipmentStatusEnum;
import practice.store.order.details.OrderProductEntity;
import practice.store.product.Availability;
import practice.store.product.CategoriesEnum;
import practice.store.product.ProductEntity;
import practice.store.utils.numbers.CalculatePrice;

import java.math.BigDecimal;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
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
        customerThird = CustomerEntity.builder()
                .id(3L)
                .username("third")
                .password(passwordEncoder.encode("third"))
                .email("third@third.third")
                .isActive(false)
                .isCompany(true)
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
                .categoriesEnum(CategoriesEnum.PHONES)
                .basePrice(basePrice)
                .finalPrice(finalPrice)
                .amountPriceReduction(basePrice.subtract(finalPrice))
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
                .categoriesEnum(CategoriesEnum.LAPTOP)
                .availability(Availability.AVAILABLE)
                .basePrice(basePrice)
                .finalPrice(finalPrice)
                .amountPriceReduction(basePrice.subtract(finalPrice))
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
                .categoriesEnum(CategoriesEnum.MONITOR)
                .availability(Availability.AVAILABLE)
                .basePrice(BigDecimal.valueOf(2000D))
                .finalPrice(BigDecimal.valueOf(2000D))
                .amountPriceReduction(BigDecimal.valueOf(2000D))
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
                .categoriesEnum(CategoriesEnum.MONITOR)
                .availability(Availability.AVAILABLE)
                .basePrice(BigDecimal.valueOf(2000D))
                .finalPrice(BigDecimal.valueOf(2000D))
                .amountPriceReduction(BigDecimal.valueOf(2000D))
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
                .categoriesEnum(CategoriesEnum.PHONES)
                .availability(Availability.AVAILABLE)
                .basePrice(BigDecimal.valueOf(2000D))
                .finalPrice(BigDecimal.valueOf(2000D))
                .amountPriceReduction(BigDecimal.valueOf(2000D))
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
                .categoriesEnum(CategoriesEnum.PHONES)
                .availability(Availability.AVAILABLE)
                .basePrice(BigDecimal.valueOf(2000D))
                .finalPrice(BigDecimal.valueOf(2000D))
                .amountPriceReduction(BigDecimal.valueOf(2000D))
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
                .accountNumber("1111")
                .isPaid(true)
                .paymentTypeEnum(PaymentTypeEnum.BANK_CARD)
                .orderStatusEnum(OrderStatusEnum.ORDER_SENT)
                .shipmentStatusEnum(ShipmentStatusEnum.SHIPMENT_IN_STORAGE)
                .orderBasePrice(BigDecimal.valueOf(200D))
                .orderFinalPrice(BigDecimal.valueOf(200D))
                .hasDiscount(false)
                .discountPercentage(0)
                .creationDateTime(new Date())
                .build();
        return orderFirst;
    }

    private OrderEntity createOrderSecond() {
        orderSecond = OrderEntity.builder()
                .id(2L)
                .orderUUID("UUID2")
                .payloadUUID("UUID2")
                .accountNumber("2222")
                .isPaid(true)
                .paymentTypeEnum(PaymentTypeEnum.BANK_TRANSFER)
                .orderStatusEnum(OrderStatusEnum.ORDER_RETURNED)
                .shipmentStatusEnum(ShipmentStatusEnum.SHIPMENT_RETURNED_TO_SENDER)
                .orderBasePrice(BigDecimal.valueOf(300D))
                .orderFinalPrice(BigDecimal.valueOf(300D))
                .hasDiscount(false)
                .discountPercentage(0)
                .creationDateTime(new Date())
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
