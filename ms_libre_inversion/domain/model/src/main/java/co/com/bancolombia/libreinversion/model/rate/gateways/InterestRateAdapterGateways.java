package co.com.bancolombia.libreinversion.model.rate.gateways;

import co.com.bancolombia.libreinversion.model.rate.LoanInteresRateRQ;
import co.com.bancolombia.libreinversion.model.rate.LoanInteresRateResponse;
import reactor.core.publisher.Mono;

public interface InterestRateAdapterGateways {

    Mono<LoanInteresRateResponse> callLoanInteresRate(LoanInteresRateRQ request,
                                                      String msgId, String idNumber);
}
