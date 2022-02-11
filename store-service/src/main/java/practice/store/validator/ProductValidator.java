package practice.store.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import practice.store.product.Availability;
import practice.store.product.ProductPayload;
import practice.store.product.ProductRepository;
import practice.store.utils.numbers.CalculatePrice;

import java.math.BigDecimal;

@PropertySource("classpath:productsDiscountValue.properties")
@RequiredArgsConstructor
@Transactional
@Component
@Log4j2
public class ProductValidator {

    private final CalculatePrice calculateFinalPrice;
    private final ProductRepository productRepository;
    private final LogHelper logHelper;

    @Value("${discount.percentage.max.value.higher}")
    private int DISCOUNT_PERCENTAGE_MAX;
    @Value("${discount.percentage.max.value.lower}")
    private int DISCOUNT_PERCENTAGE_MIN;


    public boolean isProductUuidExist(String uuid) {
        boolean result = productRepository.existsByProductUUID(uuid);
        return logHelper.logIfFalse(result, String.format("Product with UUID: %s is not exist", uuid));
    }

    public boolean isProductWithdrawFromSale(Availability availability) {
        boolean result = !(availability.equals(Availability.WITHDRAW_FROM_SALE));
        return logHelper.logIfFalse(result, "Product is withdraw from sale");
    }

    public boolean areFinalPriceAndBasePriceEquals(BigDecimal finalPrice, BigDecimal basePrice) {
        boolean result = finalPrice.compareTo(basePrice) == 0;
        return logHelper.logIfFalse(result, String.format("Final price: %.2f and base price: %.2f are not equal", finalPrice, basePrice));
    }

    public boolean areDiscountAndPriceCorrect(ProductPayload productPayload) {
        if (productPayload.isHasDiscount()) {
            return isDiscountPercentageInRangeCorrect(productPayload.getDiscountPercentage())
                    && isFinalPriceCorrect(productPayload.getBasePrice(), productPayload.getDiscountPercentage(), productPayload.getFinalPrice());
        } else {
            return isDiscountPercentageInRange(productPayload.getDiscountPercentage())
                    && areFinalPriceAndBasePriceEquals(productPayload.getFinalPrice(), productPayload.getBasePrice());
        }
    }

    public boolean isDiscountPercentageInRangeCorrect(int discountPercentage) {
        boolean result = (discountPercentage <= DISCOUNT_PERCENTAGE_MAX) && (discountPercentage >= DISCOUNT_PERCENTAGE_MIN);
        return logHelper.logIfFalse(result, String.format("Discount percentage is incorrect. Is: %d", discountPercentage));
    }


    private boolean isDiscountPercentageInRange(int discountPercentage) {
        boolean result = discountPercentage == 0;
        return logHelper.logIfFalse(result, String.format("Discount percentage is not zero. Is: %d", discountPercentage));
    }

    private boolean isFinalPriceCorrect(BigDecimal basePrice, int discountPercentage, BigDecimal finalPrice) {
        BigDecimal finalPriceCalculate = calculateFinalPrice.calculateFinalPrice(basePrice, discountPercentage);
        boolean result = finalPriceCalculate.compareTo(finalPrice) == 0;
        return logHelper.logIfFalse(result, String.format("Final price is incorrect. Is: %.2f but should be: %.2f", finalPrice, finalPriceCalculate));
    }
}
