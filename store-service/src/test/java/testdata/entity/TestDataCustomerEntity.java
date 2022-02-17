package testdata.entity;

import practice.store.customer.CustomerEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class TestDataCustomerEntity {

    public static CustomerEntity Customer(String email) {
        return CustomerEntity
                .builder()
                .id(1L)
                .username("Name")
                .password("Password")
                .email(email)
                .isActive(true)
                .isCompany(true)
                .postalCode("Postal Code")
                .street("Street")
                .city("City")
                .build();
    }

    public static CustomerEntity Customer(long id, String username, String email, boolean isActive, boolean isCompany) {
        return CustomerEntity
                .builder()
                .id(id)
                .username(username)
                .email(email)
                .isActive(isActive)
                .isCompany(isCompany)
                .postalCode("Postal Code")
                .street("Street")
                .city("City")
                .build();
    }

    public static CustomerEntity Customer(long id, String username, String password, String email, boolean isActive, boolean isCompany) {
        return CustomerEntity
                .builder()
                .id(id)
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

    public static List<CustomerEntity> CustomersList() {
        CustomerEntity existingCustomerEntityFirst = CustomerEntity
                .builder()
                .id(1L)
                .username("test name1")
                .password("test password1")
                .email("test1@email1.test1"  + UUID.randomUUID())
                .isActive(true)
                .isCompany(true)
                .postalCode("Postal Code")
                .street("Street")
                .city("City")
                .build();

        CustomerEntity existingCustomerEntitySecond = CustomerEntity
                .builder()
                .id(2L)
                .username("test name2")
                .password("test password2")
                .email("test2@email2.test2"  + UUID.randomUUID())
                .isActive(true)
                .isCompany(true)
                .postalCode("Postal Code")
                .street("Street")
                .city("City")
                .build();


        CustomerEntity existingCustomerEntityThird = CustomerEntity
                .builder()
                .id(3L)
                .username("test name3")
                .password("test password3")
                .email("test3@email3.test3"  + UUID.randomUUID())
                .isActive(true)
                .isCompany(true)
                .postalCode("Postal Code")
                .street("Street")
                .city("City")
                .build();

        return Arrays.asList(existingCustomerEntityFirst, existingCustomerEntitySecond, existingCustomerEntityThird);
    }
}
