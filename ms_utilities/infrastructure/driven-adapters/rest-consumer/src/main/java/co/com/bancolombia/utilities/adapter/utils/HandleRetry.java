package co.com.bancolombia.utilities.adapter.utils;

import co.com.bancolombia.utilities.model.exceptions.AmortizationInternalException;
import co.com.bancolombia.utilities.model.utils.ExceptionEnum;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

public class HandleRetry {

    private static final int NUMBER_ONE = 1;

    private HandleRetry() {
        throw new IllegalStateException("Utility class");
    }

    public static RetryBackoffSpec retry(String messageId, ExceptionEnum error) {
        return Retry.backoff(NUMBER_ONE, Duration.ofSeconds(1))
                .filter(HandleRetry::filterThrowable)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> new AmortizationInternalException(
                        error.name(), error.name(), error.getMessage(), "", "", messageId));
    }

    private static boolean filterThrowable(Throwable throwable) {
        return throwable instanceof AmortizationInternalException || throwable instanceof WebClientException;
    }
}
