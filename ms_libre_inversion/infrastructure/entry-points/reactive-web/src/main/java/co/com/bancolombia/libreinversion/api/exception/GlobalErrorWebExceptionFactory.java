package co.com.bancolombia.libreinversion.api.exception;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.error.ErrorData;
import co.com.bancolombia.libreinversion.model.error.ErrorResponseApi;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.logging.technical.message.ObjectTechMsg;

import java.util.ArrayList;
import java.util.List;

public class GlobalErrorWebExceptionFactory {

    private GlobalErrorWebExceptionFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static ErrorResponseApi getErrorApiFromException(LibreInversionException ex) {
        List<ErrorData> errors = new ArrayList<>();
        errors.add(ErrorData.builder().code(ex.getErrorCode()).detail(ex.getDetailError()).build());
        return ErrorResponseApi.builder().errors(errors).build();
    }

    public static ObjectTechMsg<Object> mapper(ErrorResponseApi errorResponseApi, LibreInversionException ex) {
        return ObjectTechMsg.builder().appName(Constant.APP_NAME)
                .componentName(Constant.COMPONENT_NAME).serviceName(ex.getServiceName())
                .actionName(ex.getActionName()).transactionId(ex.getMessageId())
                .message(errorResponseApi.getErrors()).build();
    }
}
