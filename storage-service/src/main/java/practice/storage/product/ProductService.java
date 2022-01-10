package practice.storage.product;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.storage.utils.converter.EntitiesConverter;
import practice.storage.utils.converter.PayloadsConverter;
import practice.storage.utils.price.CalculatePrice;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@PropertySource("classpath:productsDiscountValue.properties")
@Transactional
@Service
@Log4j2
public class ProductService {

    private final ProductRepository productRepository;
    private final EntitiesConverter entitiesConverter;

    private final PayloadsConverter payloadsConverter;

    private final CalculatePrice calculateFinalPrice;

    @Value("${discount.percentage.max.value.higher}")
    private final int discountPercentageMaxHigherValue;
    @Value("${discount.percentage.max.value.lower}")
    private final int discountPercentageMaxLowerValue;


    public ProductPayload getById(long id) {
        log.info("Looking for product by id: {}", id);
        return entitiesConverter.convertProduct(productRepository.getById(id));
    }

    public List<ProductPayload> getProducts() {
        List<ProductPayload> productsList = productRepository.
                findAll()
                .stream()
                .map(entitiesConverter::convertProduct)
                .collect(Collectors.toList());

        log.info("Looking for all products. List size: {}", productsList.size());
        return productsList;
    }

    public boolean save(ProductPayload productPayload) {
        boolean isProductNotWithdrawFromSale = isProductNotWithdrawFromSale(productPayload.getAvailability());
        boolean isProductUuidExist = isProductUuidExist(productPayload.getProductUUID());
        boolean areDiscountAndPriceCorrect = areDiscountAndPriceCorrect(productPayload);

        if (isProductNotWithdrawFromSale && isProductUuidExist && areDiscountAndPriceCorrect) {
            Availability calculatedAvailability = calculateAvailabilityDependsOnProductAmounts(productPayload.getProductUUID(), productPayload.getAmount());
            productPayload.setAvailability(calculatedAvailability);

            ProductEntity productEntity = payloadsConverter.convertProduct(productPayload);
            productRepository.save(productEntity);

            log.info("Saved new product. Details: {}", productEntity);
            return true;
        } else {
            log.info("Can not save product. Incorrect data. Details: " +
                    "productPayload: {}, " +
                    "isProductNotWithdrawFromSale: {}, " +
                    "isProductUuidExist: {}, " +
                    "areDiscountAndPriceCorrect: {}",
                    productPayload, isProductNotWithdrawFromSale, isProductUuidExist, areDiscountAndPriceCorrect);
            return false;
        }
    }


    public boolean areDiscountAndPriceCorrect(ProductPayload productPayload) {
        if (productPayload.isHasDiscount()) {
            return isDiscountPercentageNotToLow(productPayload.getDiscountPercentage()) &&
                    isFinalPriceCorrect(productPayload.getBasePrice(), productPayload.getDiscountPercentage(), productPayload.getFinalPrice()) &&
                    isPriceReductionCorrect(productPayload.getBasePrice(), productPayload.getDiscountPercentage(), productPayload.getAmountPriceReduction());
        } else {
            return isDiscountPercentageEqualZero(productPayload.getDiscountPercentage()) &&
                    isPriceReductionEqualZero(productPayload.getAmountPriceReduction()) &&
                    areFinalPriceAndBasePriceEquals(productPayload.getFinalPrice(), productPayload.getBasePrice());
        }
    }


    private Availability calculateAvailabilityDependsOnProductAmounts(String uuid, int amount) {
        if (amount == 0) {
            log.warn("Amount of product with UUID:{} is 0. Set availability to: {}", uuid, Availability.NOT_AVAILABLE);
            return Availability.NOT_AVAILABLE;
        } else if (amount < 5) {
            log.warn("Amount of product with UUID:{} is less than 5. Set availability to: {}", uuid, Availability.AWAITING_FROM_MANUFACTURE);
            return Availability.AWAITING_FROM_MANUFACTURE;
        }

        log.info("Amount of product with UUID:{} is {}. Set availability to: {}", uuid, amount, Availability.AVAILABLE);
        return Availability.AVAILABLE;
    }



    private boolean areFinalPriceAndBasePriceEquals(BigDecimal finalPrice, BigDecimal basePrice) {
        return finalPrice.compareTo(basePrice) == 0;
    }

    private boolean isDiscountPercentageEqualZero(int discountPercentage) {
        return discountPercentage == 0;
    }

    private boolean isPriceReductionEqualZero(BigDecimal amountPriceReduction) {
        return amountPriceReduction.compareTo(BigDecimal.ZERO) == 0;
    }

    private boolean isDiscountPercentageNotToHigh(int discountPercentage) {
        return discountPercentage <= discountPercentageMaxHigherValue;
    }

    private boolean isDiscountPercentageNotToLow(int discountPercentage) {
        return discountPercentage >= discountPercentageMaxLowerValue;
    }

    private boolean isFinalPriceCorrect(BigDecimal basePrice, int discountPercentage, BigDecimal finalPrice) {
        BigDecimal finalPriceCalculate = calculateFinalPrice.calculateFinalPrice(basePrice, discountPercentage);

        return finalPriceCalculate.compareTo(finalPrice) == 0;
    }

    private boolean isPriceReductionCorrect(BigDecimal basePrice, int discountPercentage, BigDecimal amountPriceReduction) {
        BigDecimal finalPriceCalculate = calculateFinalPrice.calculateFinalPrice(basePrice, discountPercentage);
        BigDecimal amountPriceReductionCalculate = basePrice.subtract(finalPriceCalculate);

        return amountPriceReductionCalculate.compareTo(amountPriceReduction) == 0;
    }

    private boolean isProductUuidExist(String uuid) {
        return productRepository.existsByProductUUID(uuid);
    }

    private boolean isProductNotWithdrawFromSale(Availability availability) {
        return !(availability.equals(Availability.WITHDRAW_FROM_SALE));
    }
}
