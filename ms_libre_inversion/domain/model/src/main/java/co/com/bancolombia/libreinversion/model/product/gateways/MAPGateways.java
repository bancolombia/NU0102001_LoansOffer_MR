package co.com.bancolombia.libreinversion.model.product.gateways;

import co.com.bancolombia.libreinversion.model.product.RuleRequest;
import co.com.bancolombia.libreinversion.model.product.RuleResponse;
import co.com.bancolombia.libreinversion.model.product.Parameter;
import co.com.bancolombia.libreinversion.model.product.Amount;
import co.com.bancolombia.libreinversion.model.product.City;
import reactor.core.publisher.Mono;

import java.util.List;

public interface MAPGateways {

    Mono<RuleResponse> ruleValidate(RuleRequest requestBodyData);
    Mono<RuleResponse> ruleValidateIncreaseMemory(RuleRequest request);
    Mono<Parameter> getParameterByName(String name);
    Mono<List<Amount>> getAmounts();
    Mono<RuleResponse> getTimeOffer();
    Mono<List<City>> getAllowedCities();
}
