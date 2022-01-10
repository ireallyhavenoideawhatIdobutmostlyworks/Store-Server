package practice.storage.utils.price;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CalculatePrice {

    /**
     * This is a simple method to calculate product final price.
     * Simple example based on Integers:    (basePrice * (100 - discountPercentage) / 100)
     * Simple example based in BigDecimal:  basePrice.multiply(basicValue.subtract(percentage)).divide(basicValue)
     *
     * @return finalPrice with rounding
     */
    public BigDecimal calculateFinalPrice(BigDecimal basePrice, int discountPercentage) {
        BigDecimal basicValue = BigDecimal.valueOf(100);
        BigDecimal percentage = BigDecimal.valueOf(discountPercentage);

        BigDecimal finalPrice = basePrice
                .multiply(basicValue.subtract(percentage))
                .divide(basicValue);

        return finalPrice.setScale(2, RoundingMode.HALF_UP);
    }
}
