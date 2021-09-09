package practice.store.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import practice.store.customer.CustomerEntity;
import practice.store.order.details.OrderProductEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Getter
@Setter
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", unique = true, nullable = false)
    private Long id;

    @NotNull(message = "'Order UUID' parameter may not be empty")
    @Column
    private String orderUUID;

    @Column
    private String accountNumber;

    @NotNull(message = "'Is paid' parameter may not be null")
    @Column
    private Boolean isPaid;

    @Enumerated(EnumType.STRING)
    private PaymentTypeEnum paymentTypeEnum;

    @NotNull(message = "'Shipment status' parameter may not be null")
    @Enumerated(EnumType.STRING)
    private ShipmentStatusEnum shipmentStatusEnum;

    @NotNull(message = "'Order status' parameter may not be null")
    @Enumerated(EnumType.STRING)
    private OrderStatusEnum orderStatusEnum;

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

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date creationDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private Set<OrderProductEntity> orderProduct = new HashSet<>();
}
