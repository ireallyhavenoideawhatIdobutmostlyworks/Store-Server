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
import testdata.entity.TestDataProductEntity;
import testdata.payload.TestDataProductPayload;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
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

        productPayloadWithDiscount = TestDataProductPayload.ProductWithDiscount();
        productPayloadWithoutDiscount = TestDataProductPayload.ProductWithoutDiscount();
    }


    @WithMockUser(username = "username")
    @Test
    void get_by_id_when_id_exist_test() throws Exception {
        // given
        String uuid = generateRandomString.generateRandomUuid();
        ProductEntity productBeforeSave = TestDataProductEntity.Product(uuid);
        productRepository.save(productBeforeSave);

        ProductPayload payload = converter.convertProduct(productBeforeSave);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .get(MAIN_ENDPOINT + uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.productUUID", Matchers.is(uuid)))
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

        List<ProductEntity> products = TestDataProductEntity.ProductList();
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
                .andExpect(jsonPath("$[0].productUUID", Matchers.is(payloads.get(0).getProductUUID())))
                .andExpect(jsonPath("$[1].productUUID", Matchers.is(payloads.get(1).getProductUUID())))
                .andExpect(jsonPath("$[2].productUUID", Matchers.is(payloads.get(2).getProductUUID())))
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
        String uuid = productPayloadWithDiscount.getProductUUID();


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
        assertThat(findByProductUUID(uuid))
                .usingRecursiveComparison()
                .ignoringFields("id", "orderProduct")
                .isEqualTo(productPayloadWithDiscount);
    }

    @WithMockUser(username = "username")
    @Test
    void add_product_without_discount_test() throws Exception {
        // given
        String uuid = productPayloadWithoutDiscount.getProductUUID();


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
        assertThat(findByProductUUID(uuid))
                .usingRecursiveComparison()
                .ignoringFields("id", "orderProduct")
                .isEqualTo(productPayloadWithoutDiscount);
    }

    @WithMockUser(username = "username")
    @Test
    void add_product_and_change_availability_to_AWAITING_FROM_MANUFACTURE_depends_on_stock_amount_test() throws Exception {
        // given
        String uuid = generateRandomString.generateRandomUuid();
        ProductPayload productPayload = TestDataProductPayload.ProductWithDiscount(uuid, 3);


        // when
        mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayload))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(201));


        // then
        assertThat(findByProductUUID(uuid))
                .usingRecursiveComparison()
                .ignoringFields("id", "availability", "orderProduct")
                .isEqualTo(productPayload);

        assertEquals(Availability.AWAITING_FROM_MANUFACTURE, findByProductUUID(uuid).getAvailability());
    }

    @WithMockUser(username = "username")
    @Test
    void add_product_and_change_availability_to_NOT_AVAILABLE_depends_on_stock_amount_test() throws Exception {
        // given
        String uuid = generateRandomString.generateRandomUuid();
        ProductPayload productPayload = TestDataProductPayload.ProductWithDiscount(uuid, 0);

        // when
        mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayload))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(201));


        // then
        assertThat(findByProductUUID(uuid))
                .usingRecursiveComparison()
                .ignoringFields("id", "availability", "orderProduct")
                .isEqualTo(productPayload);

        assertEquals(Availability.NOT_AVAILABLE, findByProductUUID(uuid).getAvailability());
    }

    @WithMockUser(username = "username")
    @Test
    void add_product_withdraw_from_sale_test() throws Exception {
        // given
        ProductPayload productPayload = TestDataProductPayload.ProductWithDiscount(Availability.WITHDRAW_FROM_SALE);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayload))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertFalse(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertFalse(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @WithMockUser(username = "username")
    @Test
    void add_product_where_uuid_is_exist_test() throws Exception {
        // given
        String uuidExist = "uuid exist test";
        ProductPayload productPayload = TestDataProductPayload.ProductWithDiscount(uuidExist);
        ProductEntity existProduct = TestDataProductEntity.Product(uuidExist);
        productRepository.save(existProduct);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayload))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertFalse(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertFalse(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @WithMockUser(username = "username")
    @Test
    void add_product_where_discount_percentage_is_to_high_test() throws Exception {
        // given
        ProductPayload productPayload = TestDataProductPayload.ProductWithDiscount(99);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayload))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertFalse(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertFalse(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @WithMockUser(username = "username")
    @Test
    void add_product_where_discount_percentage_is_to_low_test() throws Exception {
        // given
        ProductPayload productPayload = TestDataProductPayload.ProductWithDiscount(1);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayload))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertFalse(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertFalse(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @WithMockUser(username = "username")
    @Test
    void add_product_where_final_price_is_incorrect_test() throws Exception {
        // given
        ProductPayload productPayload = TestDataProductPayload.ProductWithDiscount(setBigDecimalWithScale(100));


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayload))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertFalse(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertFalse(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @WithMockUser(username = "username")
    @Test
    void add_product_where_final_price_reduction_is_incorrect_test() throws Exception {
        // given
        ProductPayload productPayload = TestDataProductPayload.ProductWithDiscount(setBigDecimalWithScale(99999));


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayload))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertFalse(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertFalse(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @WithMockUser(username = "username")
    @Test
    void add_product_where_discount_percentage_is_not_equal_zero_test() throws Exception {
        // given
        ProductPayload productPayload = TestDataProductPayload.ProductWithDiscount(99999);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayload))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertFalse(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertFalse(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @WithMockUser(username = "username")
    @Test
    void add_product_where_final_price_and_base_price_are_not_equals_test() throws Exception {
        // given
        ProductPayload productPayload = TestDataProductPayload.ProductWithDiscount(BigDecimal.valueOf(11111), BigDecimal.valueOf(99999));


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayload))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertFalse(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertFalse(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @WithMockUser(username = "username")
    @Test
    void edit_product_with_discount_test() throws Exception {
        // given
        String uuid = new GenerateRandomString().generateRandomUuid();
        String newProductName = "Awesome new product name";

        ProductEntity existProduct = TestDataProductEntity.Product(uuid);
        productRepository.save(existProduct);

        ProductPayload productPayloadForEdit = TestDataProductPayload.ProductWithDiscount(newProductName, uuid);


        // when
        mvc
                .perform(MockMvcRequestBuilders
                        .put(MAIN_ENDPOINT + uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayloadForEdit))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(204));


        // then
        assertThat(findByProductUUID(uuid))
                .usingRecursiveComparison()
                .ignoringFields("id", "orderProduct")
                .isEqualTo(productPayloadForEdit);
    }

    @WithMockUser(username = "username")
    @Test
    void edit_product_without_discount_test() throws Exception {
        // given
        String uuid = new GenerateRandomString().generateRandomUuid();
        String newProductName = "Awesome new product name";

        ProductEntity existProduct = TestDataProductEntity.ProductWithoutDiscount(uuid);
        productRepository.save(existProduct);

        ProductPayload productPayloadForEdit = TestDataProductPayload.ProductWithoutDiscount(newProductName, uuid);


        // when
        mvc
                .perform(MockMvcRequestBuilders
                        .put(MAIN_ENDPOINT + uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayloadForEdit))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(204));


        // then
        assertThat(findByProductUUID(uuid))
                .usingRecursiveComparison()
                .ignoringFields("orderProduct", "id")
                .isEqualTo(productPayloadForEdit);
    }

    @WithMockUser(username = "username")
    @Test
    void edit_product_from_with_to_without_discount_test() throws Exception {
        // given
        ProductEntity existProduct = TestDataProductEntity.Product();
        productRepository.save(existProduct);

        String uuid = existProduct.getProductUUID();
        assertTrue(findByProductUUID(uuid).isHasDiscount());

        ProductPayload productPayloadForEdit = TestDataProductPayload.ProductWithoutDiscount(uuid);


        // when
        mvc
                .perform(MockMvcRequestBuilders
                        .put(MAIN_ENDPOINT + uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayloadForEdit))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(204));


        // then
        assertThat(findByProductUUID(uuid))
                .usingRecursiveComparison()
                .ignoringFields("orderProduct", "id")
                .isEqualTo(productPayloadForEdit);

        assertFalse(findByProductUUID(uuid).isHasDiscount());
    }

    @WithMockUser(username = "username")
    @Test
    void edit_product_from_without_to_with_discount_test() throws Exception {
        ProductEntity existProduct = TestDataProductEntity.ProductWithoutDiscount();
        productRepository.save(existProduct);

        String uuid = existProduct.getProductUUID();
        assertFalse(findByProductUUID(uuid).isHasDiscount());

        ProductPayload productPayloadForEdit = TestDataProductPayload.ProductWithDiscount(uuid);


        // when
        mvc
                .perform(MockMvcRequestBuilders
                        .put(MAIN_ENDPOINT + uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayloadForEdit))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(204));


        // then
        assertThat(findByProductUUID(uuid))
                .usingRecursiveComparison()
                .ignoringFields("orderProduct", "id")
                .isEqualTo(productPayloadForEdit);

        assertTrue(findByProductUUID(uuid).isHasDiscount());
    }

    @WithMockUser(username = "username")
    @Test
    void edit_product_when_uuid_not_exist_test() throws Exception {
        // given
        ProductEntity existProduct = TestDataProductEntity.Product();
        productRepository.save(existProduct);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .put(MAIN_ENDPOINT + "uuid not exist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPayloadWithDiscount))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertFalse(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertFalse(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }


    private ProductEntity findByProductUUID(String uuid) {
        return productRepository
                .findByProductUUID(uuid)
                .orElseThrow((() -> new EntityNotFoundException(String.format("Entity with UUID: %s not found", uuid))));
    }

    protected static BigDecimal setBigDecimalWithScale(long val) {
        return BigDecimal.valueOf(val).setScale(2, RoundingMode.HALF_UP);
    }
}