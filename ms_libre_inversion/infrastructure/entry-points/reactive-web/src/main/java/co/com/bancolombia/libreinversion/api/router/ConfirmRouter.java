package co.com.bancolombia.libreinversion.api.router;

import co.com.bancolombia.libreinversion.api.handler.ConfirmHandler;
import co.com.bancolombia.libreinversion.model.error.ErrorResponseApi;
import co.com.bancolombia.libreinversion.model.request.ConfirmOfferRQ;
import co.com.bancolombia.libreinversion.model.request.ConfirmOfferResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ConfirmRouter {

    @Value("${entry-point.confirm}")
    private String routeConfirm;

    @Bean
    public RouterFunction<ServerResponse> routerConfirm(ConfirmHandler offerHandler) {
        return route(POST(routeConfirm)
                .and(accept(APPLICATION_JSON))
                .and(contentType(APPLICATION_JSON)), offerHandler::listenConfirm);
    }
}
