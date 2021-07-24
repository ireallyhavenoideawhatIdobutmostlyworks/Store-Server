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
import testdata.DataFactoryProduct;
import practice.store.utils.converter.EntitiesConverter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
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

    private final String EXCEPTION_MESSAGE_FIRST_PART = "Something went wrong. Contact administrator with code";
    private final String EXCEPTION_MESSAGE_SECOND_PART = String.format("Timestamp: %s", LocalDate.now());
    private final String MAIN_ENDPOINT = "/api/product/";


    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }


    @WithMockUser(username = "username")
    @Test
    void get_by_id_when_id_exist() throws Exception {
        // given
        ProductEntity productBeforeSave = DataFactoryProduct.createProductEntity();
        productRepository.save(productBeforeSave);
        ProductPayload payload = converter.convertProduct(productBeforeSave);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .get(MAIN_ENDPOINT + payload.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", Matchers.is(payload.getId().intValue())))
                .andReturn();


        // then
        ProductPayload productAsResponse =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProductPayload.class);

        assertThat(payload)
                .usingRecursiveComparison()
                .isEqualTo(productAsResponse);
    }

    @WithMockUser(username = "username")
    @Test
    void get_by_id_when_id_not_exist() throws Exception {
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
    void get_products_list_when_list_is_not_empty() throws Exception {
        // given
        List<ProductEntity> products = DataFactoryProduct.creteProductList();
        productRepository.saveAll(products);
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
    void get_products_empty_list_when_list_is_empty() throws Exception {
        // given
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
}