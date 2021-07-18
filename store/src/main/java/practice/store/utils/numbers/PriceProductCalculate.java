package practice.store.utils.numbers;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PriceProductCalculate {

    private final GenerateRandomNumber generateRandomNumber;
    private final List<Integer> discounts;

    public PriceProductCalculate(GenerateRandomNumber generateRandomNumber) {
        this.generateRandomNumber = generateRandomNumber;

        discounts = new ArrayList<>();
        addDiscountLevelsToList(discounts);
    }

    public Integer getDiscountLevel() {
        return returnRandomValueFromList(discounts);
    }

    public double calculateFinalPrice(double basePrice, int discountPercentage) {
        return (basePrice * (100 - discountPercentage) / 100);
    }


    private void addDiscountLevelsToList(List<Integer> discounts) {
        for (int i = 5; i <= 95; i += 5) {
            discounts.add(i);
        }
    }

    private Integer returnRandomValueFromList(List<Integer> discounts) {
        return discounts.get(generateRandomNumber.generateRandomIndexFromListSize(discounts.size()));
    }
}
