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
        double basePrice = 100.11;
        int discountPercentage = 20;
        double finalPrice = calculateFinalPriceAlgorithm(basePrice, discountPercentage);


        // when
        double finalPriceCalculate = calculatePriceProduct.calculateFinalPrice(basePrice, discountPercentage);


        // then
        assertThat(finalPrice).isEqualTo(finalPriceCalculate);
    }

    @DisplayName("Calculate final price for product without discount")
    @Test
    void should_calculate_final_price_product_without_discount() {
        // given
        double basePrice = 100;
        int discountPercentage = 0;
        double finalPrice = calculateFinalPriceAlgorithm(basePrice, discountPercentage);


        // when
        double finalPriceCalculate = calculatePriceProduct.calculateFinalPrice(basePrice, discountPercentage);


        // then
        assertThat(finalPrice).isEqualTo(finalPriceCalculate);
    }


    private double calculateFinalPriceAlgorithm(double basePrice, int discountPercentage) {
        return BigDecimal
                .valueOf(basePrice - discountPercentage)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}