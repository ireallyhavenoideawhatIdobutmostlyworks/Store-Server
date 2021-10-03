package practice.store.customer;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CustomerPayload {

    private Long id;
    private String username;
    private String password;
    private String email;
    private boolean isActive;
    private boolean isCompany;
    private String postalCode;
    private String street;
    private String city;
}
