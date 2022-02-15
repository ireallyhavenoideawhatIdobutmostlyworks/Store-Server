package practice.store.utils.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import practice.store.customer.CustomerEntity;
import practice.store.customer.CustomerPayload;
import practice.store.product.Availability;
import practice.store.product.Categories;
import practice.store.product.ProductEntity;
import practice.store.product.ProductPayload;
import testdata.entity.TestDataCustomerEntity;
import testdata.entity.TestDataProductEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Test converter object from entity to payload")
@ExtendWith(MockitoExtension.class)
@Tag("Unit_Test")
class EntitiesConverterTest {

    private EntitiesConverter entitiesConverter;


    @BeforeEach
    void setUp() {
        entitiesConverter = new EntitiesConverter();
    }


    @DisplayName("Return converted customer payload from entity")
    @Test
    void should_convert_customer_entity_to_payload_test() {
        // given
        CustomerEntity customerEntity = TestDataCustomerEntity.Customer(
                1L,
                "test name",
                "test@email.store",
                true,
                true);


        // when
        CustomerPayload customerPayload = entitiesConverter.convertCustomer(customerEntity);


        // then
        assertThat(customerPayload)
                .usingRecursiveComparison()
                .isEqualTo(customerEntity);

        assertNull(customerPayload.getPassword());
    }

    @DisplayName("Return converted product payload from entity")
    @Test
    void should_convert_product_entity_to_payload_test() {
        // given
        ProductEntity productEntity = TestDataProductEntity.Product(
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
        ProductPayload productPayload = entitiesConverter.convertProduct(productEntity);


        // then
        assertThat(productPayload)
                .usingRecursiveComparison()
                .isEqualTo(productEntity);
    }
}