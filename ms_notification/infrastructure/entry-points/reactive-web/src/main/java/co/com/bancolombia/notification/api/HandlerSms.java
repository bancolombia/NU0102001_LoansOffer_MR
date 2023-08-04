package co.com.bancolombia.notification.api;

import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import co.com.bancolombia.notification.model.commons.Constant;
import co.com.bancolombia.notification.model.sms.SmsRQ;
import co.com.bancolombia.notification.usecase.sms.SmsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class HandlerSms {

    private final SmsUseCase smsUseCase;
    private static TechLogger log =  LoggerFactory.getLog("HandlerSms");

    public Mono<ServerResponse> listenSend(ServerRequest serverRequest) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(buildResponse(serverRequest), String.class);
    }

    private Mono<Boolean> buildResponse(ServerRequest serverRequest){
        String messageId = serverRequest.headers().header(Constant.MESSAGE_ID).get(0);
        String priority = serverRequest.headers().header(Constant.PRIORITY).get(0);
        return serverRequest
                .bodyToMono(SmsRQ.class)
                .single()
                .flatMap(rq -> {
                    log.info(rq);
                    return Mono.just(rq);
                })
                .flatMap(rq->smsUseCase.send(rq, messageId, priority));
    }
}
