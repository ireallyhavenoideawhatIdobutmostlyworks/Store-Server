package practice.bank.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "payment")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Order UUID may not be empty")
    @Column
    private String orderUUID;

    @NotNull(message = "Payment UUID may not be empty")
    @Column
    private String paymentUUID;

    @NotNull(message = "Account number may not be empty")
    @Column
    private String accountNumber;

    @NotNull(message = "'Email' parameter may not be null.")
    @Column
    @Email
    private String email;

    @NotNull(message = "Order price may not be null")
    @Column
    private BigDecimal orderPrice;

    @NotNull(message = "IsPaymentSuccess may not be null")
    @Column
    private Boolean isPaymentSuccess;

    @NotNull(message = "Processing date may not be null")
    @Column
    private LocalDateTime processingDate;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
}
