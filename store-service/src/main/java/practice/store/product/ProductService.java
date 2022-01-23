package practice.store.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.store.order.details.OrderProductPayload;
import practice.store.utils.converter.EntitiesConverter;
import practice.store.utils.converter.PayloadsConverter;
import practice.store.utils.numbers.CalculatePrice;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@PropertySource("classpath:productsDiscountValue.properties")
@RequiredArgsConstructor
@Transactional
@Service
@Log4j2
public class ProductService {

    private final ProductRepository productRepository;
    private final EntitiesConverter entitiesConverter;

    private final PayloadsConverter payloadsConverter;

    private final CalculatePrice calculateFinalPrice;

    @Value("${discount.percentage.max.value.higher}")
    private int DISCOUNT_PERCENTAGE_MAX;
    @Value("${discount.percentage.max.value.lower}")
    private int DISCOUNT_PERCENTAGE_MIN;


    public ProductPayload getByUuid(String uuid) {
        ProductEntity product = productRepository
                .findByProductUUID(uuid)
                .orElseThrow((() -> new EntityNotFoundException("Element not found")));

        log.info("Returned product: {}", product);
        return entitiesConverter.convertProduct(product);
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
        if (isProductUuidExist(productPayload.getProductUUID())) {
            return false;
        }
        if (!isProductNotWithdrawFromSale(productPayload.getAvailability())) {
            return false;
        }
        if (!areDiscountAndPriceCorrect(productPayload)) {
            return false;
        }

        Availability calculatedAvailability = calculateAvailabilityDependsOnProductAmounts(productPayload.getProductUUID(), productPayload.getAmount());
        productPayload.setAvailability(calculatedAvailability);

        ProductEntity convertedProductPayload = payloadsConverter.convertProduct(productPayload);
        productRepository.save(convertedProductPayload);

        log.info("Saved new product. Details: {}", convertedProductPayload);
        return true;
    }

    public boolean edit(ProductPayload productPayload, String uuid) {
        ProductEntity productEntity = productRepository
                .findByProductUUID(uuid)
                .filter(product -> areProductUuidsTheSame(productPayload.getProductUUID(), uuid))
                .orElseThrow((() -> new EntityNotFoundException("Element not found")));

        if (productPayload.isHasDiscount()) {
            if (!isDiscountPercentageCorrect(productPayload.getDiscountPercentage())) {
                return false;
            }

            setPriceAndAmount(productPayload);
        } else {
            setNoDiscount(productPayload);
        }

        Availability calculatedAvailability = calculateAvailabilityDependsOnProductAmounts(productPayload.getProductUUID(), productPayload.getAmount());
        productPayload.setAvailability(calculatedAvailability);

        ProductEntity existingProduct = payloadsConverter.convertProduct(productPayload);
        existingProduct.setId(productEntity.getId());
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
            return isDiscountPercentageCorrect(productPayload.getDiscountPercentage()) &&
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
        boolean result = uuidPayload.equals(uuidParameter);
        return isResultTrue(result, "areProductUuidsTheSame");
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
        boolean result = finalPrice.compareTo(basePrice) == 0;
        return isResultTrue(result, "areFinalPriceAndBasePriceEquals");
    }

    private boolean isDiscountPercentageCorrect(int discountPercentage) {
        boolean result = (discountPercentage <= DISCOUNT_PERCENTAGE_MAX) && (discountPercentage >= DISCOUNT_PERCENTAGE_MIN);
        return isResultTrue(result, "isDiscountPercentageNotToHigh");
    }

    private boolean isDiscountPercentageEqualZero(int discountPercentage) {
        boolean result = discountPercentage == 0;
        return isResultTrue(result, "isDiscountPercentageEqualZero");
    }

    private boolean isPriceReductionEqualZero(BigDecimal amountPriceReduction) {
        boolean result = amountPriceReduction.compareTo(BigDecimal.ZERO) == 0;
        return isResultTrue(result, "isPriceReductionEqualZero");
    }

    private boolean isFinalPriceCorrect(BigDecimal basePrice, int discountPercentage, BigDecimal finalPrice) {
        BigDecimal finalPriceCalculate = calculateFinalPrice.calculateFinalPrice(basePrice, discountPercentage);
        boolean result = finalPriceCalculate.compareTo(finalPrice) == 0;
        return isResultTrue(result, "isFinalPriceCorrect");
    }

    private boolean isPriceReductionCorrect(BigDecimal basePrice, int discountPercentage, BigDecimal amountPriceReduction) {
        BigDecimal finalPriceCalculate = calculateFinalPrice.calculateFinalPrice(basePrice, discountPercentage);
        BigDecimal amountPriceReductionCalculate = basePrice.subtract(finalPriceCalculate);
        boolean result = amountPriceReductionCalculate.compareTo(amountPriceReduction) == 0;
        return isResultTrue(result, "isPriceReductionCorrect");
    }

    private boolean isProductUuidExist(String uuid) {
        boolean result = productRepository.existsByProductUUID(uuid);
        return isResultTrue(result, "isProductUuidExist");
    }

    private boolean isProductNotWithdrawFromSale(Availability availability) {
        boolean result = !(availability.equals(Availability.WITHDRAW_FROM_SALE));
        return isResultTrue(result, "isProductNotWithdrawFromSale");
    }

    private boolean isResultTrue(boolean result, String methodName) {
        if (!result) {
            log.error("{} result: false", methodName);
            return false;
        }
        return true;
    }
}
