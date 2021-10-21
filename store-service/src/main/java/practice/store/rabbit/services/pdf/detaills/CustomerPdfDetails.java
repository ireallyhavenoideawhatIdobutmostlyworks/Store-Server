package practice.store.rabbit.services.pdf.detaills;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;

@ToString
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
