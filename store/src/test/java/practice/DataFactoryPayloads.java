package practice;

import practice.store.customer.CustomerEntity;
import practice.store.customer.CustomerPayload;

import java.util.Arrays;
import java.util.List;

public class DataFactoryPayloads {

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

    public static CustomerPayload createCustomerPayload(long id, String username, String email, boolean isActive, boolean isCompany) {
        return CustomerPayload
                .builder()
                .id(id)
                .username(username)
                .email(email)
                .isActive(isActive)
                .isCompany(isCompany)
                .build();
    }

    public static CustomerPayload createCustomerPayload() {
        return CustomerPayload
                .builder()
                .id(1L)
                .username("name")
                .password("password")
                .email("customer@email.test")
                .isActive(true)
                .isCompany(true)
                .build();
    }

    public static CustomerPayload createCustomerPayload(String email) {
        return CustomerPayload
                .builder()
                .id(1L)
                .username("name")
                .password("password")
                .email(email)
                .isActive(true)
                .isCompany(true)
                .build();
    }

    public static CustomerPayload createCustomerPayload(long id, String email) {
        return CustomerPayload
                .builder()
                .id(id)
                .username("name")
                .password("password")
                .email(email)
                .isActive(true)
                .isCompany(true)
                .build();
    }

    public static List<CustomerPayload> creteCustomerList() {
        CustomerPayload existingCustomerEntityFirst = CustomerPayload
                .builder()
                .id(1L)
                .username("test name1")
                .password("test password1")
                .email("test@email.test1")
                .isActive(true)
                .isCompany(true)
                .build();

        CustomerPayload existingCustomerEntitySecond = CustomerPayload
                .builder()
                .id(2L)
                .username("test name2")
                .password("test password2")
                .email("test@email.test2")
                .isActive(true)
                .isCompany(true)
                .build();


        CustomerPayload existingCustomerEntityThird = CustomerPayload
                .builder()
                .id(3L)
                .username("test name3")
                .password("test password3")
                .email("test@email.test3")
                .isActive(true)
                .isCompany(true)
                .build();

        return Arrays.asList(existingCustomerEntityFirst, existingCustomerEntitySecond, existingCustomerEntityThird);
    }
}
