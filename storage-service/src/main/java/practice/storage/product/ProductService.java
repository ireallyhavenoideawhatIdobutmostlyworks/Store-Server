package practice.storage.product;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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
        boolean isSavedSuccess = isProductNotWithdrawFromSale(productPayload.getAvailability()) && isProductUuidExist(productPayload.getProductUUID());

        if (productPayload.isHasDiscount()) {
            isSavedSuccess =
                    isDiscountPercentageToLow(productPayload.getDiscountPercentage()) &&
                            isFinalPriceCorrect(productPayload.getBasePrice(), productPayload.getDiscountPercentage(), productPayload.getFinalPrice()) &&
                            isPriceReductionCorrect(productPayload.getBasePrice(), productPayload.getDiscountPercentage(), productPayload.getAmountPriceReduction());
        } else {
            isSavedSuccess =
                    isDiscountPercentageEqualZero(productPayload.getDiscountPercentage()) &&
                            isPriceReductionEqualZero(productPayload.getAmountPriceReduction()) &&
                            areFinalPriceAndBasePriceEquals(productPayload.getFinalPrice(), productPayload.getBasePrice());
        }


        if (isSavedSuccess) {
            calculateAvailabilityDependsOnProductAmounts(productPayload);

            ProductEntity existingProduct = payloadsConverter.convertProduct(productPayload);
            productRepository.save(existingProduct);

            log.info("Saved new product. Entity details: {}", existingProduct);
            return true;
        } else {
            log.info("Can not save product. Incorrect data");
            return false;
        }
    }

    public void edit(ProductPayload productPayload, String uuid) {
        areProductUuidsTheSame(productPayload.getProductUUID(), uuid);

        if (productPayload.isHasDiscount()) {
            isDiscountValueValid(productPayload);

            setPriceAndAmount(productPayload);
        } else {
            setNoDiscount(productPayload);
        }

        calculateAvailabilityDependsOnProductAmounts(productPayload);

        ProductEntity existingProduct = payloadsConverter.convertProduct(productPayload);
        existingProduct.setId(productRepository.findByProductUUID(uuid).getId());
        productRepository.save(existingProduct);
        log.info("Edited product. Entity details: {}", existingProduct);
        // ToDo add new payload for edit or change existing.
    }

    public void changeAmountBoughtProduct(ProductEntity productEntity, OrderProductPayload orderProductPayload) {
        productEntity.setAmount(productEntity.getAmount() - orderProductPayload.getAmount());
        calculateAvailabilityDependsOnProductAmounts(productEntity);
        productRepository.save(productEntity);
        log.info("Changed amount bought product. Entity amount: {}, payload amount: {}", productEntity.getAmount(), orderProductPayload.getAmount());
    }


    private void isDiscountValueValid(ProductPayload productPayload) {
        isDiscountPercentageToHigh(productPayload.getDiscountPercentage());
        isDiscountPercentageToLow(productPayload.getDiscountPercentage());
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

    // ToDo create service for calculate availability
    private void calculateAvailabilityDependsOnProductAmounts(ProductPayload product) {
        if (product.getAmount() == 0) {
            product.setAvailability(Availability.NOT_AVAILABLE);
            log.warn("Amount of product with UUID:{} is 0. Set availability to: {}", product.getProductUUID(), Availability.NOT_AVAILABLE);
        } else if (product.getAmount() < 5) {
            product.setAvailability(Availability.AWAITING_FROM_MANUFACTURE);
            log.warn("Amount of product with UUID:{} is less than 5. Set availability to: {}", product.getProductUUID(), Availability.AWAITING_FROM_MANUFACTURE);
        }
    }

    // ToDo create service for calculate availability
    private void calculateAvailabilityDependsOnProductAmounts(ProductEntity product) {
        if (product.getAmount() == 0) {
            product.setAvailability(Availability.NOT_AVAILABLE);
            log.warn("Amount of product with UUID:{} is 0. Set availability to: {}", product.getProductUUID(), Availability.NOT_AVAILABLE);
        } else if (product.getAmount() < 5) {
            product.setAvailability(Availability.AWAITING_FROM_MANUFACTURE);
            log.warn("Amount of product with UUID:{} is less than 5. Set availability to: {}", product.getProductUUID(), Availability.AWAITING_FROM_MANUFACTURE);
        }
    }

    private boolean areFinalPriceAndBasePriceEquals(BigDecimal finalPrice, BigDecimal basePrice) {
        return finalPrice.compareTo(basePrice) == 0;
    }

    private boolean isDiscountPercentageEqualZero(int discountPercentage) {
        return discountPercentage != 0;
    }

    private boolean isPriceReductionEqualZero(BigDecimal amountPriceReduction) {
        return amountPriceReduction.compareTo(BigDecimal.ZERO) == 0;
    }

    private boolean isDiscountPercentageToHigh(int discountPercentage) {
        return !(discountPercentage > discountPercentageMaxHigherValue);
    }

    private boolean isDiscountPercentageToLow(int discountPercentage) {
        return !(discountPercentage < discountPercentageMaxLowerValue);
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
        return !availability.equals(Availability.WITHDRAW_FROM_SALE);
    }
}
