package co.com.bancolombia.utilities.api.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

@Component
public class HandlerHealth {

    @NonNull
    public Mono<ServerResponse> listenHealth(ServerRequest serverRequest) {
        return ServerResponse.ok().build();
    }
}
