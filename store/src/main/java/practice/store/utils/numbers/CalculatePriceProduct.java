package practice.store.utils.numbers;

import org.springframework.stereotype.Component;

@Component
public class CalculatePriceProduct {

    public double calculateFinalPrice(double basePrice, int discountPercentage) {
        return (basePrice * (100 - discountPercentage) / 100);
    }
}
