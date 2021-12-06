package practice.mailservice.rabbit.payloads.pdf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import practice.mailservice.rabbit.payloads.ConsumerPayload;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ConsumerPdfPayload implements ConsumerPayload {

    private String orderUUID;
    private byte[] fileData;
    private String email;
}
