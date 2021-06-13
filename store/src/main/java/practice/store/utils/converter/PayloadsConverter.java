package practice.store.utils.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import practice.store.config.security.PasswordEncoderConfig;
import practice.store.customer.CustomerEntity;
import practice.store.customer.CustomerPayload;

@RequiredArgsConstructor
@Component
public class PayloadsConverter {

    private final PasswordEncoderConfig passwordEncoderConfig;

    public CustomerEntity convertCustomer(CustomerPayload customerPayload) {
        return CustomerEntity.builder()
                .id(customerPayload.getId())
                .username(customerPayload.getUsername())
                .password(passwordEncoderConfig.encoder().encode(customerPayload.getPassword()))
                .email(customerPayload.getEmail())
                .isActive(customerPayload.getIsActive())
                .isCompany(customerPayload.isCompany())
                .build();
    }
}
