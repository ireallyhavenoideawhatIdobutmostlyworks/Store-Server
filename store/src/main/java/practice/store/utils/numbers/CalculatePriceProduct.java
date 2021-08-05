package practice.store.utils.numbers;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CalculatePriceProduct {

    public BigDecimal calculateFinalPrice(BigDecimal basePrice, int discountPercentage) {
        BigDecimal finalPrice = basePrice.subtract(BigDecimal.valueOf(discountPercentage));
        return finalPrice.setScale(2, RoundingMode.HALF_UP);
    }
}
