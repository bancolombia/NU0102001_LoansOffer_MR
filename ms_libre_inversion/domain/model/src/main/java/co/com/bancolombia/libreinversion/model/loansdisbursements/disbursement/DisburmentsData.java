package co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DisburmentsData {
    private String trackingId;
    private String disbursementAppilicationId;
    private LoanDM loan;
    private DisbursementDestinationDbm disbursementDestination;
}
