package co.com.bancolombia.libreinversion.call;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.commons.ErrorEnum;
import co.com.bancolombia.libreinversion.model.constants.SellConst;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.libreinversion.request.SoapEnvelopeRequest;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Data
public class CallSoap<T, R> {

    private static final TechLogger logger = LoggerFactory.getLog(CallSoap.class.getName());
    private T objRequest;
    private R objResponse;

    public Mono<Object> call(WebClient webClient, String soapServiceUrl, String soapHeaderContent) {

        SoapEnvelopeRequest soapEnvelopeRequest = new SoapEnvelopeRequest(soapHeaderContent, objRequest);

        return (Mono<Object>) webClient.post()
                .uri(soapServiceUrl)
                .contentType(MediaType.TEXT_XML)
                .body(Mono.just(soapEnvelopeRequest), SoapEnvelopeRequest.class)
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(errorResponseBody -> Mono.error(
                                new ResponseStatusException(clientResponse.statusCode(), errorResponseBody))))
                .bodyToMono(objResponse.getClass())
                .doOnError(ResponseStatusException.class, logger::error)
                .doOnError(Exception.class, logger::error)
                .doOnSuccess(logger::info)
                .switchIfEmpty(Mono.error(() -> new LibreInversionException(
                        Constant.DECEVAL_CLIENT_ERROR, ErrorEnum.MSG_LI015.getMessage(),
                        ErrorEnum.MSG_LI015.getMessage(),
                        SellConst.SELL_OFFER, "call-soap", "")));
    }
}
