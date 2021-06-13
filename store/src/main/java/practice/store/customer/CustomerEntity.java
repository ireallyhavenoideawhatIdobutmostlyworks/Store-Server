package practice.store.customer;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Getter
@Setter
@Table(name = "customers")
public class CustomerEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @NotBlank(message = "'First name' parameter may not be blank.")
    @Column
    private String username;

    @NotNull(message = "'Password' parameter may not be null.")
    @Column
    private String password;

    @NotNull(message = "'Email' parameter may not be null.")
    @Column
    @Email
    private String email;

    @NotNull(message = "'Is active' parameter may not be null.")
    @Column
    private Boolean isActive;

    @NotNull(message = "'Is company' parameter may not be null.")
    @Column
    private boolean isCompany;
}
