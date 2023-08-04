package co.com.bancolombia.utilities.model.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCustomerCommercial {
    private Meta meta;
    private ResponseCustomerCommercialData data;
    private TopLevelLinksManagement links;
}
