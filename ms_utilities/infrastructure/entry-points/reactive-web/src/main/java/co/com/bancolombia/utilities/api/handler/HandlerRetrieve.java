package co.com.bancolombia.utilities.api.handler;

import co.com.bancolombia.utilities.model.Customer;
import co.com.bancolombia.utilities.model.Identification;
import co.com.bancolombia.utilities.model.Offer;
import co.com.bancolombia.utilities.model.RequestAmortization;
import co.com.bancolombia.utilities.model.ResponseAmortization;
import co.com.bancolombia.utilities.model.exceptions.AmortizationBusinessException;
import co.com.bancolombia.utilities.model.utils.Constant;
import co.com.bancolombia.utilities.model.utils.ExceptionEnum;
import co.com.bancolombia.utilities.usecase.AmortizationUseCase;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HandlerRetrieve {

    private final AmortizationUseCase amortizationUseCase;

    @NonNull
    public Mono<ServerResponse> listenRetrieve(ServerRequest serverRequest) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(getResponse(serverRequest), ResponseAmortization.class)
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    private Mono<ResponseAmortization> getResponse(ServerRequest request) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constant.HEADER_ID, request.headers().firstHeader(Constant.HEADER_ID));
        headers.put(Constant.DEVICE_ID, request.headers().firstHeader(Constant.DEVICE_ID));
        headers.put(Constant.TOKEN, request.headers().firstHeader(Constant.TOKEN));
        headers.put(Constant.TIMESTAMP, request.headers().firstHeader(Constant.TIMESTAMP));
        headers.put(Constant.CONSUMER, request.headers().firstHeader(Constant.CONSUMER));

        String messageId = headers.get(Constant.HEADER_ID);

        return request.bodyToMono(RequestAmortization.class)
                .single()
                .filter(requestBody -> {
                    Customer customer = requestBody.getData().getCustomer();
                    Identification identification = customer.getIdentification();
                    return identification != null && !StringUtils.isEmpty(identification.getType())
                            && !StringUtils.isEmpty(identification.getNumber())
                            && !StringUtils.isEmpty(customer.getCustomerReliability());
                })
                .switchIfEmpty(Mono.error(new AmortizationBusinessException(ExceptionEnum.LI014.name(),
                        ExceptionEnum.LI014.name(), ExceptionEnum.LI014.getMessage(), "", "", "")))

                .filter(requestBody -> {
                    Offer offer = requestBody.getData().getOffer();
                    return offer != null && offer.getAmount() != null
                            && offer.getAmount().compareTo(BigDecimal.ZERO) != 0;
                })
                .switchIfEmpty(Mono.error(new AmortizationBusinessException(ExceptionEnum.LI015.name(),
                        ExceptionEnum.LI015.name(), ExceptionEnum.LI015.getMessage(), "", "", "")))

                .filter(requestBody -> {
                    Integer term = requestBody.getData().getOffer().getTerm();
                    return term != null && term.compareTo(0) != 0;
                })
                .switchIfEmpty(Mono.error(new AmortizationBusinessException(ExceptionEnum.LI016.name(),
                        ExceptionEnum.LI016.name(), ExceptionEnum.LI016.getMessage(), "", "", "")))

                .filter(requestBody -> requestBody.getData().getOffer().getInsurances() != null)
                .switchIfEmpty(Mono.error(new AmortizationBusinessException(ExceptionEnum.LI017.name(),
                        ExceptionEnum.LI017.name(), ExceptionEnum.LI017.getMessage(), "", "", "")))

                .filter(requestBody -> requestBody.getData().getOffer().getInsurances().getInsurance() != null)
                .switchIfEmpty(Mono.error(new AmortizationBusinessException(ExceptionEnum.LI017.name(),
                        ExceptionEnum.LI017.name(), ExceptionEnum.LI017.getMessage(), "", "", "")))

                .filter(requestBody -> requestBody.getData().getOffer().getInsurances().getInsurance()
                        .contains(Constant.INSURANCE_REQUEST_SV))
                .switchIfEmpty(Mono.error(new AmortizationBusinessException(ExceptionEnum.LI017.name(),
                        ExceptionEnum.LI017.name(), ExceptionEnum.LI017.getMessage(), "", "", "")))

                .filter(requestBody -> requestBody.getData().getOffer().getAmount().compareTo(Constant.MIN_01) >= 0
                        && requestBody.getData().getOffer().getAmount().compareTo(Constant.MAX_05) <= 0)
                .switchIfEmpty(Mono.error(new AmortizationBusinessException(ExceptionEnum.LI018.name(),
                        ExceptionEnum.LI018.name(), ExceptionEnum.LI018.getMessage(), "", "", "")))

                .filter(requestBody -> requestBody.getData().getOffer().getProductId()
                        .equals(Constant.MAP_PROCUCT_CODE))
                .switchIfEmpty(Mono.error(new AmortizationBusinessException(ExceptionEnum.LI020.name(),
                        ExceptionEnum.LI020.name(), ExceptionEnum.LI020.getMessage(), "", "", "")))

                .filter(requestBody -> requestBody.getData().getOffer().getInterestRateType().equals(Constant.RATE_TYPE))
                .switchIfEmpty(Mono.error(new AmortizationBusinessException(ExceptionEnum.LI021.name(),
                        ExceptionEnum.LI021.name(), ExceptionEnum.LI021.getMessage(), "", "", "")))

                .flatMap(requestBody -> amortizationUseCase.retrieveAmortization(requestBody, messageId));
    }
}
