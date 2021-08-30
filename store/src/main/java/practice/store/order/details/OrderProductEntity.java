package practice.store.order.details;

import lombok.*;
import practice.store.product.ProductEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;
}
