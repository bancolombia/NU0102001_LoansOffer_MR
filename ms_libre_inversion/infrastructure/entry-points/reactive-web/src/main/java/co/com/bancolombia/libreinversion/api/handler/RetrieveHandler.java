package co.com.bancolombia.libreinversion.api.handler;

import co.com.bancolombia.libreinversion.model.account.DepositAccounts;
import co.com.bancolombia.libreinversion.model.commons.ErrorEnum;
import co.com.bancolombia.libreinversion.model.commons.IdTypeEnum;
import co.com.bancolombia.libreinversion.model.customer.CustomerData;
import co.com.bancolombia.libreinversion.model.customer.Customer;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.libreinversion.model.offer.RetrieveOfferData;
import co.com.bancolombia.libreinversion.model.request.RetrieveOfferRQ;
import co.com.bancolombia.libreinversion.usecase.account.AccountUseCase;
import co.com.bancolombia.libreinversion.usecase.customer.CustomerUseCase;
import co.com.bancolombia.libreinversion.usecase.redis.RedisUseCase;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import co.com.bancolombia.logging.technical.message.ObjectTechMsg;
import co.com.bancolombia.logging.technical.message.TechMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;
import reactor.util.function.Tuple3;


@Component
@RequiredArgsConstructor
public class RetrieveHandler {

    private final AccountUseCase accountUseCase;
    private final CustomerUseCase customerUseCase;
    private final RedisUseCase redisUseCase;
    private static TechLogger log = LoggerFactory.getLog("");

    @NonNull
    public Mono<ServerResponse> listenRetrieve(ServerRequest serverRequest) {

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(getRetrieveResponse(serverRequest), RetrieveOfferData.class)
                .switchIfEmpty(ServerResponse.notFound().build())
                .doOnError(r -> log.debug(r.getMessage()));
    }

    private Mono<RetrieveOfferData> getRetrieveResponse(ServerRequest serverRequest) {
        String messageId = "";
        getInitStep(messageId);
        return serverRequest
                .bodyToMono(RetrieveOfferRQ.class)
                .single()
                .flatMap(rq -> Mono.zip(
                        customerUseCase.getPersonalData(rq, messageId),
                        accountUseCase.retrieveAccounts(rq, messageId),
                        Mono.just(rq.getCustomer())))
                .flatMap(this::buildResponse);
    }

    private TechMessage<String> getInitStep(String messageId) {
        return new ObjectTechMsg("",
                messageId, "", "",
                "", null, "");
    }

    private Mono<RetrieveOfferData> buildResponse(Tuple3<CustomerData, DepositAccounts, Customer> tuple) {
        RetrieveOfferData.RetrieveOfferDataBuilder retrieveOfferData = RetrieveOfferData.builder();

        return Mono.just(retrieveOfferData.build());

    }

}
