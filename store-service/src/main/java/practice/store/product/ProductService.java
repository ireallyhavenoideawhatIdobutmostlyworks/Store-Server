package practice.store.product;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.store.exceptions.product.*;
import practice.store.order.details.OrderProductPayload;
import practice.store.utils.converter.EntitiesConverter;
import practice.store.utils.converter.PayloadsConverter;
import practice.store.utils.numbers.CalculatePrice;
import practice.store.utils.values.GenerateRandomString;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@PropertySource("classpath:productsDiscountValue.properties")
@Transactional
@Service
@Log4j2
public class ProductService {

    private final ProductRepository productRepository;
    private final EntitiesConverter entitiesConverter;

    private final PayloadsConverter payloadsConverter;

    private final GenerateRandomString generateRandomString;
    private final CalculatePrice calculateFinalPrice;

    private final int discountPercentageMaxHigherValue;
    private final int discountPercentageMaxLowerValue;

    @Autowired
    public ProductService(ProductRepository productRepository, EntitiesConverter entitiesConverter, PayloadsConverter payloadsConverter, GenerateRandomString generateRandomString, CalculatePrice calculateFinalPrice, @Value("${discount.percentage.max.value.higher}") int discountPercentageMaxHigherValue, @Value("${discount.percentage.max.value.lower}") int discountPercentageMaxLowerValue) {
        this.productRepository = productRepository;
        this.entitiesConverter = entitiesConverter;
        this.payloadsConverter = payloadsConverter;
        this.generateRandomString = generateRandomString;
        this.calculateFinalPrice = calculateFinalPrice;
        this.discountPercentageMaxHigherValue = discountPercentageMaxHigherValue;
        this.discountPercentageMaxLowerValue = discountPercentageMaxLowerValue;
    }

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

        log.info("Looking for all customers. List size: {}", productsList.size());
        return productsList;
    }

    public void save(ProductPayload productPayload) {
        checkIfProductIsNotWithdrawFromSale(productPayload.getAvailability(), productPayload.getName(), productPayload.getProductUUID());
        checkIfProductUuidExist(productPayload.getProductUUID());

        if (productPayload.isHasDiscount()) {
            checkIfDiscountPercentageIsNotToHigh(productPayload.getDiscountPercentage());
            checkIfDiscountPercentageIsNotToLow(productPayload.getDiscountPercentage());
            checkIfFinalPriceIsCorrect(productPayload.getBasePrice(), productPayload.getDiscountPercentage(), productPayload.getFinalPrice());
            checkIfPriceReductionIsCorrect(productPayload.getBasePrice(), productPayload.getDiscountPercentage(), productPayload.getAmountPriceReduction());
        } else {
            checkIfDiscountPercentageIsEqualZero(productPayload.getDiscountPercentage());
            checkIfPriceReductionIsEqualZero(productPayload.getAmountPriceReduction());
            checkIfFinalPriceAndBasePriceAreEquals(productPayload.getFinalPrice(), productPayload.getBasePrice());
        }

        calculateAvailabilityDependsOnProductAmounts(productPayload);

        productPayload.setId(null);
        productPayload.setActive(true);

        ProductEntity existingProduct = payloadsConverter.convertProduct(productPayload);
        productRepository.save(existingProduct);
        log.info("Saved new product. Entity details: {}", existingProduct);
    }

    public void edit(ProductPayload productPayload, String uuid) {
        checkIfProductUuidsAreTheSame(productPayload.getProductUUID(), uuid);

        if (productPayload.isHasDiscount()) {
            isDiscountValueValid(productPayload);

            setPriceAndAmount(productPayload);
        } else {
            setNoDiscount(productPayload);
        }

        calculateAvailabilityDependsOnProductAmounts(productPayload);

        productPayload.setId(productRepository.findByProductUUID(uuid).getId());
        ProductEntity existingProduct = payloadsConverter.convertProduct(productPayload);
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
        checkIfDiscountPercentageIsNotToHigh(productPayload.getDiscountPercentage());
        checkIfDiscountPercentageIsNotToLow(productPayload.getDiscountPercentage());
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

    private void checkIfProductUuidsAreTheSame(String uuidPayload, String uuidParameter) {
        if (!uuidPayload.equals(uuidParameter))
            throw new ProductUuidCanNotChangeException();
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

    private void checkIfFinalPriceAndBasePriceAreEquals(BigDecimal finalPrice, BigDecimal basePrice) {
        if (finalPrice.compareTo(basePrice) != 0)
            throw new ProductFinalAndBasePriceException();
    }

    private void checkIfDiscountPercentageIsEqualZero(int discountPercentage) {
        if (discountPercentage != 0)
            throw new ProductDiscountPercentageException();
    }

    private void checkIfPriceReductionIsEqualZero(BigDecimal amountPriceReduction) {
        if (amountPriceReduction.compareTo(BigDecimal.ZERO) != 0)
            throw new ProductPriceReductionException();
    }

    private void checkIfDiscountPercentageIsNotToHigh(int discountPercentage) {
        if (discountPercentage > discountPercentageMaxHigherValue)
            throw new ProductDiscountPercentageHighException(discountPercentageMaxHigherValue);
    }

    private void checkIfDiscountPercentageIsNotToLow(int discountPercentage) {
        if (discountPercentage < discountPercentageMaxLowerValue)
            throw new ProductDiscountPercentageLowException(discountPercentageMaxLowerValue);
    }

    private void checkIfFinalPriceIsCorrect(BigDecimal basePrice, int discountPercentage, BigDecimal finalPrice) {
        BigDecimal finalPriceCalculate = calculateFinalPrice.calculateFinalPrice(basePrice, discountPercentage);

        if (finalPriceCalculate.compareTo(finalPrice) != 0)
            throw new ProductFinalPriceException(finalPrice, finalPriceCalculate);
    }

    private void checkIfPriceReductionIsCorrect(BigDecimal basePrice, int discountPercentage, BigDecimal amountPriceReduction) {
        BigDecimal finalPriceCalculate = calculateFinalPrice.calculateFinalPrice(basePrice, discountPercentage);
        BigDecimal amountPriceReductionCalculate = basePrice.subtract(finalPriceCalculate);

        if (amountPriceReductionCalculate.compareTo(amountPriceReduction) != 0)
            throw new ProductPriceReductionException(amountPriceReduction, amountPriceReductionCalculate);
    }

    private void checkIfProductUuidExist(String uuid) {
        if (productRepository.existsByProductUUID(uuid))
            throw new ProductUuidExistException(uuid);
    }

    private void checkIfProductIsNotWithdrawFromSale(Availability availability, String name, String uuid) {
        if (availability.equals(Availability.WITHDRAW_FROM_SALE))
            throw new ProductWithdrawFromSaleException(name, uuid);
    }
}
