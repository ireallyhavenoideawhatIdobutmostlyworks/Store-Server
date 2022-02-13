package practice.mailservice.rabbit.payloads.pdf;

import lombok.*;
import practice.mailservice.rabbit.payloads.ConsumerPayload;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@Getter
public final class ConsumerPdfPayload implements ConsumerPayload {

    private String orderUUID;
    private byte[] fileData;
    private String email;
}
