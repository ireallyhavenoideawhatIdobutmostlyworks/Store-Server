package practice.store.order.rabbit.pdfService.detaills;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class CustomerPdfDetails {

    @JsonProperty
    private String username;
    @JsonProperty
    private String email;
    @JsonProperty
    private String postalCode;
    @JsonProperty
    private String street;
    @JsonProperty
    private String city;
}
