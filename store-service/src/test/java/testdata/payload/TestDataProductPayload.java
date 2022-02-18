package testdata.payload;

import practice.store.product.Availability;
import practice.store.product.Categories;
import practice.store.product.ProductPayload;
import practice.store.utils.values.GenerateRandomString;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class TestDataProductPayload {

    public static ProductPayload Product(String name, String productUUID, String description, BigDecimal basePrice, BigDecimal amountPriceReduction, BigDecimal finalPrice, int discountPercentage, boolean hasDiscount, int amountInStock, Categories categories, Availability availability, boolean isActive) {
        return ProductPayload.builder()
                .name(name)
                .productUUID(productUUID)
                .description(description)
                .basePrice(basePrice)
                .finalPrice(finalPrice)
                .discountPercentage(discountPercentage)
                .hasDiscount(hasDiscount)
                .amount(amountInStock)
                .categories(categories)
                .availability(availability)
                .isActive(isActive)
                .build();
    }

    public static ProductPayload ProductWithDiscount() {
        return ProductPayload.builder()
                .name("test name")
                .productUUID(new GenerateRandomString().generateRandomUuid())
                .description("test description")
                .basePrice(setBigDecimalWithScale(100))
                .finalPrice(setBigDecimalWithScale(90))
                .discountPercentage(10)
                .hasDiscount(true)
                .amount(5)
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();
    }

    public static ProductPayload ProductWithDiscount(String name, String uuid) {
        return ProductPayload.builder()
                .name(name)
                .productUUID(uuid)
                .description("test description")
                .basePrice(setBigDecimalWithScale(100))
                .finalPrice(setBigDecimalWithScale(90))
                .discountPercentage(10)
                .hasDiscount(true)
                .amount(5)
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();
    }

    public static ProductPayload ProductWithDiscount(int discountPercentage) {
        return ProductPayload.builder()
                .name("test name")
                .productUUID(new GenerateRandomString().generateRandomUuid())
                .description("test description")
                .basePrice(setBigDecimalWithScale(100))
                .finalPrice(setBigDecimalWithScale(90))
                .discountPercentage(discountPercentage)
                .hasDiscount(true)
                .amount(5)
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();
    }

    public static ProductPayload ProductWithDiscount(BigDecimal finalPrice) {
        return ProductPayload.builder()
                .name("test name")
                .productUUID(new GenerateRandomString().generateRandomUuid())
                .description("test description")
                .basePrice(finalPrice)
                .finalPrice(finalPrice)
                .discountPercentage(0)
                .hasDiscount(true)
                .amount(5)
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();
    }

    public static ProductPayload ProductWithDiscount(BigDecimal basePrice, BigDecimal finalPrice) {
        return ProductPayload.builder()
                .name("test name")
                .productUUID(new GenerateRandomString().generateRandomUuid())
                .description("test description")
                .basePrice(basePrice)
                .finalPrice(finalPrice)
                .discountPercentage(0)
                .hasDiscount(true)
                .amount(5)
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();
    }

    public static ProductPayload ProductWithDiscount(Availability availability) {
        return ProductPayload.builder()
                .name("test name")
                .productUUID(new GenerateRandomString().generateRandomUuid())
                .description("test description")
                .basePrice(setBigDecimalWithScale(100))
                .finalPrice(setBigDecimalWithScale(90))
                .discountPercentage(10)
                .hasDiscount(true)
                .amount(5)
                .categories(Categories.PHONES)
                .availability(availability)
                .isActive(true)
                .build();
    }

    public static ProductPayload ProductWithDiscount(String uuid, int amount) {
        return ProductPayload.builder()
                .name("test name")
                .productUUID(uuid)
                .description("test description")
                .basePrice(setBigDecimalWithScale(100))
                .finalPrice(setBigDecimalWithScale(90))
                .discountPercentage(10)
                .hasDiscount(true)
                .amount(amount)
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();
    }

    public static ProductPayload ProductWithDiscount(String uuid) {
        return ProductPayload.builder()
                .name("product name")
                .productUUID(uuid)
                .description("test description")
                .basePrice(setBigDecimalWithScale(100))
                .finalPrice(setBigDecimalWithScale(90))
                .discountPercentage(10)
                .hasDiscount(true)
                .amount(5)
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();
    }

    public static ProductPayload ProductWithoutDiscount() {
        return ProductPayload.builder()
                .name("test name")
                .productUUID(new GenerateRandomString().generateRandomUuid())
                .description("test description")
                .basePrice(setBigDecimalWithScale(100))
                .finalPrice(setBigDecimalWithScale(100))
                .discountPercentage(0)
                .hasDiscount(false)
                .amount(5)
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();
    }

    public static ProductPayload ProductWithoutDiscount(String uuid) {
        return ProductPayload.builder()
                .name("name")
                .productUUID(uuid)
                .description("test description")
                .basePrice(setBigDecimalWithScale(100))
                .finalPrice(setBigDecimalWithScale(100))
                .discountPercentage(0)
                .hasDiscount(false)
                .amount(5)
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();
    }

    public static ProductPayload ProductWithoutDiscount(String name, String uuid) {
        return ProductPayload.builder()
                .name(name)
                .productUUID(uuid)
                .description("test description")
                .basePrice(setBigDecimalWithScale(100))
                .finalPrice(setBigDecimalWithScale(100))
                .discountPercentage(0)
                .hasDiscount(false)
                .amount(5)
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();
    }

    protected static BigDecimal setBigDecimalWithScale(long val) {
        return BigDecimal.valueOf(val).setScale(2, RoundingMode.HALF_UP);
    }
}
