package practice.store.order.rabbit.pdfService.detaills;

import lombok.Builder;

@Builder
public class CustomerPdfDetails {

    private String username;
    private String email;
    private String postalCode;
    private String street;
    private String city;
}
