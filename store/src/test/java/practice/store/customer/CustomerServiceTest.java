package practice.store.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import practice.store.utils.converter.EntitiesConverter;
import practice.store.utils.converter.PayloadsConverter;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test customer service")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private EntitiesConverter entitiesConverter;
    private PayloadsConverter payloadsConverter;
    private CustomerEntity customerEntity;
    private CustomerService customerService;


    @BeforeEach
    void setUp() {
        entitiesConverter = new EntitiesConverter();
        payloadsConverter = new PayloadsConverter(passwordEncoder);
        customerEntity = createCustomerEntity();
        customerService = new CustomerService(customerRepository, entitiesConverter, payloadsConverter);
    }

    @DisplayName("Return customer by ID")
    @Test
    void should_return_customer_when_id_is_valid_test() {
        // given
        long id = customerEntity.getId();


        // when
        when(customerRepository.getById(id)).thenReturn(customerEntity);

        CustomerPayload customerPayload = entitiesConverter.convertCustomer(customerEntity);
        CustomerPayload customerPayloadReturnedFromService = customerService.getById(customerPayload.getId());


        // then
        assertEquals(
                customerPayload,
                customerPayloadReturnedFromService);
    }


    @DisplayName("Throw exception when ID is not exist")
    @Test
    void should_throw_exception_when_id_is_not_exist_test() {
        // given
        String exceptionMessage = "Unable to find practice.store.customer.CustomerEntity with id %d";
        long idWhichNotExist = 11L;


        // when
        when(customerRepository.getById(idWhichNotExist))
                .thenThrow(new EntityNotFoundException(
                        String.format(exceptionMessage, idWhichNotExist)));


        // then
        Exception exception = assertThrows(javax.persistence.EntityNotFoundException.class, () -> customerService.getById(idWhichNotExist));

        assertEquals(
                String.format(exceptionMessage, idWhichNotExist),
                exception.getMessage());
    }


    private CustomerEntity createCustomerEntity() {
        return CustomerEntity
                .builder()
                .id(1L)
                .username("test name")
                .password("test password")
                .email("test@email.test")
                .isActive(true)
                .isCompany(true)
                .build();
    }
}