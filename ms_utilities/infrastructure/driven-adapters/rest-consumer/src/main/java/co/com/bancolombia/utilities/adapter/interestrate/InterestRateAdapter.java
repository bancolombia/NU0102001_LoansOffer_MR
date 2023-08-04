package co.com.bancolombia.utilities.adapter.interestrate;

import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import co.com.bancolombia.utilities.model.exceptions.AmortizationInternalException;
import co.com.bancolombia.utilities.model.gateways.InterestRateGateway;
import co.com.bancolombia.utilities.model.interestrate.RequestInterestRate;
import co.com.bancolombia.utilities.model.interestrate.ResponseInterestRate;
import co.com.bancolombia.utilities.model.utils.Constant;
import co.com.bancolombia.utilities.model.utils.ExceptionEnum;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.time.Duration;

import static co.com.bancolombia.utilities.adapter.utils.HandleRetry.retry;

@Component
public class InterestRateAdapter implements InterestRateGateway {

    @Value("${routesLF.routeInterestRate}")
    private String routeLoans;

    @Value("${header.X-IBM-Client-Id}")
    private String xIbmClientId;

    @Value("${header.X-IBM-Client-Secret}")
    private String xIbmClientSecret;

    private final WebClient webClient;

    private static final TechLogger logger = LoggerFactory.getLog("InterestRateAdapter");

    public InterestRateAdapter(@Value("${routesLF.urlBaseInterestRate}") String baseUrl) throws SSLException {
        SslContext sslContext = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(Constant.NUMBER_FIVE))
                .secure(sslSpec -> sslSpec.sslContext(sslContext))
                .resolver(DefaultAddressResolverGroup.INSTANCE);

        webClient = WebClient.builder()
                .baseUrl(baseUrl).clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

    @Override
    public Mono<ResponseInterestRate> retrieveInterestRate(RequestInterestRate requestBody, String messageId) {
        return webClient
                .post()
                .uri(routeLoans)
                .header(Constant.X_IBM_CLIENT_ID, "3395d54f-19b8-4918-99d0-8d9afd634ac4")
                .header(Constant.X_IBM_CLIENT_SECRET, "K6sR2tM2cS1xM6sY1pJ1uQ6cI3yA2eI7vV0oU3lX7kT0vH8eB0")
                .header(Constant.MESSAGE_ID, messageId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestBody), RequestInterestRate.class)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new AmortizationInternalException(
                        ExceptionEnum.LI009.name(), ExceptionEnum.LI009.name(),
                        ExceptionEnum.LI009.getMessage(), "", "", messageId)))
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new AmortizationInternalException(
                        ExceptionEnum.LI010.name(), ExceptionEnum.LI010.name(),
                        ExceptionEnum.LI010.getMessage(), "", "", messageId)))
                .bodyToMono(ResponseInterestRate.class)
                .retryWhen(retry(messageId, ExceptionEnum.LI010))
                .doOnSuccess(e -> logger.info(messageId));
    }
}
