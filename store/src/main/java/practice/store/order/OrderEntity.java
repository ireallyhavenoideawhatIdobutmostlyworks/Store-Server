package practice.store.order;

import lombok.*;
import practice.store.customer.CustomerEntity;
import practice.store.order.details.OrderProductEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "orders")
public class OrderEntity {

    /*
    ToDo add Date
     */

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
    private PaymentType paymentType;

    @NotNull(message = "'Shipment status' parameter may not be null")
    @Enumerated(EnumType.STRING)
    private ShipmentStatus shipmentStatus;

    @NotNull(message = "'Order status' parameter may not be null")
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @NotNull(message = "'Order price' parameter may not be null")
    @Column
    private BigDecimal orderPrice;

    @JoinColumn(name = "customer_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CustomerEntity customer;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "order_map_product_details",
            joinColumns = @JoinColumn(name = "order_id", nullable = false, updatable = false),
            inverseJoinColumns = @JoinColumn(name = "order_product_details_id", nullable = false, updatable = false)
    )
    private Set<OrderProductEntity> orderProduct = new HashSet<>();
}
