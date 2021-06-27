package practice.store.utils.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import practice.store.customer.CustomerEntity;
import practice.store.customer.CustomerPayload;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test converter object from entity to payload")
class EntitiesConverterTest {


    @DisplayName("Return converted payload from entity")
    @Test
    void should_convert_entity_to_payload_test() {
        // given
        EntitiesConverter entitiesConverter = new EntitiesConverter();
        CustomerEntity customerEntity = createCustomerEntity();


        // when
        CustomerPayload customerPayload = entitiesConverter.convertCustomer(customerEntity);


        // then
        assertThat(customerPayload)
                .usingRecursiveComparison()
                .isEqualTo(customerEntity);
    }


    private CustomerEntity createCustomerEntity() {
        return CustomerEntity
                .builder()
                .id(1L)
                .username("test name")
                .email("test@email.test")
                .isActive(true)
                .isCompany(true)
                .build();
    }
}