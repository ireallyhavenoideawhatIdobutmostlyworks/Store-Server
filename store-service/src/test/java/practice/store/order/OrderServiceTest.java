//package practice.store.order;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import practice.store.customer.CustomerRepository;
//import practice.store.order.details.OrderProductRepository;
//import practice.store.rabbit.services.mail.SenderMailService;
//import practice.store.rabbit.services.pdf.SenderPdfService;
//import practice.store.product.ProductPayload;
//import practice.store.product.ProductRepository;
//import practice.store.product.ProductService;
//import practice.store.utils.converter.EntitiesConverter;
//import practice.store.utils.converter.PayloadsConverter;
//import practice.store.utils.numbers.CalculatePrice;
//import practice.store.utils.values.GenerateRandomString;
//import testdata.entity.TestDataOrderEntity;
//import testdata.payload.TestDataOrderPayload;
//import testdata.payload.TestDataProductPayload;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("Tests for order service")
//class OrderServiceTest {
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @Mock
//    private OrderRepository orderRepository;
//    @Mock
//    private CustomerRepository customerRepository;
//    @Mock
//    private ProductRepository productRepository;
//    @Mock
//    private OrderProductRepository orderProductRepository;
//
//    private OrderService orderService;
//    private OrderEntity orderEntity;
//    private OrderPayload orderPayloadWithDiscount, orderPayloadWithoutDiscount;
//
//    private ProductPayload productFirst, productSecond;
//
//    private CalculatePrice calculateFinalPrice;
//    private GenerateRandomString generateRandomString;
//
//    private EntitiesConverter entitiesConverter;
//    private PayloadsConverter payloadsConverter;
//
//    private ProductService productService;
//
//    private SenderMailService senderMailService;
//    private SenderPdfService senderPdfService;
//
//
//    @BeforeEach
//    void setUp() {
//        entitiesConverter = new EntitiesConverter();
//        payloadsConverter = new PayloadsConverter(passwordEncoder);
//
//        orderService = new OrderService(orderRepository, customerRepository, productRepository, orderProductRepository, payloadsConverter, entitiesConverter, generateRandomString, calculateFinalPrice, productService, senderMailService, senderPdfService);
//
//        productFirst = TestDataProductPayload.ProductWithDiscount();
//        productSecond = TestDataProductPayload.ProductWithDiscount();
//
//        orderEntity = TestDataOrderEntity.Order();
//        orderPayloadWithDiscount = TestDataOrderPayload.OrderWithDiscount(productFirst.getProductUUID(), productSecond.getProductUUID());
//        orderPayloadWithoutDiscount = TestDataOrderPayload.OrderWithoutDiscount(productFirst.getProductUUID(), productSecond.getProductUUID());
//    }
//
//    @DisplayName("Add order when data is correct")
//    @Test
//    void should_add_order_when_payload_is_correct_test() {
//
//    }
//
//    @DisplayName("Throw exception when order does not have product")
//    @Test
//    void should_throw_exception_when_order_does_not_have_product_test() {
//
//    }
//
//    @DisplayName("Throw exception when product UUID not exist")
//    @Test
//    void should_throw_exception_when_product_uuid_not_exist_test() {
//
//    }
//
//    @DisplayName("Throw exception when product amount is equal zero")
//    @Test
//    void should_throw_exception_when_product_amount_is_equal_zero_test() {
//
//    }
//
//    @DisplayName("Throw exception when product amount is greater than product amount in stock")
//    @Test
//    void should_throw_exception_when_product_amount_is_greater_than_product_amount_in_stock_test() {
//
//    }
//
//    @DisplayName("Throw exception when order not has discount and prices are not equal")
//    @Test
//    void should_throw_exception_when_order_not_has_discount_and_prices_are_not_equal_test() {
//
//    }
//
//    @DisplayName("Throw exception when order has discount and prices are equal")
//    @Test
//    void should_throw_exception_when_order_has_discount_and_prices_are_equal_test() {
//
//    }
//
//    @DisplayName("Throw exception when order has discount and discount percentage is equal zero")
//    @Test
//    void should_throw_exception_when_order_has_discount_and_discount_percentage_is_equal_zero_test() {
//
//    }
//
//    @DisplayName("Throw exception when order not has discount and discount percentage is grater than zero")
//    @Test
//    void should_throw_exception_when_order_not_has_discount_and_discount_percentage_is_greater_zero_test() {
//
//    }
//
//    @DisplayName("Throw exception when order has discount and prices are incorrect with discount percentage")
//    @Test
//    void should_throw_exception_when_order_has_discount_and_prices_are_incorrect_with_discount_percentage_test() {
//
//    }
//}
