package practice.pdfservice.rabbit.mail;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@Getter
public class SenderMailPayload {

    private String orderUUID;
    private byte[] fileData;
    private String email;
}
