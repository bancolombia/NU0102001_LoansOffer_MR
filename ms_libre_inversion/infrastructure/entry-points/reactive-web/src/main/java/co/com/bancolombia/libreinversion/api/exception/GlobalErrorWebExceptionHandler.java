package co.com.bancolombia.libreinversion.api.exception;


import co.com.bancolombia.libreinversion.model.error.ErrorResponseApi;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import co.com.bancolombia.logging.technical.message.ObjectTechMsg;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    private static TechLogger techLogger = LoggerFactory.getLog("loansOffer");
    private static final String MESSAGE = "message";

    public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties webProperties,
                                          ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        this.setMessageWriters(configurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {

        Map<String, Object> errorPropertiesMap = getErrorAttributes(request, ErrorAttributeOptions.defaults());

        return ServerResponse.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorPropertiesMap));
    }

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request,
                                                  ErrorAttributeOptions options) {

        Map<String, Object> map = new HashMap<>();
        Throwable error = getError(request);
        String message = "";

        if (error instanceof WebExchangeBindException) {
            message = error.getMessage();
            map.put(MESSAGE, message);
            techLogger.error(message);
        }

        if (error instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = ((ResponseStatusException) error);
            message = responseStatusException.getReason() != null ? responseStatusException.getReason()
                    : responseStatusException.getStatus().getReasonPhrase();
            map.put(MESSAGE, message);
            techLogger.error(message);
        }

        if (error instanceof WebClientException) {
            message = ((WebClientException) error).getMessage();
            map.put(MESSAGE, message);
            techLogger.error(message);
        }

        return map;
    }
}
