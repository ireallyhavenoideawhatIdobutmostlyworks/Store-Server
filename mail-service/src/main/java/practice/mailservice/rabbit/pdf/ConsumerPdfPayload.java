package practice.mailservice.rabbit.pdf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ConsumerPdfPayload {

    private String orderUUID;
    private byte[] fileData;
    private String email;
}
