package practice.store.product;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.store.order.details.OrderProductPayload;
import practice.store.utils.converter.EntitiesConverter;
import practice.store.utils.converter.PayloadsConverter;
import practice.store.utils.numbers.CalculatePrice;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@PropertySource("classpath:productsDiscountValue.properties")
@AllArgsConstructor
@Transactional
@Service
@Log4j2
public class ProductService {

    private final ProductRepository productRepository;
    private final EntitiesConverter entitiesConverter;

    private final PayloadsConverter payloadsConverter;

    private final CalculatePrice calculateFinalPrice;

    @Value("${discount.percentage.max.value.higher}")
    private final int DISCOUNT_PERCENTAGE_MAX;
    @Value("${discount.percentage.max.value.lower}")
    private final int DISCOUNT_PERCENTAGE_MIN;


    public ProductPayload getByUuid(String uuid) {
        log.info("Looking for product by uuid: {}", uuid);
        return entitiesConverter.convertProduct(productRepository.findByProductUUID(uuid));
    }

    public List<ProductPayload> getProducts() {
        List<ProductPayload> productsList = productRepository.
                findAll()
                .stream()
                .map(entitiesConverter::convertProduct)
                .collect(Collectors.toList());

        log.info("Looking for all customers. List size: {}", productsList.size());
        return productsList;
    }

    public boolean save(ProductPayload productPayload) {
        boolean isProductNotWithdrawFromSale = isProductNotWithdrawFromSale(productPayload.getAvailability());
        if (!isProductNotWithdrawFromSale) {
            log.info("Product with UUID: {} is withdraw from sale", productPayload.getProductUUID());
            return false;
        }

        boolean isProductUuidExist = isProductUuidExist(productPayload.getProductUUID());
        if (!isProductUuidExist) {
            log.info("Product with UUID: {} is not exist", productPayload.getProductUUID());
            return false;
        }

        boolean areDiscountAndPriceCorrect = areDiscountAndPriceCorrect(productPayload);
        if (areDiscountAndPriceCorrect) {
            log.info("Product with UUID: {} has incorrect discount and price", productPayload.getProductUUID());
            return false;
        }

        Availability calculatedAvailability = calculateAvailabilityDependsOnProductAmounts(productPayload.getProductUUID(), productPayload.getAmount());
        productPayload.setAvailability(calculatedAvailability);

        ProductEntity productEntity = payloadsConverter.convertProduct(productPayload);
        productRepository.save(productEntity);

        log.info("Saved new product. Details: {}", productEntity);
        return true;
    }

    public boolean edit(ProductPayload productPayload, String uuid) {
        boolean areProductUuidsTheSame = areProductUuidsTheSame(productPayload.getProductUUID(), uuid);
        if (!areProductUuidsTheSame) {
            log.info("Product UUID are not same: {} and {}", productPayload.getProductUUID(), uuid);
            return false;
        }

        if (productPayload.isHasDiscount()) {
            boolean isDiscountPercentageNotToHigh = isDiscountPercentageNotToHigh(productPayload.getDiscountPercentage());
            if (!isDiscountPercentageNotToHigh) {
                log.info("Product UUID discount percentage is to high: {}", productPayload.getDiscountPercentage());
                return false;
            }
            boolean isDiscountPercentageNotToLow = isDiscountPercentageNotToLow(productPayload.getDiscountPercentage());
            if (!isDiscountPercentageNotToLow) {
                log.info("Product UUID discount percentage is to low: {}", productPayload.getDiscountPercentage());
                return false;
            }

            setPriceAndAmount(productPayload);
        } else {
            setNoDiscount(productPayload);
        }

        Availability calculatedAvailability = calculateAvailabilityDependsOnProductAmounts(productPayload.getProductUUID(), productPayload.getAmount());
        productPayload.setAvailability(calculatedAvailability);

        ProductEntity existingProduct = payloadsConverter.convertProduct(productPayload);
        long productID = productRepository.findByProductUUID(productPayload.getProductUUID()).getId();
        existingProduct.setId(productID);
        productRepository.save(existingProduct);

        log.info("Edited product. Entity details: {}", existingProduct);
        return true;
    }

    public void changeAmountBoughtProduct(ProductEntity productEntity, OrderProductPayload orderProductPayload) {
        productEntity.setAmount(productEntity.getAmount() - orderProductPayload.getAmount());

        Availability calculatedAvailability = calculateAvailabilityDependsOnProductAmounts(productEntity.getProductUUID(), productEntity.getAmount());
        productEntity.setAvailability(calculatedAvailability);

        productRepository.save(productEntity);
        log.info("Changed amount bought product. Entity amount: {}, payload amount: {}", productEntity.getAmount(), orderProductPayload.getAmount());
    }


    private boolean areDiscountAndPriceCorrect(ProductPayload productPayload) {
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

    private void setPriceAndAmount(ProductPayload productPayload) {
        BigDecimal finalPrice = calculateFinalPrice.calculateFinalPrice(productPayload.getBasePrice(), productPayload.getDiscountPercentage());
        productPayload.setFinalPrice(finalPrice);
        productPayload.setAmountPriceReduction(productPayload.getBasePrice().subtract(finalPrice));
    }

    private void setNoDiscount(ProductPayload productPayload) {
        productPayload.setDiscountPercentage(0);
        productPayload.setAmountPriceReduction(BigDecimal.valueOf(0));
        productPayload.setFinalPrice(productPayload.getBasePrice());
    }

    private boolean areProductUuidsTheSame(String uuidPayload, String uuidParameter) {
        return uuidPayload.equals(uuidParameter);
    }

    private Availability calculateAvailabilityDependsOnProductAmounts(String uuid, int amount) {
        if (amount == 0) {
            log.warn("Amount of product with UUID:{} is 0. Set availability to: {}", uuid, Availability.NOT_AVAILABLE);
            return Availability.NOT_AVAILABLE;
        }

        if (amount < 5) {
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
        return discountPercentage > DISCOUNT_PERCENTAGE_MAX; // up to 95
    }

    private boolean isDiscountPercentageNotToLow(int discountPercentage) {
        return discountPercentage < DISCOUNT_PERCENTAGE_MIN; // low to 5
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
