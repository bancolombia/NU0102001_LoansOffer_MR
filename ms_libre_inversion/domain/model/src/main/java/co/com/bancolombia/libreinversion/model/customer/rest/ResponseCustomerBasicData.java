package co.com.bancolombia.libreinversion.model.customer.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCustomerBasicData {

    private String typeCustomer;
    private String fullName;
    private String role;
    private String vinculationState;
    private String vinculationDate;
    private String dateLastUpdate;
    private String customerStatus;
    private String relatedType;
    private String specialDial;
}
