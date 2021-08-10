package testdata;

import practice.store.product.Availability;
import practice.store.product.Categories;
import practice.store.product.ProductEntity;
import practice.store.product.ProductPayload;
import practice.store.utils.values.GenerateRandomString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

public class DataFactoryProduct {

    public static ProductEntity createProductEntity(long id, String name, String productUUID, String description, BigDecimal basePrice, BigDecimal amountPriceReduction, BigDecimal finalPrice, int discountPercentage, boolean hasDiscount, int amountInStock, Categories categories, Availability availability, boolean isActive) {
        return ProductEntity.builder()
                .id(id)
                .name(name)
                .productUUID(productUUID)
                .description(description)
                .basePrice(basePrice)
                .amountPriceReduction(amountPriceReduction)
                .finalPrice(finalPrice)
                .discountPercentage(discountPercentage)
                .hasDiscount(hasDiscount)
                .amountInStock(amountInStock)
                .categories(categories)
                .availability(availability)
                .isActive(isActive)
                .build();
    }

    public static ProductPayload createProductPayload(long id, String name, String productUUID, String description, BigDecimal basePrice, BigDecimal amountPriceReduction, BigDecimal finalPrice, int discountPercentage, boolean hasDiscount, int amountInStock, Categories categories, Availability availability, boolean isActive) {
        return ProductPayload.builder()
                .id(id)
                .name(name)
                .productUUID(productUUID)
                .description(description)
                .basePrice(basePrice)
                .amountPriceReduction(amountPriceReduction)
                .finalPrice(finalPrice)
                .discountPercentage(discountPercentage)
                .hasDiscount(hasDiscount)
                .amountInStock(amountInStock)
                .categories(categories)
                .availability(availability)
                .isActive(isActive)
                .build();
    }

    public static ProductPayload createProductPayloadWithDiscount() {
        return ProductPayload.builder()
                .id(1L)
                .name("test name")
                .productUUID(new GenerateRandomString().generateRandomUuid())
                .description("test description")
                .basePrice(setBigDecimalWithScale(100))
                .amountPriceReduction(setBigDecimalWithScale(10))
                .finalPrice(setBigDecimalWithScale(90))
                .discountPercentage(10)
                .hasDiscount(true)
                .amountInStock(5)
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();
    }

    public static ProductPayload createProductPayloadWithDiscount(long id, String name, String uuid) {
        return ProductPayload.builder()
                .id(id)
                .name(name)
                .productUUID(uuid)
                .description("test description")
                .basePrice(setBigDecimalWithScale(100))
                .amountPriceReduction(setBigDecimalWithScale(10))
                .finalPrice(setBigDecimalWithScale(90))
                .discountPercentage(10)
                .hasDiscount(true)
                .amountInStock(5)
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();
    }

    public static ProductPayload createProductPayloadWithoutDiscount() {
        return ProductPayload.builder()
                .id(1L)
                .name("test name")
                .productUUID(new GenerateRandomString().generateRandomUuid())
                .description("test description")
                .basePrice(setBigDecimalWithScale(100))
                .amountPriceReduction(setBigDecimalWithScale(0))
                .finalPrice(setBigDecimalWithScale(100))
                .discountPercentage(0)
                .hasDiscount(false)
                .amountInStock(5)
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();
    }

    public static ProductPayload createProductPayloadWithoutDiscount(long id, String name, String uuid) {
        return ProductPayload.builder()
                .id(id)
                .name(name)
                .productUUID(uuid)
                .description("test description")
                .basePrice(setBigDecimalWithScale(100))
                .amountPriceReduction(setBigDecimalWithScale(0))
                .finalPrice(setBigDecimalWithScale(100))
                .discountPercentage(0)
                .hasDiscount(false)
                .amountInStock(5)
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();
    }

    public static ProductEntity createProductEntity() {
        return ProductEntity.builder()
                .id(1L)
                .name("test name")
                .productUUID("test uuid")
                .description("test description")
                .basePrice(setBigDecimalWithScale(100))
                .amountPriceReduction(setBigDecimalWithScale(10))
                .finalPrice(setBigDecimalWithScale(90))
                .discountPercentage(10)
                .hasDiscount(true)
                .amountInStock(5)
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();
    }

    public static ProductEntity createProductEntity(String uuid) {
        return ProductEntity.builder()
                .id(1L)
                .name("test name")
                .productUUID(uuid)
                .description("test description")
                .basePrice(setBigDecimalWithScale(100))
                .amountPriceReduction(setBigDecimalWithScale(10))
                .finalPrice(setBigDecimalWithScale(90))
                .discountPercentage(10)
                .hasDiscount(true)
                .amountInStock(5)
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();
    }

    public static ProductEntity createProductEntityWithoutDiscount(String uuid) {
        return ProductEntity.builder()
                .id(1L)
                .name("test name")
                .productUUID(uuid)
                .description("test description")
                .basePrice(setBigDecimalWithScale(100))
                .amountPriceReduction(setBigDecimalWithScale(0))
                .finalPrice(setBigDecimalWithScale(100))
                .discountPercentage(0)
                .hasDiscount(false)
                .amountInStock(5)
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();
    }

    public static List<ProductEntity> creteProductList() {
        ProductEntity existingProductEntityFirst = ProductEntity
                .builder()
                .id(11L)
                .name("test name 1")
                .productUUID("testUUID 1")
                .description("test description 1")
                .basePrice(setBigDecimalWithScale(100))
                .amountPriceReduction(setBigDecimalWithScale(10))
                .finalPrice(setBigDecimalWithScale(90))
                .discountPercentage(10)
                .hasDiscount(true)
                .amountInStock(5)
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();

        ProductEntity existingProductEntitySecond = ProductEntity
                .builder()
                .id(12L)
                .name("test name 2")
                .productUUID("testUUID 2")
                .description("test description 2")
                .basePrice(setBigDecimalWithScale(100))
                .amountPriceReduction(setBigDecimalWithScale(10))
                .finalPrice(setBigDecimalWithScale(90))
                .discountPercentage(10)
                .hasDiscount(true)
                .amountInStock(5)
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();

        ProductEntity existingProductEntityThird = ProductEntity
                .builder()
                .id(13L)
                .name("test name 3")
                .productUUID("testUUID 3")
                .description("test description 3")
                .basePrice(setBigDecimalWithScale(100))
                .amountPriceReduction(setBigDecimalWithScale(10))
                .finalPrice(setBigDecimalWithScale(90))
                .discountPercentage(10)
                .hasDiscount(true)
                .amountInStock(5)
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();

        return Arrays.asList(existingProductEntityFirst, existingProductEntitySecond, existingProductEntityThird);
    }


    private static BigDecimal setBigDecimalWithScale(long val) {
        return BigDecimal.valueOf(val).setScale(2, RoundingMode.HALF_UP);
    }
}

