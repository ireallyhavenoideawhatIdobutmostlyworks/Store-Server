package practice.pdfservice.files;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Headers {

    CITY(5, "City"),
    INVOICE_DATE(5, "Invoice date"),
    SELL_DATE(5, "Sale date"),

    SELLER(5, "Seller"),
    BUYER(1, "Buyer"),

    POSITION(1, "Pos"),
    NAME(2, "Name"),
    PRODUCT_UUID(3, "Product UUID"),
    DESCRIPTION(4, "Description"),
    FINAL_PRICE(5, "Final price");

    private int cellPosition;
    private String cellValue;
}
