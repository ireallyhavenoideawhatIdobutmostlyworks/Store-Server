package practice.pdfservice.rabbit.store.detaills;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerPdfDetails {

    private String username;
    private String email;
    private String postalCode;
    private String street;
    private String city;
}
