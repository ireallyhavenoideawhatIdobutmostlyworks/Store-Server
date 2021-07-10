package practice.store.unit.utils.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import practice.store.DataFactory;
import practice.store.customer.CustomerEntity;
import practice.store.customer.CustomerPayload;
import practice.store.utils.converter.PayloadsConverter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test converter object from payload to entity")
class PayloadsConverterUnitTest {

    @Mock
    private PasswordEncoder passwordEncoder;


    @DisplayName("Return converted entity from payload")
    @Test
    void should_convert_payload_to_entity_test() {
        // given
        PayloadsConverter payloadsConverter = new PayloadsConverter(passwordEncoder);
        CustomerPayload customerPayload = DataFactory.createCustomerPayload(
                1L,
                "test name",
                "test password",
                "test@email.store",
                true,
                true);

        String password = "test password";
        String encodedPassword = "encoded test password";
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);


        // when
        CustomerEntity customerEntity = payloadsConverter.convertCustomer(customerPayload);


        // then
        assertThat(customerPayload)
                .usingRecursiveComparison()
                .ignoringFields("password")
                .isEqualTo(customerEntity);

        assertEquals(customerEntity.getPassword(), encodedPassword);
    }
}