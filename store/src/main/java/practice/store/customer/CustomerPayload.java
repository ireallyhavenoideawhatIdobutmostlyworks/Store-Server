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
    private Boolean isActive;
    private boolean isCompany;
}
