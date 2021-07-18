package practice.store.data.factory;

import practice.store.customer.CustomerEntity;
import practice.store.customer.CustomerPayload;

public class DataFactoryCustomer {

    public static CustomerEntity createCustomerEntity(long id, String username, String password, String email, boolean isActive, boolean isCompany) {
        return CustomerEntity
                .builder()
                .id(id)
                .username(username)
                .password(password)
                .email(email)
                .isActive(isActive)
                .isCompany(isCompany)
                .build();
    }

    public static CustomerEntity createCustomerEntity(long id, String username, String email, boolean isActive, boolean isCompany) {
        return CustomerEntity
                .builder()
                .id(id)
                .username(username)
                .email(email)
                .isActive(isActive)
                .isCompany(isCompany)
                .build();
    }

    public static CustomerPayload createCustomerPayload(long id, String username, String password, String email, boolean isActive, boolean isCompany) {
        return CustomerPayload
                .builder()
                .id(id)
                .username(username)
                .password(password)
                .email(email)
                .isActive(isActive)
                .isCompany(isCompany)
                .build();
    }
}
