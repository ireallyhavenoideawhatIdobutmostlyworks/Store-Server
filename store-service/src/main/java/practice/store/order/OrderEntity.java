package practice.store.order;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import practice.store.customer.CustomerEntity;
import practice.store.order.details.OrderProductEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", unique = true, nullable = false)
    private Long id;

    @NotNull(message = "'Order UUID' parameter may not be empty")
    @Column
    private String orderUUID;

    @NotNull(message = "'Payload UUID' parameter may not be empty")
    @Column
    private String payloadUUID;

    @NotNull(message = "'Payment UUID' parameter may not be empty")
    @Column
    private String paymentUUID;

    @NotNull(message = "'Account number' parameter may not be empty")
    @Column
    private String accountNumber;

    @NotNull(message = "'Is paid' parameter may not be null")
    @Column
    private Boolean isPaid;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @NotNull(message = "'Shipment status' parameter may not be null")
    @Enumerated(EnumType.STRING)
    private ShipmentStatus shipmentStatus;

    @NotNull(message = "'Order status' parameter may not be null")
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @NotNull(message = "'Order base price' parameter may not be null")
    @Column
    private BigDecimal orderBasePrice;

    @NotNull(message = "'Order final price' parameter may not be null")
    @Column
    private BigDecimal orderFinalPrice;

    @NotNull(message = "'Has discount' parameter may not be null")
    @Column
    private boolean hasDiscount;

    @NotNull(message = "'Discount percentage' parameter may not be null")
    @Column
    private int discountPercentage;

    @NotNull(message = "'Is cancelled' parameter may not be null")
    @Column
    private boolean isCancelled;

    @Column
    private LocalDateTime creationDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<OrderProductEntity> orderProduct = new HashSet<>();
}
