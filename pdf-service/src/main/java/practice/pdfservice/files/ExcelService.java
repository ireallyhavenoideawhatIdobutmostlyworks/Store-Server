package practice.pdfservice.files;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import practice.pdfservice.rabbit.store.ConsumerStorePayload;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

@PropertySource({
        "classpath:seller.properties",
        "classpath:excel.properties"
})
@Service
public class ExcelService {

    @Value("${seller.name}")
    private String sellerName;
    @Value("${seller.nip}")
    private String sellerNip;
    @Value("${seller.city}")
    private String sellerCity;
    @Value("${seller.city.postal.code}")
    private String sellerCityPostalCode;
    @Value("${seller.street}")
    private String sellerStreet;
    @Value("${seller.street.number}")
    private String sellerStreetNumber;
    @Value("${seller.mobile.number}")
    private String sellerMobileNumber;
    @Value("${seller.email}")
    private String sellerEmail;

    @Value("${output.excel.path}")
    private String outputExcelPath;


    public void createExcelFile(ConsumerStorePayload consumerStorePayload) {
        Workbook invoiceAsExcel = new HSSFWorkbook();
        String outputPath = String.format(outputExcelPath, consumerStorePayload.getOrderPdfDetails().getOrderUUID());

        OutputStream os = null;
        try {
            os = new FileOutputStream(outputPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // ToDo add logger.error
        }

        Sheet sheet = invoiceAsExcel.createSheet("New Sheet");

        createRowsAndCells(sheet);
        addHeader(sheet);
        addSellerAndBuyer(sheet, consumerStorePayload);
        addProductHeader(sheet);
        addProductContent(sheet, consumerStorePayload);

        try {
            invoiceAsExcel.write(os);
            invoiceAsExcel.close();
        } catch (IOException e) {
            e.printStackTrace();
            // ToDo add logger.error
        }
    }


    private void createRowsAndCells(Sheet sheet) {
        for (int i = 0; i < 50; i++) {
            sheet.createRow(i);
            for (int a = 0; a < 10; a++) {
                sheet.getRow(i).createCell(a);
            }
        }
    }

    private void addHeader(Sheet sheet) {
        setValueToCell(sheet, 2, Headers.CITY.getCellPosition(), Headers.CITY.getCellValue());
        setValueToCell(sheet, 3, 5, sellerCity);
        setValueToCell(sheet, 4, Headers.INVOICE_DATE.getCellPosition(), Headers.INVOICE_DATE.getCellValue());
        setValueToCell(sheet, 5, 5, LocalDateTime.now().toLocalDate().toString());
        setValueToCell(sheet, 6, Headers.SELL_DATE.getCellPosition(), Headers.SELL_DATE.getCellValue());
        setValueToCell(sheet, 7, 5, LocalDateTime.now().toLocalDate().toString());
    }

    private void addSellerAndBuyer(Sheet sheet, ConsumerStorePayload consumerStorePayload) {
        setValueToCell(sheet, 11, Headers.SELL_DATE.getCellPosition(), Headers.SELL_DATE.getCellValue());
        setValueToCell(sheet, 12, 5, sellerName);
        setValueToCell(sheet, 13, 5, sellerNip);
        setValueToCell(sheet, 14, 5, String.format("%s %s", sellerStreet, sellerStreetNumber));
        setValueToCell(sheet, 15, 5, String.format("%s %s", sellerCityPostalCode, sellerCity));

        setValueToCell(sheet, 11, Headers.BUYER.getCellPosition(), Headers.BUYER.getCellValue());
        setValueToCell(sheet, 12, 1, consumerStorePayload.getCustomerPdfDetails().getUsername());
        setValueToCell(sheet, 13, 1, consumerStorePayload.getCustomerPdfDetails().getStreet());
        setValueToCell(sheet, 14, 1, String.format("%s %s", consumerStorePayload.getCustomerPdfDetails().getPostalCode(), consumerStorePayload.getCustomerPdfDetails().getCity()));
    }

    private void addProductHeader(Sheet sheet) {
        setValueToCell(sheet, 18, Headers.POSITION.getCellPosition(), Headers.POSITION.getCellValue());
        setValueToCell(sheet, 18, Headers.NAME.getCellPosition(), Headers.NAME.getCellValue());
        setValueToCell(sheet, 18, Headers.PRODUCT_UUID.getCellPosition(), Headers.PRODUCT_UUID.getCellValue());
        setValueToCell(sheet, 18, Headers.DESCRIPTION.getCellPosition(), Headers.DESCRIPTION.getCellValue());
        setValueToCell(sheet, 18, Headers.FINAL_PRICE.getCellPosition(), Headers.FINAL_PRICE.getCellValue());
    }

    private void addProductContent(Sheet sheet, ConsumerStorePayload consumerStorePayload) {
        int productFieldsSize = consumerStorePayload.getProductPdfDetailsList().get(0).getClass().getDeclaredFields().length;
        int sizeOfProductFieldsWithPositionCell = productFieldsSize + 1;
        int rowStart = 19;

        for (int i = 1; i <= consumerStorePayload.getProductPdfDetailsList().size(); i++) {

            for (int a = 1; a < sizeOfProductFieldsWithPositionCell + 1; a++) {
                if (a == Headers.POSITION.getCellPosition()) {
                    setValueToCell(sheet, rowStart, a, String.valueOf(i));
                }
                if (a == Headers.NAME.getCellPosition()) {
                    setValueToCell(sheet, rowStart, a, consumerStorePayload.getProductPdfDetailsList().get(i - 1).getName());
                }
                if (a == Headers.PRODUCT_UUID.getCellPosition()) {
                    setValueToCell(sheet, rowStart, a, consumerStorePayload.getProductPdfDetailsList().get(i - 1).getProductUUID());
                }
                if (a == Headers.DESCRIPTION.getCellPosition()) {
                    setValueToCell(sheet, rowStart, a, consumerStorePayload.getProductPdfDetailsList().get(i - 1).getDescription());
                }
                if (a == Headers.FINAL_PRICE.getCellPosition()) {
                    String finalPrice = String.valueOf(consumerStorePayload.getProductPdfDetailsList().get(i - 1).getFinalPrice());
                    setValueToCell(sheet, rowStart, a, finalPrice);
                }
            }
            rowStart++;
        }

        setValueToCell(sheet, rowStart + consumerStorePayload.getProductPdfDetailsList().size(), sizeOfProductFieldsWithPositionCell, "Order Price");
        setValueToCell(sheet, rowStart + consumerStorePayload.getProductPdfDetailsList().size() + 1, sizeOfProductFieldsWithPositionCell, String.valueOf(consumerStorePayload.getOrderPdfDetails().getOrderPrice()));
    }

    private void setValueToCell(Sheet sheet, int rowPosition, int cellPosition, String content) {
        sheet
                .getRow(rowPosition)
                .getCell(cellPosition)
                .setCellValue(content);
    }
}
