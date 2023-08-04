package co.com.bancolombia.libreinversion.model.loansdisbursements.gateway;

import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.DisbursementRQ;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.response.DisbursementRS;
import reactor.core.publisher.Mono;

public interface DisbursementsGateWay {

    Mono<DisbursementRS> requestDisbursement(DisbursementRQ disbursement, String msgId);
}
