package practice.store.integration.customer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import practice.store.DataFactory;
import practice.store.customer.CustomerEntity;
import practice.store.customer.CustomerPayload;
import practice.store.customer.CustomerRepository;
import practice.store.utils.converter.EntitiesConverter;
import practice.store.utils.converter.PayloadsConverter;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
class CustomerControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mvc;

    @Autowired
    private CustomerRepository customerRepository;

    private CustomerEntity existingCustomerEntityFirst;
    private CustomerEntity existingCustomerEntitySecond;
    private CustomerEntity existingCustomerEntityThird;
    private List<CustomerEntity> existingCustomerEntityList;

    private CustomerPayload existingCustomerPayload;

    private EntitiesConverter entitiesConverter;
    private PayloadsConverter payloadsConverter;

    private final String MAIN_ENDPOINT = "/api/customer/";


    @BeforeAll
    void prepareTestData() {
        createCustomers();
        assertThat(customerRepository.findAll()).size().isEqualTo(existingCustomerEntityList.size());
    }

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
        assertThat(customerRepository.findAll()).isEmpty();
    }


    @WithMockUser(username = "username")
    @Transactional
    @Test
    void getById() throws Exception {
        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .get(MAIN_ENDPOINT + existingCustomerEntityFirst.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andReturn();

        // then
        CustomerEntity customerAsResponse =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CustomerEntity.class);

        assertThat(existingCustomerEntityFirst)
                .usingRecursiveComparison()
                .ignoringFields("password")
                .isEqualTo(customerAsResponse);
    }

//    @WithMockUser(username = "username")
//    @Transactional
//    @Test
//    void getCustomersList() throws Exception {
//        // when
//        MvcResult mvcResult = mvc
//                .perform(MockMvcRequestBuilders
//                        .get(MAIN_ENDPOINT)
//                        .contentType(MediaType.APPLICATION_JSON)
//                )
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().is(200))
//                .andReturn();
//
//        // then
//        List<CustomerEntity> meetingsAsResponse =
//                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
//
//        assertThat(meetingsAsResponse).hasSize(existingCustomerEntityList.size());
//
//        assertThat(meetingsAsResponse)
//                .usingRecursiveComparison()
//                .ignoringFields("password")
//                .isEqualTo(existingCustomerEntityList);
//    }

    @Test
    void save() {
    }

    @Test
    void edit() {
    }

    @Test
    void remove() {
    }


    private void createCustomers() {
        existingCustomerEntityFirst = DataFactory.createCustomerEntity(1L, "test name1", "test password1", "test@email.test1", true, true);
        existingCustomerEntitySecond = DataFactory.createCustomerEntity(2L, "test name2", "test password2", "test@email.test2", true, true);
        existingCustomerEntityThird = DataFactory.createCustomerEntity(3L, "test name3", "test password3", "test@email.test3", true, true);
        existingCustomerEntityList = Arrays.asList(existingCustomerEntityFirst, existingCustomerEntitySecond, existingCustomerEntityThird);
        customerRepository.saveAll(existingCustomerEntityList);
    }
}