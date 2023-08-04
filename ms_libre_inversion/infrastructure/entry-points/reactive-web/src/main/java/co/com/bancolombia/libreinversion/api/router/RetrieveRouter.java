package co.com.bancolombia.libreinversion.api.router;

import co.com.bancolombia.libreinversion.api.handler.RetrieveHandler;
import co.com.bancolombia.libreinversion.model.error.ErrorResponseApi;
import co.com.bancolombia.libreinversion.model.swagger.RetrieveOfferReponse;
import co.com.bancolombia.libreinversion.model.swagger.RetrieveOfferRequest;
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
public class RetrieveRouter {

    @Value("${entry-point.retrieve}")
    private String routeRetrieve;

    @Bean
    public RouterFunction<ServerResponse> routerRetrieve(RetrieveHandler offerHandler) {
        return route(POST(routeRetrieve)
                .and(accept(APPLICATION_JSON))
                .and(contentType(APPLICATION_JSON)), offerHandler::listenRetrieve);
    }
}
