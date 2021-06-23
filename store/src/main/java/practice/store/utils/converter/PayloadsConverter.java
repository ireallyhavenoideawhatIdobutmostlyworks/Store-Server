package practice.store.utils.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import practice.store.config.security.SecurityConfig;
import practice.store.customer.CustomerEntity;
import practice.store.customer.CustomerPayload;

@RequiredArgsConstructor
@Component
public class PayloadsConverter {

    private final SecurityConfig securityConfig;

    public CustomerEntity convertCustomer(CustomerPayload customerPayload) {
        return CustomerEntity.builder()
                .id(customerPayload.getId())
                .username(customerPayload.getUsername())
                .password(securityConfig.encoder().encode(customerPayload.getPassword()))
                .email(customerPayload.getEmail())
                .isActive(customerPayload.isActive())
                .isCompany(customerPayload.isCompany())
                .build();
    }
}
