package co.com.bancolombia.libreinversion.model.customer;

import co.com.bancolombia.libreinversion.model.account.rest.Identification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    @Schema(required = true)
    private Identification identification;
    private String companyIdType;
    private String companyIdNumber;
}
