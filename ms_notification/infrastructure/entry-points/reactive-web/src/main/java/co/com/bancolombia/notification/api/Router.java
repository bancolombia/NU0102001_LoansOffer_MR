package co.com.bancolombia.notification.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@Configuration
public class Router {

    @Bean
    public RouterFunction<ServerResponse> routerSms(HandlerSms handler) {
        return route(POST("api/v1/sms"), handler::listenSend);

    }

    @Bean
    public RouterFunction<ServerResponse> routerEmail(HandlerEmailBasic handler) {
        return route(POST("api/v1/email/basic"), handler::listenSend);

    }

    @Bean
    public RouterFunction<ServerResponse> routerStatus(HealthHandler healthHandler) {

        return route(GET("api/v1/health"), healthHandler::listenHealth);

    }

    @Bean
    public RouterFunction<ServerResponse> routerEmailAttachment(HandlerEmailAttached handler) {
        return route(POST("api/v1/email/attached"), handler::listenSend);
    }
}
