package testdata.payload;

import practice.store.customer.CustomerPayload;

import java.util.UUID;

public abstract class TestDataCustomerPayload {

    public static CustomerPayload Customer() {
        return CustomerPayload
                .builder()
                .username("name")
                .password("password")
                .email("customer@email.test" + UUID.randomUUID())
                .isActive(true)
                .isCompany(true)
                .postalCode("Postal Code")
                .street("Street")
                .city("City")
                .build();
    }

    public static CustomerPayload Customer(String email) {
        return CustomerPayload
                .builder()
                .username("name")
                .password("password")
                .email(email)
                .isActive(true)
                .isCompany(true)
                .postalCode("Postal Code")
                .street("Street")
                .city("City")
                .build();
    }

    public static CustomerPayload Customer(String username, String password, String email, boolean isActive, boolean isCompany) {
        return CustomerPayload
                .builder()
                .username(username)
                .password(password)
                .email(email)
                .isActive(isActive)
                .isCompany(isCompany)
                .postalCode("Postal Code")
                .street("Street")
                .city("City")
                .build();
    }
}
