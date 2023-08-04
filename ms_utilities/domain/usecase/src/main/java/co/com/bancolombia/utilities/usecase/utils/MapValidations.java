package co.com.bancolombia.utilities.usecase.utils;

import co.com.bancolombia.utilities.model.exceptions.AmortizationInternalException;
import co.com.bancolombia.utilities.model.gateways.MapGateway;
import co.com.bancolombia.utilities.model.product.Attribute;
import co.com.bancolombia.utilities.model.product.RuleRequest;
import co.com.bancolombia.utilities.model.product.RuleResponse;
import co.com.bancolombia.utilities.model.utils.Constant;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class MapValidations {

    private final MapGateway mapGateway;

    public Mono<RuleResponse> validateRuleRuleOccupationSDE(String age, String occupation,
                                                            boolean phone, boolean email) {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(Attribute.builder().name(Constant.MAP_ATTRIBUTE_AGE).value(age).build());
        attributes.add(Attribute.builder().name(Constant.MAP_ATTRIBUTE_OCCUPATION).value(occupation).build());
        attributes.add(Attribute.builder().name(Constant.MAP_ATTRIBUTE_PHONE)
                .value(phone ? Constant.MAP_VALUE_VERDADERO : Constant.MAP_VALUE_FALSO).build());
        attributes.add(Attribute.builder().name(Constant.MAP_ATTRIBUTE_MAIL)
                .value(email ? Constant.MAP_VALUE_VERDADERO : Constant.MAP_VALUE_FALSO).build());

        RuleRequest ruleRequest = RuleRequest.builder()
                .ruleName(Constant.MAP_REGLA_SEGURO_DESEMPLEO)
                .productCode(Constant.MAP_PROCUCT_CODE)
                .attributes(attributes)
                .build();

        return mapGateway.ruleValidate(ruleRequest);
    }

    public boolean validateRuleResponse(boolean b, String code, String detail) {
        if (b) {
            showException(code, detail);
        }
        return true;
    }

    private void showException(String code, String detail) {
        throw new AmortizationInternalException(code, code, detail, "", "", "");
    }
}
