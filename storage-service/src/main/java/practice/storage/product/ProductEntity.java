package practice.storage.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import practice.store.order.details.OrderProductEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
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

    @NotNull(message = "'Amount of price reduction' parameter may not be null")  @JsonFormat(shape=JsonFormat.Shape.STRING)
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

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<OrderProductEntity> orderProduct = new HashSet<>();
}
