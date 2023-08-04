package co.com.bancolombia.utilities.adapter.installments;

import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import co.com.bancolombia.utilities.model.exceptions.AmortizationInternalException;
import co.com.bancolombia.utilities.model.utils.Constant;
import co.com.bancolombia.utilities.model.installments.RequestInstallments;
import co.com.bancolombia.utilities.model.installments.ResponseInstallments;
import co.com.bancolombia.utilities.model.gateways.InstallmentsGateway;
import co.com.bancolombia.utilities.model.utils.ExceptionEnum;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

import static co.com.bancolombia.utilities.adapter.utils.HandleRetry.retry;

@Component
public class InstallmentsAdapter implements InstallmentsGateway {

    @Value("${routesLF.routeInstallments}")
    private String routeInstallments;

    @Value("${header.X-IBM-Client-Id-Installments}")
    private String xIbmClientId;

    @Value("${header.X-IBM-Client-Secret-Installments}")
    private String xIbmClientSecret;

    private final WebClient webClient;

    private static final TechLogger logger = LoggerFactory.getLog("InstallmentsDataAdapter");

    public InstallmentsAdapter(@Value("${routesLF.urlBase}") String baseUrl) throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        HttpClient httpClient = HttpClient.create().secure(sslSpec -> sslSpec.sslContext(sslContext))
                .resolver(DefaultAddressResolverGroup.INSTANCE);

        webClient = WebClient
                .builder().baseUrl(baseUrl).clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

    @Override
    public Mono<ResponseInstallments> retrieveInstallments(RequestInstallments request, String messageId) {
        return webClient
                .post()
                .uri(routeInstallments)
                .header(Constant.X_IBM_CLIENT_ID, xIbmClientId)
                .header(Constant.X_IBM_CLIENT_SECRET, xIbmClientSecret)
                .header(Constant.MESSAGE_ID, messageId)
                .header(Constant.CONTENT_TYPE, Constant.CONTENT_TYPE_V4)
                .body(Mono.just(request), RequestInstallments.class)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new AmortizationInternalException(
                        ExceptionEnum.LI007.name(), ExceptionEnum.LI007.name(),
                        ExceptionEnum.LI007.getMessage(), "", "", messageId)))
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new AmortizationInternalException(
                        ExceptionEnum.LI008.name(), ExceptionEnum.LI008.name(),
                        ExceptionEnum.LI008.getMessage(), "", "", messageId)))
                .bodyToMono(ResponseInstallments.class)
                .retryWhen(retry(messageId, ExceptionEnum.LI008))
                .doOnSuccess(e -> logger.info(messageId));
    }
}