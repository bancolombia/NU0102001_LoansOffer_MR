package co.com.bancolombia.libreinversion.model.offer.sellutil;

import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.response.DisbursementRS;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CacheSell {
    private boolean isFirmarPagare;
    private boolean isCreatrePagare;
    private boolean isChangeOpportunities;
    private boolean isExecDisbursement;
    private DisbursementRS disbursementRS;
}
