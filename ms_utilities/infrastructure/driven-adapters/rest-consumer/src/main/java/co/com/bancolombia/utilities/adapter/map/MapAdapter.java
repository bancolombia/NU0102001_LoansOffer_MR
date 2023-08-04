package co.com.bancolombia.utilities.adapter.map;

import co.com.bancolombia.utilities.model.exceptions.AmortizationInternalException;
import co.com.bancolombia.utilities.model.gateways.MapGateway;
import co.com.bancolombia.utilities.model.product.RuleRequest;
import co.com.bancolombia.utilities.model.product.RuleResponse;
import co.com.bancolombia.utilities.model.utils.Constant;
import co.com.bancolombia.utilities.model.utils.ExceptionEnum;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

import static co.com.bancolombia.utilities.adapter.utils.HandleRetry.retry;

@Component
public class MapAdapter implements MapGateway {

    @Value("${routesLF.ruleValidate}")
    private String routeRuleValidate;

    @Value("${header.MAP-Client-Id}")
    private String clientId;

    @Value("${header.MAP-Client-Secret}")
    private String clientSecret;

    private final WebClient webClient;

    public MapAdapter(@Value("${routesLF.urlBaseMap}") String baseUrl) throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        HttpClient httpClient = HttpClient.create().secure(sslSpec -> sslSpec.sslContext(sslContext));

        webClient = WebClient
                .builder().baseUrl(baseUrl).clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

    @Override
    public Mono<RuleResponse> ruleValidate(RuleRequest request) {
        return webClient
                .post()
                .uri(routeRuleValidate)
                .header(Constant.X_MAP_CLIENT_ID, clientId)
                .header(Constant.X_MAP_SECRET_ID, clientSecret)
                .header(Constant.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), RuleRequest.class)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new AmortizationInternalException(
                        ExceptionEnum.LI011.name(), ExceptionEnum.LI011.name(),
                        ExceptionEnum.LI011.getMessage(), "", "", "")))
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new AmortizationInternalException(
                        ExceptionEnum.LI012.name(), ExceptionEnum.LI012.name(),
                        ExceptionEnum.LI012.getMessage(), "", "", "")))
                .bodyToMono(RuleResponse.class)
                .retryWhen(retry("", ExceptionEnum.LI012));
    }
}
