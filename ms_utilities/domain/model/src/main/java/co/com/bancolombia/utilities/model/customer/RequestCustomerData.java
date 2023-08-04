package co.com.bancolombia.utilities.model.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RequestCustomerData {
    private String customerDocumentType;
    private String customerDocumentId;
    private String queryType;
}