package co.com.bancolombia.libreinversion.model.customer;

import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerContact;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerDetail;
import co.com.bancolombia.libreinversion.model.notification.rest.ResponseRetrieveInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ValidateOccupation {

    Integer age;
    ResponseCustomerDetail detail;
    ResponseCustomerContact contact;
    ResponseRetrieveInfo alerts;
    String productCode;
    String messageId;

}
