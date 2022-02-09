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
                .orElseThrow((() -> new EntityNotFoundException(String.format("Entity with UUID: %s not found", uuid))));

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
        if (!isProductWithdrawFromSale(productPayload.getAvailability())) {
            return false;
        }
        if (!areDiscountAndPriceCorrect(productPayload)) {
            return false;
        }

        Availability calculatedAvailability = calculateAvailabilityDependsOnProductAmounts(productPayload.getProductUUID(), productPayload.getAmount());
        ProductEntity convertedProductPayload = payloadsConverter.convertProduct(productPayload, calculatedAvailability);
        productRepository.save(convertedProductPayload);

        log.info("Saved new product. Details: {}", convertedProductPayload);
        return true;
    }

    public boolean edit(ProductPayload productPayload) {
        long existProductEntityID = productRepository
                .findByProductUUID(productPayload.getProductUUID())
                .map(ProductEntity::getId)
                .orElseThrow((() -> new EntityNotFoundException(String.format("Entity with UUID: %s not found", productPayload.getProductUUID()))));

        Availability calculatedAvailability = calculateAvailabilityDependsOnProductAmounts(productPayload.getProductUUID(), productPayload.getAmount());
        ProductEntity existingProduct = payloadsConverter.convertProduct(productPayload, calculatedAvailability, existProductEntityID);

        if (existingProduct.isHasDiscount()) {
            if (!isDiscountPercentageInRangeCorrect(existingProduct.getDiscountPercentage())) {
                return false;
            }
            setFinalPrice(existingProduct);
        } else {
            setNoDiscount(existingProduct);
        }

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
            return isDiscountPercentageInRangeCorrect(productPayload.getDiscountPercentage())
                    && isFinalPriceCorrect(productPayload.getBasePrice(), productPayload.getDiscountPercentage(), productPayload.getFinalPrice());
        } else {
            return isDiscountPercentageInRange(productPayload.getDiscountPercentage())
                    && areFinalPriceAndBasePriceEquals(productPayload.getFinalPrice(), productPayload.getBasePrice());
        }
    }

    private void setFinalPrice(ProductEntity productEntity) {
        BigDecimal finalPrice = calculateFinalPrice.calculateFinalPrice(productEntity.getBasePrice(), productEntity.getDiscountPercentage());
        productEntity.setFinalPrice(finalPrice);
    }

    private void setNoDiscount(ProductEntity productEntity) {
        productEntity.setDiscountPercentage(0);
        productEntity.setFinalPrice(productEntity.getBasePrice());
    }

    private Availability calculateAvailabilityDependsOnProductAmounts(String uuid, int amount) {
        if (amount == 0) {
            log.info("Amount of product with UUID:{} is 0. Set availability to: {}", uuid, Availability.NOT_AVAILABLE);
            return Availability.NOT_AVAILABLE;
        }

        if (amount < 5) {
            log.info("Amount of product with UUID:{} is less than 5. Set availability to: {}", uuid, Availability.AWAITING_FROM_MANUFACTURE);
            return Availability.AWAITING_FROM_MANUFACTURE;
        }

        log.info("Amount of product with UUID:{} is {}. Set availability to: {}", uuid, amount, Availability.AVAILABLE);
        return Availability.AVAILABLE;
    }

    private boolean areFinalPriceAndBasePriceEquals(BigDecimal finalPrice, BigDecimal basePrice) {
        boolean result = finalPrice.compareTo(basePrice) == 0;
        return logIfFalse(result, String.format("Final price: %.2f and base price: %.2f are not equal", finalPrice, basePrice));
    }

    private boolean isDiscountPercentageInRangeCorrect(int discountPercentage) {
        boolean result = (discountPercentage <= DISCOUNT_PERCENTAGE_MAX) && (discountPercentage >= DISCOUNT_PERCENTAGE_MIN);
        return logIfFalse(result, String.format("Discount percentage is incorrect. Is: %d", discountPercentage));
    }

    private boolean isDiscountPercentageInRange(int discountPercentage) {
        boolean result = discountPercentage == 0;
        return logIfFalse(result, String.format("Discount percentage is not zero. Is: %d", discountPercentage));
    }

    private boolean isFinalPriceCorrect(BigDecimal basePrice, int discountPercentage, BigDecimal finalPrice) {
        BigDecimal finalPriceCalculate = calculateFinalPrice.calculateFinalPrice(basePrice, discountPercentage);
        boolean result = finalPriceCalculate.compareTo(finalPrice) == 0;
        return logIfFalse(result, String.format("Final price is incorrect. Is: %.2f but should be: %.2f", finalPrice, finalPriceCalculate));
    }

    private boolean isProductUuidExist(String uuid) {
        boolean result = productRepository.existsByProductUUID(uuid);
        return logIfFalse(result, String.format("Product with UUID: %s is not exist", uuid));
    }

    private boolean isProductWithdrawFromSale(Availability availability) {
        boolean result = !(availability.equals(Availability.WITHDRAW_FROM_SALE));
        return logIfFalse(result, "Product is withdraw from sale");
    }

    private boolean logIfFalse(boolean result, String desc) {
        if (!result) {
            log.error("Result condition is false because: {}.", desc);
            return false;
        }
        return true;
    }
}
