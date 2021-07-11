package practice.store.integration.customer;

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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import practice.store.DataFactory;
import practice.store.customer.CustomerEntity;
import practice.store.customer.CustomerRepository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mvc;

    @Autowired
    private CustomerRepository customerRepository;

    private final String EXCEPTION_MESSAGE_FIRST_PART = "Something went wrong. Contact administrator with code";
    private final String EXCEPTION_MESSAGE_SECOND_PART = String.format("Timestamp: %s", LocalDate.now());
    private final String MAIN_ENDPOINT = "/api/customer/";


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
        CustomerEntity customerBeforeSave = DataFactory.createCustomerEntity("some@test.email");
        customerRepository.save(customerBeforeSave);
        CustomerEntity customerAfterSave = customerRepository.findByEmail(customerBeforeSave.getEmail());


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .get(MAIN_ENDPOINT + customerAfterSave.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", Matchers.is(customerAfterSave.getId().intValue())))
                .andReturn();


        // then
        CustomerEntity customerAsResponse =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CustomerEntity.class);

        assertThat(customerAfterSave)
                .usingRecursiveComparison()
                .ignoringFields("password")
                .isEqualTo(customerAsResponse);
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
    void get_customers_list_when_list_is_not_empty() throws Exception {
        // given
        List<CustomerEntity> customers = DataFactory.creteCustomerList();
        customerRepository.saveAll(customers);
        List<CustomerEntity> existingCustomerEntityList = customerRepository.findAll();


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .get(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].id", Matchers.is(existingCustomerEntityList.get(0).getId().intValue())))
                .andExpect(jsonPath("$[1].id", Matchers.is(existingCustomerEntityList.get(1).getId().intValue())))
                .andExpect(jsonPath("$[2].id", Matchers.is(existingCustomerEntityList.get(2).getId().intValue())))
                .andReturn();


        // then
        List<CustomerEntity> meetingsAsResponse =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});

        assertThat(meetingsAsResponse).hasSize(existingCustomerEntityList.size());

        assertThat(meetingsAsResponse)
                .usingRecursiveComparison()
                .ignoringFields("password")
                .isEqualTo(existingCustomerEntityList);
    }

    @WithMockUser(username = "username")
    @Test
    void get_customers_empty_list_when_list_is_empty() throws Exception {
        // given
        assertTrue(customerRepository.findAll().isEmpty());


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
        List<CustomerEntity> meetingsAsResponse =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
                });

        assertThat(meetingsAsResponse).hasSize(customerRepository.findAll().size());
    }

    @WithMockUser(username = "username")
    @Test
    void add_customer_when_data_are_correct() throws Exception {
        // given
        customerRepository.deleteAll();
        List<CustomerEntity> existingCustomerEntityList = DataFactory.creteCustomerList();
        customerRepository.saveAll(existingCustomerEntityList);
        CustomerEntity entityToSave = DataFactory.createCustomerEntity();


        // when
        mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entityToSave))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(201));


        // then
        assertThat(customerRepository.findAll()).hasSize(4);

        assertThat(customerRepository.findAll().get(3))
                .usingRecursiveComparison()
                .ignoringFields("id", "password")
                .isEqualTo(entityToSave);
    }

    @WithMockUser(username = "username")
    @Test
    void not_add_customer_when_email_is_exist() throws Exception {
        // given
        customerRepository.saveAll(DataFactory.creteCustomerList());

        String emailExist = customerRepository.findAll().get(0).getEmail();
        CustomerEntity entityToSave = DataFactory.createCustomerEntity(emailExist);

        int customersListSizeBeforePerformPostRequest = customerRepository.findAll().size();


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entityToSave))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertThat(customerRepository.findAll()).hasSize(customersListSizeBeforePerformPostRequest);

        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @WithMockUser(username = "username")
    @Transactional
    @Test
    void edit_customer_when_data_are_correct() throws Exception {
        // given
        customerRepository.saveAll(DataFactory.creteCustomerList());
        int customersListSizeBeforePerformPutRequest = customerRepository.findAll().size();

        long idExist = customerRepository.findAll().get(0).getId();
        String emailExist = customerRepository.findAll().get(0).getEmail();
        CustomerEntity entityForPutRequest = DataFactory.createCustomerEntity(idExist, emailExist);


        // when
        mvc
                .perform(MockMvcRequestBuilders
                        .put(MAIN_ENDPOINT + idExist)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entityForPutRequest))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(204));


        // then
        assertThat(customerRepository.findAll()).hasSize(customersListSizeBeforePerformPutRequest);

        CustomerEntity customerAfterEditFromDatabase = customerRepository.getById(idExist);
        assertThat(customerAfterEditFromDatabase)
                .usingRecursiveComparison()
                .ignoringFields("password")
                .isEqualTo(entityForPutRequest);
    }

    @WithMockUser(username = "username")
    @Transactional
    @Test
    void not_edit_customer_when_id_email_not_exist() throws Exception {
        // given
        customerRepository.saveAll(DataFactory.creteCustomerList());
        int customersListSizeBeforePerformPutRequest = customerRepository.findAll().size();

        long idBelongToFirstCustomer = customerRepository.findAll().get(0).getId();
        String emailNotExist = "email@not.exist";

        CustomerEntity entityForPutRequestWithIncorrectData = DataFactory.createCustomerEntity(idBelongToFirstCustomer, emailNotExist);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .put(MAIN_ENDPOINT + idBelongToFirstCustomer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entityForPutRequestWithIncorrectData))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertThat(customerRepository.findAll()).hasSize(customersListSizeBeforePerformPutRequest);

        CustomerEntity customerAfterEditFromDatabase = customerRepository.getById(idBelongToFirstCustomer);
        assertThat(customerAfterEditFromDatabase)
                .usingRecursiveComparison()
                .isNotEqualTo(entityForPutRequestWithIncorrectData);

        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @WithMockUser(username = "username")
    @Transactional
    @Test
    void not_edit_customer_when_id_and_email_not_belong_to_the_same_customer() throws Exception {
        // given
        customerRepository.saveAll(DataFactory.creteCustomerList());
        int customersListSizeBeforePerformPutRequest = customerRepository.findAll().size();

        long idBelongToFirstCustomer = customerRepository.findAll().get(0).getId();
        String emailBelongToSecondCustomer = customerRepository.findAll().get(1).getEmail();

        CustomerEntity entityForPutRequestWithIncorrectData = DataFactory.createCustomerEntity(idBelongToFirstCustomer, emailBelongToSecondCustomer);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .put(MAIN_ENDPOINT + idBelongToFirstCustomer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entityForPutRequestWithIncorrectData))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();


        // then
        assertThat(customerRepository.findAll()).hasSize(customersListSizeBeforePerformPutRequest);

        CustomerEntity customerAfterEditFromDatabase = customerRepository.getById(idBelongToFirstCustomer);
        assertThat(customerAfterEditFromDatabase)
                .usingRecursiveComparison()
                .isNotEqualTo(entityForPutRequestWithIncorrectData);

        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @WithMockUser(username = "username")
    @Test
    void delete_entity_when_id_not_exist() throws Exception {
        // given
        customerRepository.saveAll(DataFactory.creteCustomerList());
        int customersListSizeBeforePerformDeleteRequest = customerRepository.findAll().size();
        long idNotExist = customersListSizeBeforePerformDeleteRequest + 10;


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .delete(MAIN_ENDPOINT + idNotExist)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404)).andReturn();


        // then
        assertEquals(customersListSizeBeforePerformDeleteRequest, customerRepository.findAll().size());

        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_FIRST_PART));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(EXCEPTION_MESSAGE_SECOND_PART));
    }

    @WithMockUser(username = "username")
    @Transactional
    @Test
    void change_isActive_field_when_id_exist() throws Exception {
        // given
        customerRepository.saveAll(DataFactory.creteCustomerList());
        int customersListSizeBeforePerformDeleteRequest = customerRepository.findAll().size();
        long idExist = customerRepository.findAll().get(0).getId();


        // when
        mvc
                .perform(MockMvcRequestBuilders
                        .delete(MAIN_ENDPOINT + idExist)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(204));


        // then
        assertEquals(customersListSizeBeforePerformDeleteRequest, customerRepository.findAll().size());

        CustomerEntity customerAfterPerformDeleteRequest = customerRepository.getById(idExist);
        assertFalse(customerAfterPerformDeleteRequest.isActive());
    }
}