package testdata.payload;

import practice.store.product.Availability;
import practice.store.product.Categories;
import practice.store.product.ProductPayload;
import practice.store.utils.values.GenerateRandomString;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class TestDataProductPayload {

    public static ProductPayload Product(long id, String name, String productUUID, String description, BigDecimal basePrice, BigDecimal amountPriceReduction, BigDecimal finalPrice, int discountPercentage, boolean hasDiscount, int amountInStock, Categories categories, Availability availability, boolean isActive) {
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

    public static ProductPayload ProductWithDiscount() {
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

    public static ProductPayload ProductWithDiscount(long id, String uuid) {
        return ProductPayload.builder()
                .id(id)
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

    public static ProductPayload ProductWithDiscount(long id, String name, String uuid) {
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

    public static ProductPayload ProductWithoutDiscount() {
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

    public static ProductPayload ProductWithoutDiscount(long id, String name, String uuid) {
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

    public static ProductPayload ProductWithoutDiscount(long id, String uuid) {
        return ProductPayload.builder()
                .id(id)
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

    protected static BigDecimal setBigDecimalWithScale(long val) {
        return BigDecimal.valueOf(val).setScale(2, RoundingMode.HALF_UP);
    }
}
