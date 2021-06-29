package practice.store.junit.utils.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import practice.store.customer.CustomerEntity;
import practice.store.customer.CustomerPayload;
import practice.store.junit.DataFactor;
import practice.store.utils.converter.PayloadsConverter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test converter object from payload to entity")
class PayloadsConverterTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    private final String PASSWORD = "test password";


    @DisplayName("Return converted entity from payload")
    @Test
    void should_convert_payload_to_entity_test() {
        // given
        PayloadsConverter payloadsConverter = new PayloadsConverter(passwordEncoder);
        CustomerPayload customerPayload = DataFactor.createCustomerPayload(
                1L,
                "test name",
                "test password",
                "test@email.store",
                true,
                true);

        String passwordReturned = "returned test password";
        when(passwordEncoder.encode(PASSWORD)).thenReturn(passwordReturned);


        // when
        CustomerEntity customerEntity = payloadsConverter.convertCustomer(customerPayload);


        // then
        assertThat(customerPayload)
                .usingRecursiveComparison()
                .ignoringFields("password")
                .isEqualTo(customerEntity);

        assertEquals(
                customerEntity.getPassword(),
                passwordReturned);
    }
}