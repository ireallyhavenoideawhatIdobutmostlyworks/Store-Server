package practice.store.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.store.exceptions.product.*;
import practice.store.utils.converter.EntitiesConverter;
import practice.store.utils.converter.PayloadsConverter;
import practice.store.utils.numbers.CalculatePriceProduct;
import practice.store.utils.values.GenerateRandomString;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@PropertySource("classpath:productsDiscountValue.properties")
@Transactional
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final EntitiesConverter entitiesConverter;

    private final PayloadsConverter payloadsConverter;

    private final GenerateRandomString generateRandomString;
    private final CalculatePriceProduct calculateFinalPrice;

    private final int discountPercentageMaxHigherValue;
    private final int discountPercentageMaxLowerValue;

    @Autowired
    public ProductService(ProductRepository productRepository, EntitiesConverter entitiesConverter, PayloadsConverter payloadsConverter, GenerateRandomString generateRandomString, CalculatePriceProduct calculateFinalPrice, @Value("${discount.percentage.max.value.higher}") int discountPercentageMaxHigherValue, @Value("${discount.percentage.max.value.lower}") int discountPercentageMaxLowerValue) {
        this.productRepository = productRepository;
        this.entitiesConverter = entitiesConverter;
        this.payloadsConverter = payloadsConverter;
        this.generateRandomString = generateRandomString;
        this.calculateFinalPrice = calculateFinalPrice;
        this.discountPercentageMaxHigherValue = discountPercentageMaxHigherValue;
        this.discountPercentageMaxLowerValue = discountPercentageMaxLowerValue;
    }

    public ProductPayload getById(long id) {
        return entitiesConverter.convertProduct(productRepository.getById(id));
    }

    public List<ProductPayload> getProducts() {
        return productRepository.
                findAll()
                .stream()
                .map(entitiesConverter::convertProduct)
                .collect(Collectors.toList());
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
    }

    public void edit(ProductPayload productPayload, long id) {
        checkIfEntityExist(id);
        checkIfProductUuidAndEntityAreTheSame(id, productPayload.getProductUUID());

        if (productPayload.isHasDiscount()) {
            checkIfDiscountPercentageIsNotToHigh(productPayload.getDiscountPercentage());
            checkIfDiscountPercentageIsNotToLow(productPayload.getDiscountPercentage());

            setPriceAndAmount(productPayload);
        } else {
            setNoDiscount(productPayload);
        }

        calculateAvailabilityDependsOnProductAmounts(productPayload);

        productPayload.setId(id);
        ProductEntity existingProduct = payloadsConverter.convertProduct(productPayload);

        productRepository.save(existingProduct);
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

    private void checkIfEntityExist(long id) {
        if (!productRepository.existsById(id))
            throw new EntityNotFoundException();
    }

    private void checkIfProductUuidAndEntityAreTheSame(long id, String uuid) {
        if (!productRepository.existsByProductUUIDAndId(uuid, id) && !productRepository.existsByProductUUID(uuid))
            throw new ProductUuidCanNotChangeException();
    }

    private void calculateAvailabilityDependsOnProductAmounts(ProductPayload product) {
        if (product.getAmountInStock() == 0)
            product.setAvailability(Availability.NOT_AVAILABLE);
        else if (product.getAmountInStock() < 5)
            product.setAvailability(Availability.AWAITING_FROM_MANUFACTURE);
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
