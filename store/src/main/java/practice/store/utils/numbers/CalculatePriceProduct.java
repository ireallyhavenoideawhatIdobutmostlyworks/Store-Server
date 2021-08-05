package practice.store.utils.numbers;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CalculatePriceProduct {

    public double calculateFinalPrice(double basePrice, int discountPercentage) {
        return BigDecimal
                .valueOf(basePrice - discountPercentage)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
