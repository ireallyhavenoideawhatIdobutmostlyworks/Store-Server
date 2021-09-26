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
import practice.store.product.Availability;
import practice.store.product.Categories;
import practice.store.product.ProductEntity;
import practice.store.product.ProductPayload;
import testdata.payload.TestDataCustomerPayload;
import testdata.payload.TestDataProductPayload;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test converter object from payload to entity")
class PayloadsConverterTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    private PayloadsConverter payloadsConverter;


    @BeforeEach
    void setUp() {
        payloadsConverter = new PayloadsConverter(passwordEncoder);
    }


    @DisplayName("Return converted customer entity from payload")
    @Test
    void should_convert_customer_payload_to_entity_test() {
        // given
        CustomerPayload customerPayload = TestDataCustomerPayload.Customer(
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

    @DisplayName("Return converted product entity from payload")
    @Test
    void should_convert_product_payload_to_entity_test() {
        // given
        ProductPayload productPayload = TestDataProductPayload.Product(
                1L,
                "test name",
                "test uuid",
                "test description",
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(90),
                10,
                true,
                5,
                Categories.PHONES,
                Availability.AVAILABLE,
                true
        );


        // when
        ProductEntity productEntity = payloadsConverter.convertProduct(productPayload);


        // then
        assertThat(productPayload)
                .usingRecursiveComparison()
                .isEqualTo(productEntity);
    }
}