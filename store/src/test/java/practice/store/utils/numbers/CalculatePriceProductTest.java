package practice.store.utils.numbers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Calculate product price")
class CalculatePriceProductTest {

    private CalculatePriceProduct calculatePriceProduct;


    @BeforeEach
    void setUp() {
        calculatePriceProduct = new CalculatePriceProduct();
    }


    @DisplayName("Calculate final price for product with discount")
    @Test
    void should_calculate_final_price_product_with_discount() {
        // given
        BigDecimal basePrice = BigDecimal.valueOf(100.11);
        int discountPercentage = 20;
        BigDecimal finalPrice = calculateFinalPriceAlgorithm(basePrice, discountPercentage);


        // when
        BigDecimal finalPriceCalculate = calculatePriceProduct.calculateFinalPrice(basePrice, discountPercentage);


        // then
        assertThat(finalPrice).isEqualTo(finalPriceCalculate);
    }

    @DisplayName("Calculate final price for product without discount")
    @Test
    void should_calculate_final_price_product_without_discount() {
        // given
        BigDecimal basePrice = BigDecimal.valueOf(100);
        int discountPercentage = 0;
        BigDecimal finalPrice = calculateFinalPriceAlgorithm(basePrice, discountPercentage);


        // when
        BigDecimal finalPriceCalculate = calculatePriceProduct.calculateFinalPrice(basePrice, discountPercentage);


        // then
        assertThat(finalPrice).isEqualTo(finalPriceCalculate);
    }


    private BigDecimal calculateFinalPriceAlgorithm(BigDecimal basePrice, int discountPercentage) {
        BigDecimal finalPrice = basePrice.subtract(BigDecimal.valueOf(discountPercentage));
        return finalPrice.setScale(2, RoundingMode.HALF_UP);
    }
}