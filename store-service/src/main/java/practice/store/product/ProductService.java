package practice.store.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.store.order.details.OrderProductPayload;
import practice.store.utils.converter.EntitiesConverter;
import practice.store.utils.converter.PayloadsConverter;
import practice.store.utils.numbers.CalculatePrice;
import practice.store.validator.ProductValidator;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
@Log4j2
public class ProductService {

    private final ProductRepository productRepository;
    private final EntitiesConverter entitiesConverter;

    private final PayloadsConverter payloadsConverter;

    private final CalculatePrice calculateFinalPrice;

    private final AvailabilityService availabilityService;

    private final ProductValidator productValidator;


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
        if (productValidator.isProductUuidExist(productPayload.getProductUUID())) {
            return false;
        }
        if (!productValidator.isProductWithdrawFromSale(productPayload.getAvailability())) {
            return false;
        }
        if (!productValidator.areDiscountAndPriceCorrect(productPayload)) {
            return false;
        }

        Availability calculatedAvailability = availabilityService.calculateAvailability(productPayload.getProductUUID(), productPayload.getAmount());
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

        Availability calculatedAvailability = availabilityService.calculateAvailability(productPayload.getProductUUID(), productPayload.getAmount());
        ProductEntity existingProduct = payloadsConverter.convertProduct(productPayload, calculatedAvailability, existProductEntityID);

        if (existingProduct.isHasDiscount()) {
            if (!productValidator.isDiscountPercentageInRangeCorrect(existingProduct.getDiscountPercentage())) {
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
        productEntity.setAvailability(availabilityService.calculateAvailability(productEntity.getProductUUID(), productEntity.getAmount()));
        productRepository.save(productEntity);
        log.info("Changed amount bought product. Entity amount: {}, payload amount: {}", productEntity.getAmount(), orderProductPayload.getAmount());
    }


    private void setFinalPrice(ProductEntity productEntity) {
        BigDecimal finalPrice = calculateFinalPrice.calculateFinalPrice(productEntity.getBasePrice(), productEntity.getDiscountPercentage());
        productEntity.setFinalPrice(finalPrice);
    }

    private void setNoDiscount(ProductEntity productEntity) {
        productEntity.setDiscountPercentage(0);
        productEntity.setFinalPrice(productEntity.getBasePrice());
    }
}
