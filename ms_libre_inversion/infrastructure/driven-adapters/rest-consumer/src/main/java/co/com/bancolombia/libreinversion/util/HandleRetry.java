package co.com.bancolombia.libreinversion.util;


import co.com.bancolombia.libreinversion.model.commons.ErrorEnum;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

public class HandleRetry {

    private static final int NUMBER_ONE = 1;
    public static RetryBackoffSpec retry(String messageId, ErrorEnum error) {
        return Retry.backoff(NUMBER_ONE, Duration.ofSeconds(NUMBER_ONE))
                .filter(throwable -> throwable instanceof LibreInversionException
                        || throwable instanceof WebClientException)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> new LibreInversionException(
                        error.getName(), error.getName(), error.getMessage(), "", "", messageId));
    }
}
