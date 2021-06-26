package practice.store.utils.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import practice.store.customer.CustomerEntity;
import practice.store.customer.CustomerPayload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PayloadsConverterTest {

    private PayloadsConverter payloadsConverter;

    @Mock
    private PasswordEncoder passwordEncoder;

    private final String PASSWORD = "test password";


    @BeforeEach
    void setUp() {
        payloadsConverter = new PayloadsConverter(passwordEncoder);
    }

    @DisplayName("Convert payload object to entity object and return it")
    @Test
    void testShouldConvertPayloadObjectToEntityObject() {
        // given
        String PASSWORD_RETURNED = "returned test password";
        CustomerPayload customerPayload = createCustomerPayload();

        // when
        when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD_RETURNED);
        CustomerEntity customerEntity = payloadsConverter.convertCustomer(customerPayload);

        // then
        assertThat(customerPayload)
                .usingRecursiveComparison()
                .ignoringFields("password")
                .isEqualTo(customerEntity);

        assertThat(customerEntity.getPassword()).isEqualTo(PASSWORD_RETURNED);
    }


    private CustomerPayload createCustomerPayload() {
        return CustomerPayload
                .builder()
                .id(1L)
                .username("test name")
                .password(PASSWORD)
                .email("test@email.test")
                .isActive(true)
                .isCompany(true)
                .build();
    }
}