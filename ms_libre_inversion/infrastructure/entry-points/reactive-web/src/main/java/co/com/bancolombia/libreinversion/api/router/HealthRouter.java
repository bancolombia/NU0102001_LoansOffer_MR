package co.com.bancolombia.libreinversion.api.router;

import co.com.bancolombia.libreinversion.api.handler.HealthHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class HealthRouter {

    @Value("${entry-point.health}")
    private String routeHealth;

    @Bean
    public RouterFunction<ServerResponse> routerHealth(HealthHandler healthHandler) {
        return route(GET(routeHealth), healthHandler::listenHealth);
    }
}
