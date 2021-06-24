package practice.store.utils.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import practice.store.customer.CustomerEntity;
import practice.store.customer.CustomerPayload;

@RequiredArgsConstructor
@Component
public class PayloadsConverter {

    private final PasswordEncoder passwordEncoder;


    public CustomerEntity convertCustomer(CustomerPayload customerPayload) {
        return CustomerEntity.builder()
                .id(customerPayload.getId())
                .username(customerPayload.getUsername())
                .password(passwordEncoder.encode(customerPayload.getPassword()))
                .email(customerPayload.getEmail())
                .isActive(customerPayload.isActive())
                .isCompany(customerPayload.isCompany())
                .build();
    }
}
