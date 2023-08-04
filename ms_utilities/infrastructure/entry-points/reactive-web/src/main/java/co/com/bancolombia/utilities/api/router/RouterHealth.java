package co.com.bancolombia.utilities.api.router;

import co.com.bancolombia.utilities.api.handler.HandlerHealth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterHealth {

    @Value("${routes.basePath}")
    private String basePath;

    @Value("${routes.health}")
    private String route;

    @Bean
    public RouterFunction<ServerResponse> routerHealthUtilities(HandlerHealth handlerHealth) {
        return route(GET(basePath.concat(route)), handlerHealth::listenHealth);
    }
}
