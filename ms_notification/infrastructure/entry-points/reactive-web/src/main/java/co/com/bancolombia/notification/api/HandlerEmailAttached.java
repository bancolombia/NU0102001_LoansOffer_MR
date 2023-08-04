package co.com.bancolombia.notification.api;

import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import co.com.bancolombia.notification.model.commons.Constant;
import co.com.bancolombia.notification.model.email.attachment.EmailAttachedRQ;
import co.com.bancolombia.notification.usecase.emailattachment.EmailAttachedUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class HandlerEmailAttached {

    private final EmailAttachedUseCase emailAttacdUseCase;
    private static TechLogger log = LoggerFactory.getLog(HandlerEmailAttached.class.getName());

    public Mono<ServerResponse> listenSend(ServerRequest serverRequest) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(buildResponse(serverRequest), Boolean.class);
    }

    private Mono<Boolean> buildResponse(ServerRequest serverRequest) {
        String messageId = serverRequest.headers().header(Constant.MESSAGE_ID).get(0);
        String priority = serverRequest.headers().header(Constant.PRIORITY).get(0);
        return serverRequest
                .bodyToMono(EmailAttachedRQ.class)
                .single()
                .flatMap(rq -> emailAttacdUseCase.send(rq, priority, messageId));
    }
}
