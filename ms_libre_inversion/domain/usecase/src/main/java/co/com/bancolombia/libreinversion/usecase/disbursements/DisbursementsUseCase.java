package co.com.bancolombia.libreinversion.usecase.disbursements;

import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.DisbursementRQ;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.response.DisbursementRS;
import co.com.bancolombia.libreinversion.model.loansdisbursements.gateway.DisbursementsGateWay;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DisbursementsUseCase {

    private final DisbursementsGateWay disbursementsGateWay;
    private static TechLogger log = LoggerFactory.getLog(DisbursementsUseCase.class.getName());

    public Mono<DisbursementRS> execcuteDisbursement(DisbursementRQ disbursement, String messageId) {
        return disbursementsGateWay.requestDisbursement(disbursement, messageId).flatMap(s -> {
            log.info(s);
            return Mono.just(s);
        });
    }
}
