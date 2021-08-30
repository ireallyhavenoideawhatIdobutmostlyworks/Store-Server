package practice.store.product;

import lombok.*;
import practice.store.order.details.OrderProductEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
//@EqualsAndHashCode
@Entity
@Getter
@Setter
@Table(name = "products")
public class ProductEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "product_id", unique = true, nullable = false)
    private Long id;

    @NotBlank(message = "'Name' parameter may not be blank")
    @Column
    private String name;

    @NotNull(message = "'Product UUID' parameter may not be empty")
    @Column
    private String productUUID;

    @NotBlank(message = "'Description' parameter may not be blank")
    @Column
    private String description;

    @NotNull(message = "'Base price' parameter may not be null")
    @Column
    private BigDecimal basePrice;

    @NotNull(message = "'Amount of price reduction' parameter may not be null")
    @Column
    private BigDecimal amountPriceReduction;

    @NotNull(message = "'Final price' parameter may not be null")
    @Column
    private BigDecimal finalPrice;

    @NotNull(message = "'Discount percentage' parameter may not be null")
    @Column
    private int discountPercentage;

    @NotNull(message = "'Has discount' parameter may not be null")
    @Column
    private boolean hasDiscount;

    @NotNull(message = "'Amount' parameter may not be null")
    @Column
    private int amount;

    @NotNull(message = "'Categories' parameter may not be null")
    @Enumerated(EnumType.STRING)
    private Categories categories;

    @NotNull(message = "'Availability' parameter may not be null")
    @Enumerated(EnumType.STRING)
    private Availability availability;

    @NotNull(message = "'Is active' parameter may not be null")
    @Column
    private boolean isActive;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, optional = false)
    private OrderProductEntity orderProduct;
}
