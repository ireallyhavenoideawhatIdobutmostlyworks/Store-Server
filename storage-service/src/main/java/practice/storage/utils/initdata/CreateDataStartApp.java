package practice.storage.utils.initdata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import practice.storage.product.Availability;
import practice.storage.product.Categories;
import practice.storage.product.ProductEntity;
import practice.storage.utils.price.CalculatePrice;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Component
@Getter
public class CreateDataStartApp {

    private final CalculatePrice calculateFinalPrice;

    private ProductEntity productFirst, productSecond, productThird, productFourth, productFifth, productSixth;
    private Set<ProductEntity> products;


    public void createProducts() {
        productFirst = createProductFirst();
        productSecond = createProductSecond();
        productThird = createProductThird();
        productFourth = createProductFourth();
        productFifth = createProductFifth();
        productSixth = createProductSixth();
        products = new HashSet<>(Arrays.asList(productFirst, productSecond, productThird));
    }


    private ProductEntity createProductFirst() {
        BigDecimal basePrice = BigDecimal.valueOf(500);
        int discount = 15;
        BigDecimal finalPrice = calculateFinalPrice.calculateFinalPrice(basePrice, discount);

        productFirst = ProductEntity.builder()
                .id(1L)
                .name("Nexus 6")
                .productUUID("UUID1")
                .description("Description product 1")
                .categories(Categories.PHONES)
                .basePrice(basePrice)
                .finalPrice(finalPrice)
                .amountPriceReduction(basePrice.subtract(finalPrice))
                .discountPercentage(discount)
                .amount(100)
                .hasDiscount(true)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();
        return productFirst;
    }

    private ProductEntity createProductSecond() {
        BigDecimal basePrice = BigDecimal.valueOf(1500);
        int discount = 10;
        BigDecimal finalPrice = calculateFinalPrice.calculateFinalPrice(basePrice, discount);

        productSecond = ProductEntity.builder()
                .id(2L)
                .name("Dell")
                .productUUID("UUID2")
                .description("Description product 2")
                .categories(Categories.LAPTOP)
                .availability(Availability.AVAILABLE)
                .basePrice(basePrice)
                .finalPrice(finalPrice)
                .amountPriceReduction(basePrice.subtract(finalPrice))
                .discountPercentage(discount)
                .amount(900)
                .hasDiscount(true)
                .isActive(true)
                .build();
        return productSecond;
    }

    private ProductEntity createProductThird() {
        productThird = ProductEntity.builder()
                .id(3L)
                .name("PANASONIC")
                .productUUID("UUID3")
                .description("Description product 3")
                .categories(Categories.MONITOR)
                .availability(Availability.AVAILABLE)
                .basePrice(BigDecimal.valueOf(2000D))
                .finalPrice(BigDecimal.valueOf(2000D))
                .amountPriceReduction(BigDecimal.valueOf(2000D))
                .discountPercentage(0)
                .amount(100)
                .hasDiscount(false)
                .isActive(true)
                .build();
        return productThird;
    }

    private ProductEntity createProductFourth() {
        productFourth = ProductEntity.builder()
                .id(4L)
                .name("XIAOMI")
                .productUUID("UUID4")
                .description("Description product 4")
                .categories(Categories.MONITOR)
                .availability(Availability.AVAILABLE)
                .basePrice(BigDecimal.valueOf(2000D))
                .finalPrice(BigDecimal.valueOf(2000D))
                .amountPriceReduction(BigDecimal.valueOf(2000D))
                .discountPercentage(0)
                .amount(100)
                .hasDiscount(false)
                .isActive(false)
                .build();
        return productFourth;
    }

    private ProductEntity createProductFifth() {
        productFifth = ProductEntity.builder()
                .id(5L)
                .name("APPLE")
                .productUUID("UUID5")
                .description("Description product 5")
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .basePrice(BigDecimal.valueOf(2000D))
                .finalPrice(BigDecimal.valueOf(2000D))
                .amountPriceReduction(BigDecimal.valueOf(2000D))
                .discountPercentage(0)
                .amount(100)
                .hasDiscount(false)
                .isActive(true)
                .build();
        return productFifth;
    }

    private ProductEntity createProductSixth() {
        productSixth = ProductEntity.builder()
                .id(6L)
                .name("SAMSUNG")
                .productUUID("UUID6")
                .description("Description product 6")
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .basePrice(BigDecimal.valueOf(2000D))
                .finalPrice(BigDecimal.valueOf(2000D))
                .amountPriceReduction(BigDecimal.valueOf(2000D))
                .discountPercentage(0)
                .amount(100)
                .hasDiscount(false)
                .isActive(true)
                .build();
        return productSixth;
    }
}
