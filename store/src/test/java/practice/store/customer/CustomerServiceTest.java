package practice.store.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import practice.store.exceptions.customer.CustomerEmailExistException;
import practice.store.exceptions.customer.CustomerEmailWithIdIncorrectException;
import testdata.DataFactoryCustomer;
import practice.store.utils.converter.EntitiesConverter;
import practice.store.utils.converter.PayloadsConverter;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for customer service")
class CustomerServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CustomerRepository customerRepository;
    private CustomerService customerService;
    private CustomerEntity customerEntity;
    private CustomerPayload customerPayload;

    private EntitiesConverter entitiesConverter;
    private PayloadsConverter payloadsConverter;


    @BeforeEach
    void setUp() {
        entitiesConverter = new EntitiesConverter();
        payloadsConverter = new PayloadsConverter(passwordEncoder);

        customerService = new CustomerService(customerRepository, entitiesConverter, payloadsConverter);

        customerEntity = DataFactoryCustomer.createCustomerEntity(1L, "test name", "test password", "test@email.store", true, true);
        customerPayload = DataFactoryCustomer.createCustomerPayload(1L, "test name", "test password", "test@email.store", true, true);
    }

    @DisplayName("Return customer by ID")
    @Test
    void should_return_customer_when_id_is_exist_test() {
        // given
        long id = customerEntity.getId();

        when(customerRepository.getById(id)).thenReturn(customerEntity);
        CustomerPayload customerPayload = entitiesConverter.convertCustomer(customerEntity);


        // when
        CustomerPayload customerPayloadReturnedFromService = customerService.getById(customerPayload.getId());


        // then
        assertEquals(
                customerPayload,
                customerPayloadReturnedFromService);

        verify(customerRepository, times(1)).getById(id);
    }

    @DisplayName("Throw exception when ID is not exist")
    @Test
    void should_throw_exception_when_id_is_not_exist_test() {
        // given
        String exceptionMessage = "Unable to find practice.store.customer.CustomerEntity with id %d";
        long idNotExist = 11L;

        when(customerRepository.getById(idNotExist))
                .thenThrow(new EntityNotFoundException(String.format(exceptionMessage, idNotExist)));


        // when
        Throwable exception = catchThrowable(() -> customerService.getById(idNotExist));


        // then
        assertThat(exception)
                .isInstanceOf(javax.persistence.EntityNotFoundException.class)
                .hasMessageContaining(String.format(exceptionMessage, idNotExist));

        verify(customerRepository, times(1)).getById(idNotExist);
    }

    @DisplayName("Return not empty customers list")
    @Test
    void should_return_customers_list_when_list_is_not_empty_test() {
        // given
        List<CustomerEntity> customerEntityList = Collections.singletonList(customerEntity);
        when(customerRepository.findAll()).thenReturn(customerEntityList);
        List<CustomerPayload> customerPayloadList = Collections.singletonList(entitiesConverter.convertCustomer(customerEntity));


        // when
        List<CustomerPayload> customerPayloadListReturnedFromService = customerService.getList();


        // then
        assertThat(customerPayloadList)
                .usingRecursiveComparison()
                .isEqualTo(customerPayloadListReturnedFromService);

        verify(customerRepository, times(1)).findAll();
    }

    @DisplayName("Return empty customers list")
    @Test
    void should_return_empty_customers_list_when_list_is_empty_test() {
        // given
        List<CustomerEntity> emptyCustomerEntityList = new ArrayList<>();
        when(customerRepository.findAll()).thenReturn(emptyCustomerEntityList);


        // when
        List<CustomerPayload> customerPayloadListReturnedFromService = customerService.getList();


        // then
        assertTrue(customerPayloadListReturnedFromService.isEmpty());

        verify(customerRepository, times(1)).findAll();
    }

    @DisplayName("Add customer when data is correct")
    @Test
    void should_add_customer_when_payload_is_correct_test() {
        // given
        when(customerRepository.existsByEmail(customerPayload.getEmail())).thenReturn(false);
        customerPayload.setId(null);
        CustomerEntity customerEntity = payloadsConverter.convertCustomer(customerPayload);


        // when
        customerService.save(customerPayload);


        // then
        verify(customerRepository, times(1)).save(customerEntity);
        verify(customerRepository, times(1)).existsByEmail(customerPayload.getEmail());
    }

    @DisplayName("Throw exception when email is exist during save")
    @Test
    void should_throw_exception_when_email_is_exist_during_save_test() {
        // given
        String emailIsExist = customerEntity.getEmail();
        String exceptionMessage = "Customer with email:%s is exist.";

        when(customerRepository.existsByEmail(emailIsExist)).thenReturn(true);


        // when
        Throwable exception = catchThrowable(() -> customerService.save(customerPayload));


        // then
        assertThat(exception)
                .isInstanceOf(CustomerEmailExistException.class)
                .hasMessage(String.format(exceptionMessage, emailIsExist));

        verify(customerRepository, times(1)).existsByEmail(emailIsExist);
        verify(customerRepository, times(0)).save(customerEntity);
    }

    @DisplayName("Edit customer when data is correct")
    @Test
    void should_edit_customer_when_payload_is_correct_test() {
        // given
        String emailExist = customerPayload.getEmail();
        long idExist = customerPayload.getId();

        when(customerRepository.existsByEmailAndId(emailExist, idExist)).thenReturn(true);
        CustomerEntity existingCustomer = payloadsConverter.convertCustomer(customerPayload);


        // when
        customerService.edit(customerPayload, idExist);


        // then
        verify(customerRepository, times(1)).existsByEmailAndId(emailExist, idExist);
        verify(customerRepository, times(1)).save(existingCustomer);
    }

    @DisplayName("Throw exception when email and id not belong to same customer during edit")
    @Test
    void should_throw_exception_when_email_and_id_not_belong_to_same_customer_test() {
        // given
        String emailExist = customerEntity.getEmail();
        long idNotExist = 11L;
        String exceptionMessage = "Email:%s and id:%d not belong to same customer.";


        // when
        Throwable exception = catchThrowable(() -> customerService.edit(customerPayload, idNotExist));


        // then
        assertThat(exception)
                .isInstanceOf(CustomerEmailWithIdIncorrectException.class)
                .hasMessage(String.format(exceptionMessage, emailExist, idNotExist));

        verify(customerRepository, times(1)).existsByEmailAndId(emailExist, idNotExist);
        verify(customerRepository, times(0)).save(customerEntity);
    }

    @DisplayName("Set isActive field to false during delete")
    @Test
    void should_set_is_active_field_to_false_test() {
        // given
        when(customerRepository.getById(customerEntity.getId())).thenReturn(customerEntity);
        customerEntity.setActive(false);


        // when
        customerService.deleteCustomer(customerEntity.getId());


        // then
        verify(customerRepository, times(1)).getById(customerEntity.getId());
        verify(customerRepository, times(1)).save(customerEntity);
    }

    @DisplayName("Throw exception when id is not exist during delete")
    @Test
    void should_throw_exception_when_id_is_not_exist_during_delete_test() {
        // given
        String exceptionMessage = "Unable to find practice.store.customer.CustomerEntity with id %d";
        long idNotExist = 11L;

        when(customerRepository.getById(idNotExist))
                .thenThrow(new EntityNotFoundException(String.format(exceptionMessage, idNotExist)));


        // when
        Throwable exception = catchThrowable(() -> customerService.deleteCustomer(idNotExist));


        // then
        assertThat(exception)
                .isInstanceOf(javax.persistence.EntityNotFoundException.class)
                .hasMessage(String.format(exceptionMessage, idNotExist));

        verify(customerRepository, times(1)).getById(idNotExist);
        verify(customerRepository, times(0)).save(customerEntity);
    }
}