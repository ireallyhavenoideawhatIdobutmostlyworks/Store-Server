package practice.store.utils.converter;

import org.springframework.stereotype.Component;
import practice.store.customer.CustomerEntity;
import practice.store.customer.CustomerPayload;

@Component
public class EntitiesConverter {

    public CustomerPayload convertCustomer(CustomerEntity customerEntity) {
        return CustomerPayload.builder()
                .id(customerEntity.getId())
                .username(customerEntity.getUsername())
                .password(customerEntity.getPassword())
                .email(customerEntity.getEmail())
                .isActive(customerEntity.getIsActive())
                .isCompany(customerEntity.isCompany())
                .build();
    }
}
