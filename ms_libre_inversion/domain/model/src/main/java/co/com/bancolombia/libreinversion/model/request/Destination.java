package co.com.bancolombia.libreinversion.model.request;


import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.Beneficiary;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Destination {

    @Schema(required = true)
    private double amount;

    @Schema(required = true)
    private String destinationType;

    @Schema(required = true)
    private String destinationId;

    private Beneficiary beneficiary;
}
