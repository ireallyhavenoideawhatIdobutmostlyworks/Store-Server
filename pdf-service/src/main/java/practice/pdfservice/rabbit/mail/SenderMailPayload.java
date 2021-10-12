package practice.pdfservice.rabbit.mail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class SenderMailPayload {

    @JsonProperty
    private String orderUUID;
    @JsonProperty
    private byte[] fileData;
}
