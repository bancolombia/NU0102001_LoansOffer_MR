package co.com.bancolombia.libreinversion.model.customer.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCustomerContact {

    private ResponseCustomerContactData  data;
    private MetaData meta;
}
