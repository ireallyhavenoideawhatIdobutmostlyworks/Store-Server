package practice.pdfservice.rabbit.store.detaills;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@Getter
public class CustomerPdfDetails {

    private String username;
    private String email;
    private String postalCode;
    private String street;
    private String city;
}
