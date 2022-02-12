//package testdata.payload;
//
//import practice.store.customer.CustomerPayload;
//
//public abstract class TestDataCustomerPayload {
//
//    public static CustomerPayload Customer() {
//        return CustomerPayload
//                .builder()
//                .id(1L)
//                .username("name")
//                .password("password")
//                .email("customer@email.test")
//                .isActive(true)
//                .isCompany(true)
//                .build();
//    }
//
//    public static CustomerPayload Customer(String email) {
//        return CustomerPayload
//                .builder()
//                .id(1L)
//                .username("name")
//                .password("password")
//                .email(email)
//                .isActive(true)
//                .isCompany(true)
//                .build();
//    }
//
//    public static CustomerPayload Customer(long id, String email) {
//        return CustomerPayload
//                .builder()
//                .id(id)
//                .username("name")
//                .password("password")
//                .email(email)
//                .isActive(true)
//                .isCompany(true)
//                .build();
//    }
//
//    public static CustomerPayload Customer(long id, String username, String password, String email, boolean isActive, boolean isCompany) {
//        return CustomerPayload
//                .builder()
//                .id(id)
//                .username(username)
//                .password(password)
//                .email(email)
//                .isActive(isActive)
//                .isCompany(isCompany)
//                .build();
//    }
//}
