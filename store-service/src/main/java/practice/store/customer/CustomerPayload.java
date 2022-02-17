package practice.store.customer;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Builder
@Getter
public final class CustomerPayload {

    private final String username;
    private final String password;
    private final String email;
    private final Boolean isActive;
    private final Boolean isCompany;
    private final String postalCode;
    private final String street;
    private final String city;
}
