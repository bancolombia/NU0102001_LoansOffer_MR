package co.com.bancolombia.notification.api;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class HealthHandler {


    public Mono<ServerResponse> listenHealth(ServerRequest serverRequest) {
        return ServerResponse.ok().build();
    }
}
