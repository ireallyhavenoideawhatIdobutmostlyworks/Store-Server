package practice.store.order.details;

import lombok.*;
import practice.store.order.OrderEntity;
import practice.store.product.ProductEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "order_product_details")
public class OrderProductEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "order_product_id", unique = true, nullable = false)
    private Long id;

    @NotNull(message = "'Amount' parameter may not be empty")
    @Column
    private int amount;

    @NotNull(message = "'Unit price' parameter may not be null")
    @Column
    private BigDecimal unitPrice;

    @NotNull(message = "'Collection price' parameter may not be null")
    @Column
    private BigDecimal collectionPrice;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_id")
    private ProductEntity product;
}
