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
        BigDecimal basePrice = BigDecimal.valueOf(123);
        int discountPercentage = 20;
        BigDecimal finalPrice = BigDecimal.valueOf(98.40).setScale(2, RoundingMode.HALF_UP);


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
        BigDecimal finalPrice = BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP);


        // when
        BigDecimal finalPriceCalculate = calculatePriceProduct.calculateFinalPrice(basePrice, discountPercentage);


        // then
        assertThat(finalPrice).isEqualTo(finalPriceCalculate);
    }
}