package co.com.bancolombia.utilities.model.gateways;

import co.com.bancolombia.utilities.model.product.RuleRequest;
import co.com.bancolombia.utilities.model.product.RuleResponse;
import reactor.core.publisher.Mono;

public interface MapGateway {
    Mono<RuleResponse> ruleValidate(RuleRequest ruleRequest);
}