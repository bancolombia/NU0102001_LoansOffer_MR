package co.com.bancolombia.utilities.model.gateways;

import co.com.bancolombia.utilities.model.interestrate.RequestInterestRate;
import co.com.bancolombia.utilities.model.interestrate.ResponseInterestRate;
import reactor.core.publisher.Mono;

public interface InterestRateGateway {

    Mono<ResponseInterestRate> retrieveInterestRate(RequestInterestRate requestBody, String msgId);
}
