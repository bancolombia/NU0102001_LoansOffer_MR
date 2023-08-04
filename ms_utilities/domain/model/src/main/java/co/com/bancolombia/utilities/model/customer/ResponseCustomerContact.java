package co.com.bancolombia.utilities.model.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCustomerContact {
    private Meta meta;
    private ResponseCustomerContactData data;
    private TopLevelLinksManagement links;
}
