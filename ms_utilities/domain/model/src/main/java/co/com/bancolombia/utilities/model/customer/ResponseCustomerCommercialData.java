package co.com.bancolombia.utilities.model.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCustomerCommercialData {
    private String mdmKey;
    private String fullName;
    private String role;
    private String customerStatus;
    private String businessManagerCode;
    private String managerCode;
    private String manager;
    private String vinculationOffice;
    private String segment;
    private String subSegment;
    private String marketNiche;
}
