package co.com.bancolombia.libreinversion.model.customer.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRQ {

    private String customerDocumentType;
    private String customerDocumentId;
    private String queryType;
}
