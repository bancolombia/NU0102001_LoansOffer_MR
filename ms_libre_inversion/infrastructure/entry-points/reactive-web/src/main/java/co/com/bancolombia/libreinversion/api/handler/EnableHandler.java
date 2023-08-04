package co.com.bancolombia.libreinversion.api.handler;

import co.com.bancolombia.libreinversion.model.offer.EnableOfferData;
import co.com.bancolombia.libreinversion.model.offer.RetrieveOfferData;
import co.com.bancolombia.libreinversion.model.request.EnableOfferRQ;
import co.com.bancolombia.libreinversion.usecase.stoc.OpportunitiesUseCase;
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

@Component
@RequiredArgsConstructor
public class EnableHandler {

    private final OpportunitiesUseCase opportunitiesUseCase;
    private static TechLogger log =  LoggerFactory.getLog("EnableHandler");

    @NonNull
    public Mono<ServerResponse> listenEnable(ServerRequest serverRequest) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(getRetrieveResponse(serverRequest), RetrieveOfferData.class);
    }

    private Mono<EnableOfferData> getRetrieveResponse(ServerRequest serverRequest) {
        String messageId = "";
        getInitStep(messageId);
        return serverRequest
                .bodyToMono(EnableOfferRQ.class)
                .single()
                .flatMap(rq -> opportunitiesUseCase.getBusinessOpportunities(rq, messageId));
    }

    private TechMessage<String> getInitStep(String messageId){
        return new ObjectTechMsg<>("",
                messageId, "", "",
                "", null, "");
    }
}
