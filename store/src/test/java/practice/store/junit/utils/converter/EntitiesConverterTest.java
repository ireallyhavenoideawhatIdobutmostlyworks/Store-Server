package practice.store.junit.utils.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import practice.store.customer.CustomerEntity;
import practice.store.customer.CustomerPayload;
import practice.store.junit.DataFactory;
import practice.store.utils.converter.EntitiesConverter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test converter object from entity to payload")
class EntitiesConverterTest {


    @DisplayName("Return converted payload from entity")
    @Test
    void should_convert_entity_to_payload_test() {
        // given
        EntitiesConverter entitiesConverter = new EntitiesConverter();
        CustomerEntity customerEntity = DataFactory.createCustomerEntity(
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
}