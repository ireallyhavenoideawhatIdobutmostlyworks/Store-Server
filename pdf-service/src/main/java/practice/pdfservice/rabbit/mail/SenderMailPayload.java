package practice.pdfservice.rabbit.mail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SenderMailPayload {

    private String orderUUID;
    private byte[] fileData;
    private String email;
}
