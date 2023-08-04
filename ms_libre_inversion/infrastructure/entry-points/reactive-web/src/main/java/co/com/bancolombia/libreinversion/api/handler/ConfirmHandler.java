package co.com.bancolombia.libreinversion.api.handler;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.commons.ErrorEnum;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.libreinversion.model.request.ConfirmOfferRQ;
import co.com.bancolombia.libreinversion.model.request.ConfirmOfferResponse;
import co.com.bancolombia.libreinversion.usecase.redis.RedisUseCase;
import co.com.bancolombia.libreinversion.usecase.request.ConfirmOfferCompleteUseCase;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;


@Component
@RequiredArgsConstructor
public class ConfirmHandler {

    private static TechLogger log = LoggerFactory.getLog("ConfirmHandler");
    private final ConfirmOfferCompleteUseCase confirmOfferCompleteUseCase;
    private final RedisUseCase redisUseCase;

    @Value("${adapter.aws.s3.bucketName}")
    private String bucketName;

    @NonNull
    public Mono<ServerResponse> listenConfirm(ServerRequest serverRequest) {
        String messageId = "";

        return serverRequest.bodyToMono(ConfirmOfferRQ.class)
                .single()
                .flatMap(this::setCacheRQAndOperation)
                .flatMap(body -> {
                        return confirmOfferCompleteUseCase.comfirmComplete(body, messageId, bucketName);
                })
                .flatMap(response -> {
                            if (response.getData() != null) {
                                return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(response);
                            }
                            return ServerResponse.ok().build();
                        }
                );
    }

    private Mono<ConfirmOfferRQ> setCacheRQAndOperation(ConfirmOfferRQ confirmOfferRQ) {

        return Mono.just(confirmOfferRQ);
    }
}
