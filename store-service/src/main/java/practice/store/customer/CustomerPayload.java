package practice.store.customer;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public final class CustomerPayload {

    private final String username;
    private final String password;
    private final String email;
    private final boolean isActive;
    private final boolean isCompany;
    private final String postalCode;
    private final String street;
    private final String city;
}
