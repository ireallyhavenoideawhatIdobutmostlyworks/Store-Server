package practice.store;

import practice.store.customer.CustomerEntity;
import practice.store.customer.CustomerPayload;

import java.util.Arrays;
import java.util.List;

public class DataFactory {

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

    public static CustomerEntity createCustomerEntity() {
        return CustomerEntity
                .builder()
                .id(1L)
                .username("name")
                .password("password")
                .email("customer@email.test")
                .isActive(true)
                .isCompany(true)
                .build();
    }

    public static CustomerEntity createCustomerEntity(String email) {
        return CustomerEntity
                .builder()
                .id(1L)
                .username("name")
                .password("password")
                .email(email)
                .isActive(true)
                .isCompany(true)
                .build();
    }

    public static CustomerEntity createCustomerEntity(long id, String email) {
        return CustomerEntity
                .builder()
                .id(id)
                .username("name")
                .password("password")
                .email(email)
                .isActive(true)
                .isCompany(true)
                .build();
    }

    public static List<CustomerEntity> creteCustomerList() {
        CustomerEntity existingCustomerEntityFirst = DataFactory.createCustomerEntity(1L, "test name1", "test password1", "test@email.test1", true, true);
        CustomerEntity existingCustomerEntitySecond = DataFactory.createCustomerEntity(2L, "test name2", "test password2", "test@email.test2", true, true);
        CustomerEntity existingCustomerEntityThird = DataFactory.createCustomerEntity(3L, "test name3", "test password3", "test@email.test3", true, true);
        return Arrays.asList(existingCustomerEntityFirst, existingCustomerEntitySecond, existingCustomerEntityThird);
    }
}
