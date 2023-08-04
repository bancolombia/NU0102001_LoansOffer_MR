package co.com.bancolombia.utilities.api.exception;

import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import co.com.bancolombia.logging.technical.message.ObjectTechMsg;
import co.com.bancolombia.utilities.model.Error;
import co.com.bancolombia.utilities.model.Failure;
import co.com.bancolombia.utilities.model.error.ErrorLog;
import co.com.bancolombia.utilities.model.exceptions.AmortizationBusinessException;
import co.com.bancolombia.utilities.model.exceptions.AmortizationInternalException;
import co.com.bancolombia.utilities.model.utils.Constant;
import co.com.bancolombia.utilities.model.utils.ExceptionEnum;
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
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    private static final TechLogger logger = LoggerFactory.getLog("GlobalWebExceptionHandler");

    public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties webProperties,
                                          ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        this.setMessageWriters(configurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {

        Throwable error = getError(request);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (error instanceof AmortizationBusinessException) {
            status = HttpStatus.NOT_FOUND;
        }

        Map<String, Object> errorPropertiesMap = getErrorAttributes(request, ErrorAttributeOptions.defaults());

        return ServerResponse.status(status).contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorPropertiesMap));
    }

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> map = new HashMap<>();
        Throwable error = getError(request);
        String message;

        if (error instanceof WebClientException || error instanceof ResponseStatusException) {
            message = error.getMessage();
            ErrorLog errorLog = ErrorLog.builder().detailError(ExceptionEnum.LI000.getMessage() + " - " + message)
                    .errorCode(ExceptionEnum.LI000.name()).build();
            AmortizationInternalException ex = new AmortizationInternalException(errorLog);
            Failure failure = getInternalErrorFromException(ex);
            logger.error(message);
            map.put(Constant.ERRORS, failure.getErrors());
        } else if (error instanceof AmortizationBusinessException) {
            AmortizationBusinessException ex = (AmortizationBusinessException) error;
            Failure failure = getBusinessErrorFromException(ex);
            ObjectTechMsg<Object> objectTechMsg = ObjectTechMsg.builder().appName(Constant.APP_NAME)
                    .componentName(Constant.COMPONENT_NAME).serviceName(ex.getServiceName())
                    .actionName(ex.getActionName()).transactionId(ex.getMessageId())
                    .message(failure.getErrors()).build();
            logger.error(objectTechMsg);
            map.put(Constant.ERRORS, failure.getErrors());
        } else {
            AmortizationInternalException ex = (AmortizationInternalException) error;
            Failure failure = getInternalErrorFromException(ex);
            ObjectTechMsg<Object> objectTechMsg = ObjectTechMsg.builder().appName(Constant.APP_NAME)
                    .componentName(Constant.COMPONENT_NAME).serviceName(ex.getServiceName())
                    .actionName(ex.getActionName()).transactionId(ex.getMessageId())
                    .message(failure.getErrors()).build();
            logger.error(objectTechMsg);
            map.put(Constant.ERRORS, failure.getErrors());
        }
        return map;
    }

    private Failure getBusinessErrorFromException(AmortizationBusinessException ex) {
        List<Error> errors = new ArrayList<>();
        errors.add(Error.builder().code(ex.getErrorCode()).detail(ex.getDetailError()).build());
        return Failure.builder().errors(errors).build();
    }

    private Failure getInternalErrorFromException(AmortizationInternalException ex) {
        List<Error> errors = new ArrayList<>();
        errors.add(Error.builder().code(ex.getErrorCode()).detail(ex.getDetailError()).build());
        return Failure.builder().errors(errors).build();
    }
}
