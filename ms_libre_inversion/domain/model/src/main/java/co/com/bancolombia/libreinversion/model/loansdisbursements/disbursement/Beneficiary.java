package co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement;

import io.swagger.v3.oas.annotations.media.Schema;
import co.com.bancolombia.libreinversion.model.account.rest.Identification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Beneficiary {
    private String fullName;
    @Schema(required = true)
    private Identification identification;
}
