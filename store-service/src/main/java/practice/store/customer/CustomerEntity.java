package practice.store.customer;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "customers")
public class CustomerEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "customer_id", unique = true, nullable = false)
    private Long id;

    @NotBlank(message = "'Username' parameter may not be blank.")
    @Column
    private String username;

    @NotNull(message = "'Password' parameter may not be null.")
    @Column
    private String password;

    @NotNull(message = "'Email' parameter may not be null.")
    @Column(unique = true)
    @Email
    private String email;

    @NotNull(message = "'IsActive' parameter may not be null.")
    @Column
    private boolean isActive;

    @NotNull(message = "'IsCompany' parameter may not be null.")
    @Column
    private boolean isCompany;

    @NotNull(message = "'Postal code' parameter may not be null.")
    @Column
    private String postalCode;

    @NotNull(message = "'Street' parameter may not be null.")
    @Column
    private String street;

    @NotNull(message = "'City' parameter may not be null.")
    @Column
    private String city;
}
