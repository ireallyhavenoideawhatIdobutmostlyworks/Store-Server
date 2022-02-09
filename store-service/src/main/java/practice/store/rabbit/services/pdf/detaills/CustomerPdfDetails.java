package practice.store.rabbit.services.pdf.detaills;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;

@ToString
@Builder
public final class CustomerPdfDetails {

    @JsonProperty
    private final String username;
    @JsonProperty
    private final String email;
    @JsonProperty
    private final String postalCode;
    @JsonProperty
    private final String street;
    @JsonProperty
    private final String city;
}
