package practice.store.product;

import lombok.*;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Data
@Table(name = "products")
@SelectBeforeUpdate
public class ProductEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
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
    private double basePrice;

    @NotNull(message = "'Amount of price reduction' parameter may not be null")
    @Column
    private double amountPriceReduction;

    @NotNull(message = "'Final price' parameter may not be null")
    @Column
    private double finalPrice;

    @NotNull(message = "'Discount percentage' parameter may not be null")
    @Column
    private int discountPercentage;

    @NotNull(message = "'Has discount' parameter may not be null")
    @Column
    private boolean hasDiscount;

    @NotNull(message = "'Amount in stock' parameter may not be null")
    @Column
    private int amountInStock;

    @NotNull(message = "'Categories' parameter may not be null")
    @Enumerated(EnumType.STRING)
    private Categories categories;

    @NotNull(message = "'Availability' parameter may not be null")
    @Enumerated(EnumType.STRING)
    private Availability availability;

    @NotNull(message = "'Is active' parameter may not be null")
    @Column
    private boolean isActive;
}
