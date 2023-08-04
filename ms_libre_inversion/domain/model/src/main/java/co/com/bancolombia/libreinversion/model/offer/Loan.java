package co.com.bancolombia.libreinversion.model.offer;

import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.DirectDebit;
import co.com.bancolombia.libreinversion.model.request.DisbursementDestination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Loan {

    @Schema(required = true)
    private String signingCode;
    @Schema(required = true)
    private Integer paymentDay;
    private String referralOfficer;
    private String advisoryCode;
    private String promotionalCode;
    private String branchOffice;
    @Schema(required = true)
    private DirectDebit directDebit;
    @Schema(required = true)
    private DisbursementDestination disbursementDestination;
}
