package practice.store.product;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import practice.store.utils.converter.EntitiesConverter;
import practice.store.utils.values.GenerateRandomString;
import testdata.DataFactoryProduct;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mvc;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private EntitiesConverter converter;
    @Autowired
    private GenerateRandomString generateRandomString;

    private ProductPayload productPayloadWithDiscount;
    private ProductPayload productPayloadWithoutDiscount;

    private final String EXCEPTION_MESSAGE_FIRST_PART = "Something went wrong. Contact administrator with code";
    private final String EXCEPTION_MESSAGE_SECOND_PART = String.format("Timestamp: %s", LocalDate.now());
    private final String MAIN_ENDPOINT = "/api/product/";


    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        productPayloadWithDiscount = DataFactoryProduct.createProductPayloadWithDiscount();
        productPayloadWithoutDiscount = DataFactoryProduct.createProductPayloadWithoutDiscount();
    }


    @WithMockUser(username = "username")
    @Test
    void get_by_id_when_id_exist_test() throws Exception {
        // given
        String uuid = generateRandomString.generateRandomUuid();
        ProductEntity productBeforeSave = DataFactoryProduct.createProductEntity(uuid);
        productRepository.save(productBeforeSave);

        ProductPayload payload = converter.convertProduct(productBeforeSave);
        Long id = productRepository.findByProductUUID(uuid).getId();


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .get(MAIN_ENDPOINT + id)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", Matchers.is(id.intValue())))
                .andReturn();


        // then
        ProductPayload productAsResponse =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProductPayload.class);

        assertThat(payload)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(productAsResponse);
    }

    @WithMockUser(username = "username")
    @Test
    void get_by_id_when_id_not_exist_test() throws Exception {
        // given
        long idNotExist = 111L;


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .get(MAIN_ENDPOINT + idNotExist)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404))
                .andReturn();


        // then
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @WithMockUser(username = "username")
    @Test
    void get_products_list_when_list_is_not_empty_test() throws Exception {
        // given
        productRepository.deleteAll();
        assertThat(productRepository.findAll()).isEmpty();

        List<ProductEntity> products = DataFactoryProduct.creteProductList();
        productRepository.saveAll(products);
        assertThat(productRepository.findAll()).hasSize(products.size());

        List<ProductEntity> existingProductEntityList = productRepository.findAll();
        List<ProductPayload> payloads = existingProductEntityList.stream().map(converter::convertProduct).collect(Collectors.toList());


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .get(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].id", Matchers.is(payloads.get(0).getId().intValue())))
                .andExpect(jsonPath("$[1].id", Matchers.is(payloads.get(1).getId().intValue())))
                .andExpect(jsonPath("$[2].id", Matchers.is(payloads.get(2).getId().intValue())))
                .andReturn();


        // then
        List<ProductPayload> productsAsResponse =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
                });

        assertThat(productsAsResponse).hasSize(payloads.size());

        assertThat(productsAsResponse)
                .usingRecursiveComparison()
                .isEqualTo(payloads);
    }

    @WithMockUser(username = "username")
    @Test
    void get_products_empty_list_when_list_is_empty_test() throws Exception {
        // given
        productRepository.deleteAll();
        assertTrue(productRepository.findAll().isEmpty());


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .get(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andReturn();


        // then
        List<ProductPayload> productsAsResponse =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
                });

        assertThat(productsAsResponse).isEmpty();
    }

    @WithMockUser(username = "username")
    @Test
    void add_product_with_discount_test() throws Exception {
        // given
        String uuid = generateRandomString.generateRandomUuid();
        productPayloadWithDiscount.setProductUUID(uuid);


        // when
        mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayloadWithDiscount))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(201));


        // then
        assertThat(productRepository.findByProductUUID(uuid))
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(productPayloadWithDiscount);
    }

    @WithMockUser(username = "username")
    @Test
    void add_product_without_discount_test() throws Exception {
        // given
        String uuid = generateRandomString.generateRandomUuid();
        productPayloadWithoutDiscount.setProductUUID(uuid);


        // when
        mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayloadWithoutDiscount))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(201));


        // then
        assertThat(productRepository.findByProductUUID(uuid))
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(productPayloadWithoutDiscount);
    }

    @WithMockUser(username = "username")
    @Test
    void add_product_and_change_availability_to_AWAITING_FROM_MANUFACTURE_depends_on_stock_amount_test() throws Exception {
        // given
        String uuid = generateRandomString.generateRandomUuid();
        productPayloadWithDiscount.setProductUUID(uuid);
        productPayloadWithDiscount.setAmountInStock(3);


        // when
        mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayloadWithDiscount))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(201));


        // then
        assertThat(productRepository.findByProductUUID(uuid))
                .usingRecursiveComparison()
                .ignoringFields("id", "availability")
                .isEqualTo(productPayloadWithDiscount);

        assertEquals(Availability.AWAITING_FROM_MANUFACTURE, productRepository.findByProductUUID(uuid).getAvailability());
    }

    @WithMockUser(username = "username")
    @Test
    void add_product_and_change_availability_to_NOT_AVAILABLE_depends_on_stock_amount_test() throws Exception {
        // given
        String uuid = generateRandomString.generateRandomUuid();
        productPayloadWithDiscount.setProductUUID(uuid);
        productPayloadWithDiscount.setAmountInStock(0);

        // when
        mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayloadWithDiscount))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(201));


        // then
        assertThat(productRepository.findByProductUUID(uuid))
                .usingRecursiveComparison()
                .ignoringFields("id", "availability")
                .isEqualTo(productPayloadWithDiscount);

        assertEquals(Availability.NOT_AVAILABLE, productRepository.findByProductUUID(uuid).getAvailability());
    }

    @WithMockUser(username = "username")
    @Test
    void add_product_withdraw_from_sale_test() throws Exception {
        // given
        productPayloadWithDiscount.setAvailability(Availability.WITHDRAW_FROM_SALE);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayloadWithDiscount))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @WithMockUser(username = "username")
    @Test
    void add_product_where_uuid_is_exist_test() throws Exception {
        // given
        String uuidExist = "uuid exist test";
        productPayloadWithDiscount.setProductUUID(uuidExist);

        ProductEntity existProduct = DataFactoryProduct.createProductEntity(uuidExist);
        productRepository.save(existProduct);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayloadWithDiscount))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @WithMockUser(username = "username")
    @Test
    void add_product_where_discount_percentage_is_to_high_test() throws Exception {
        // given
        productPayloadWithDiscount.setDiscountPercentage(99);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayloadWithDiscount))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @WithMockUser(username = "username")
    @Test
    void add_product_where_discount_percentage_is_to_low_test() throws Exception {
        // given
        productPayloadWithDiscount.setDiscountPercentage(1);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayloadWithDiscount))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @WithMockUser(username = "username")
    @Test
    void add_product_where_final_price_is_incorrect_test() throws Exception {
        // given
        productPayloadWithDiscount.setFinalPrice(99999);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayloadWithDiscount))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @WithMockUser(username = "username")
    @Test
    void add_product_where_final_price_reduction_is_incorrect_test() throws Exception {
        // given
        productPayloadWithDiscount.setFinalPrice(99999);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayloadWithDiscount))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @WithMockUser(username = "username")
    @Test
    void add_product_where_discount_percentage_is_not_equal_zero_test() throws Exception {
        // given
        productPayloadWithDiscount.setDiscountPercentage(99999);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayloadWithDiscount))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @WithMockUser(username = "username")
    @Test
    void add_product_where_price_reduction_is_not_equal_zero_test() throws Exception {
        // given
        productPayloadWithDiscount.setAmountPriceReduction(99999);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayloadWithDiscount))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @WithMockUser(username = "username")
    @Test
    void add_product_where_final_price_and_base_price_are_not_equals_test() throws Exception {
        // given
        productPayloadWithDiscount.setBasePrice(11111);
        productPayloadWithDiscount.setFinalPrice(99999);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayloadWithDiscount))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }
}