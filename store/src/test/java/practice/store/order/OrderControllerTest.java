package practice.store.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import practice.store.customer.CustomerEntity;
import practice.store.customer.CustomerRepository;
import practice.store.order.details.OrderProductRepository;
import practice.store.product.ProductEntity;
import practice.store.product.ProductRepository;
import testdata.entity.TestDataCustomerEntity;
import testdata.entity.TestDataProductEntity;
import testdata.payload.TestDataOrderPayload;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class OrderControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mvc;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private OrderProductRepository orderProductRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private List<ProductEntity> productEntities;
    private ProductEntity product;
    private CustomerEntity customer;

    private final String EXCEPTION_MESSAGE_FIRST_PART = "Something went wrong. Contact administrator with code";
    private final String EXCEPTION_MESSAGE_SECOND_PART = String.format("Timestamp: %s", LocalDate.now());
    private final String MAIN_ENDPOINT = "/api/order/";


    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        enterCustomerTestData();
    }


    @Test
    void add_order_when_data_are_correct() throws Exception {
        // given
        CustomerEntity customer = TestDataCustomerEntity.Customer("super_unigue@email.address");
        customerRepository.save(customer);
        setAuthContext(customer.getEmail());

        int amountProductsInStock = 100;
        int amountProductInOrder = 5;

        ProductEntity productFirst = TestDataProductEntity.Product(amountProductsInStock);
        productRepository.save(productFirst);
        ProductEntity productSecond = TestDataProductEntity.Product(amountProductsInStock);
        productRepository.save(productSecond);

        String productFirstUUID = productFirst.getProductUUID();
        String productSecondUUID = productSecond.getProductUUID();

        OrderPayload orderPayload = TestDataOrderPayload.Order(productFirstUUID, productSecondUUID, amountProductInOrder);
        BigDecimal priceByAmountFirstProduct = productFirst.getFinalPrice().multiply(BigDecimal.valueOf(amountProductInOrder));
        BigDecimal priceByAmountSecondProduct = productSecond.getFinalPrice().multiply(BigDecimal.valueOf(amountProductInOrder));
        orderPayload.setOrderFinalPrice(priceByAmountFirstProduct.add(priceByAmountSecondProduct));

        CustomerEntity loggedCustomer = customerRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());


        // when
        mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderPayload))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(201));


        // then
        OrderEntity addedOrder = orderRepository.findByPayloadUUID(orderPayload.getPayloadUUID());
        assertThat(addedOrder)
                .usingRecursiveComparison()
                .ignoringFields("id", "orderUUID", "isPaid", "shipmentStatusEnum", "orderStatusEnum", "creationDateTime", "orderProduct", "customer")
                .isEqualTo(orderPayload);

        assertFalse(addedOrder.getIsPaid());
        assertEquals(ShipmentStatusEnum.SHIPMENT_AWAITING_FOR_ACCEPT, addedOrder.getShipmentStatusEnum());
        assertEquals(OrderStatusEnum.ORDER_AWAITING, addedOrder.getOrderStatusEnum());
        assertThat(loggedCustomer).isEqualTo(addedOrder.getCustomer());

        int amountFirstProductAfterAddedOrder = productRepository.findByProductUUID(productFirstUUID).getAmount();
        int amountSecondProductAfterAddedOrder = productRepository.findByProductUUID(productSecondUUID).getAmount();

        assertEquals((amountProductsInStock - amountProductInOrder), amountFirstProductAfterAddedOrder);
        assertEquals((amountProductsInStock - amountProductInOrder), amountSecondProductAfterAddedOrder);
    }

    @Test
    void add_order_without_product_test() throws Exception {
        // given
        OrderPayload orderPayload = TestDataOrderPayload.Order();


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderPayload))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @Test
    void add_order_with_not_existing_product_test() throws Exception {
        // given
        OrderPayload orderPayload = TestDataOrderPayload.Order("not exist uuid first", "not exist uuid second", 100);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderPayload))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @Test
    void add_order_when_product_amount_in_order_is_equal_zero_test() throws Exception {
        // given
        int amountProductsInStock = 100;
        int amountProductInOrder = 0;

        ProductEntity productFirst = TestDataProductEntity.Product(amountProductsInStock);
        productRepository.save(productFirst);
        ProductEntity productSecond = TestDataProductEntity.Product(amountProductsInStock);
        productRepository.save(productSecond);

        String productFirstUUID = productFirst.getProductUUID();
        String productSecondUUID = productSecond.getProductUUID();

        OrderPayload orderPayload = TestDataOrderPayload.Order(productFirstUUID, productSecondUUID, amountProductInOrder);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderPayload))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @Test
    void add_order_when_product_amount_in_stock_is_equal_zero_test() throws Exception {
        // given
        int amountProductsInStock = 0;
        int amountProductInOrder = 10;

        ProductEntity productFirst = TestDataProductEntity.Product(amountProductsInStock);
        productRepository.save(productFirst);
        ProductEntity productSecond = TestDataProductEntity.Product(amountProductsInStock);
        productRepository.save(productSecond);

        String productFirstUUID = productFirst.getProductUUID();
        String productSecondUUID = productSecond.getProductUUID();

        OrderPayload orderPayload = TestDataOrderPayload.Order(productFirstUUID, productSecondUUID, amountProductInOrder);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderPayload))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @Test
    void add_order_when_product_amount_in_stock_is_less_than_product_in_order_test() throws Exception {
        // given
        int amountProductsInStock = 5;
        int amountProductInOrder = 10;

        ProductEntity productFirst = TestDataProductEntity.Product(amountProductsInStock);
        productRepository.save(productFirst);
        ProductEntity productSecond = TestDataProductEntity.Product(amountProductsInStock);
        productRepository.save(productSecond);

        String productFirstUUID = productFirst.getProductUUID();
        String productSecondUUID = productSecond.getProductUUID();

        OrderPayload orderPayload = TestDataOrderPayload.Order(productFirstUUID, productSecondUUID, amountProductInOrder);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderPayload))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @Test
    void add_order_without_discount_with_prices_not_equals_test() throws Exception {
        // given
        int amountProductsInStock = 500;
        int amountProductInOrder = 10;

        ProductEntity productFirst = TestDataProductEntity.Product(amountProductsInStock);
        productRepository.save(productFirst);
        ProductEntity productSecond = TestDataProductEntity.Product(amountProductsInStock);
        productRepository.save(productSecond);

        String productFirstUUID = productFirst.getProductUUID();
        String productSecondUUID = productSecond.getProductUUID();

        OrderPayload orderPayload = TestDataOrderPayload.Order(productFirstUUID, productSecondUUID, amountProductInOrder);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderPayload))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @Test
    void add_order_with_discount_with_prices_not_equals_test() throws Exception {
        // given
        int amountProductsInStock = 500;

        ProductEntity productFirst = TestDataProductEntity.Product(amountProductsInStock);
        productRepository.save(productFirst);
        ProductEntity productSecond = TestDataProductEntity.Product(amountProductsInStock);
        productRepository.save(productSecond);

        String productFirstUUID = productFirst.getProductUUID();
        String productSecondUUID = productSecond.getProductUUID();

        OrderPayload orderPayload = TestDataOrderPayload.OrderWithDiscount(productFirstUUID, productSecondUUID);
        orderPayload.setOrderBasePrice(BigDecimal.valueOf(1000));
        orderPayload.setOrderFinalPrice(BigDecimal.valueOf(1000));


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderPayload))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @Test
    void add_order_with_discount_with_discount_equals_zero_test() throws Exception {
        // given
        int amountProductsInStock = 500;

        ProductEntity productFirst = TestDataProductEntity.Product(amountProductsInStock);
        productRepository.save(productFirst);
        ProductEntity productSecond = TestDataProductEntity.Product(amountProductsInStock);
        productRepository.save(productSecond);

        String productFirstUUID = productFirst.getProductUUID();
        String productSecondUUID = productSecond.getProductUUID();

        OrderPayload orderPayload = TestDataOrderPayload.OrderWithDiscount(productFirstUUID, productSecondUUID);
        orderPayload.setDiscountPercentage(0);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderPayload))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @Test
    void add_order_without_discount_with_discount_greater_than_zero_test() throws Exception {
        // given
        int amountProductsInStock = 500;

        ProductEntity productFirst = TestDataProductEntity.Product(amountProductsInStock);
        productRepository.save(productFirst);
        ProductEntity productSecond = TestDataProductEntity.Product(amountProductsInStock);
        productRepository.save(productSecond);

        String productFirstUUID = productFirst.getProductUUID();
        String productSecondUUID = productSecond.getProductUUID();

        OrderPayload orderPayload = TestDataOrderPayload.Order(productFirstUUID, productSecondUUID, 5);
        orderPayload.setOrderFinalPrice(BigDecimal.valueOf(900));
        orderPayload.setOrderBasePrice(BigDecimal.valueOf(900));
        orderPayload.setDiscountPercentage(10);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderPayload))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @Test
    void add_order_with_discount_with_incorrect_final_price_test() throws Exception {
        // given
        int amountProductsInStock = 500;

        ProductEntity productFirst = TestDataProductEntity.Product(amountProductsInStock);
        productRepository.save(productFirst);
        ProductEntity productSecond = TestDataProductEntity.Product(amountProductsInStock);
        productRepository.save(productSecond);

        String productFirstUUID = productFirst.getProductUUID();
        String productSecondUUID = productSecond.getProductUUID();

        OrderPayload orderPayload = TestDataOrderPayload.OrderWithDiscount(productFirstUUID, productSecondUUID);
        orderPayload.setOrderFinalPrice(BigDecimal.valueOf(800));
        orderPayload.setOrderBasePrice(BigDecimal.valueOf(900));


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderPayload))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }


    private void enterCustomerTestData() {
        customer = TestDataCustomerEntity.Customer("some@test.email");
        customer.setPassword(passwordEncoder.encode("top secret password"));
        customerRepository.save(customer);
    }

    private void setAuthContext(String email) {
        UserDetails userDetails = loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private UserDetails loadUserByUsername(String email) {
        CustomerEntity customerEntity = customerRepository.findByEmail(email);
        return new User(customerEntity.getEmail(), customerEntity.getPassword(), new ArrayList<>());
    }
}
