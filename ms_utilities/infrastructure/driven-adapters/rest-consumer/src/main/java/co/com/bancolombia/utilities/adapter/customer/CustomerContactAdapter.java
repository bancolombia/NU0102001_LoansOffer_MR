package co.com.bancolombia.utilities.adapter.customer;

import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import co.com.bancolombia.utilities.model.customer.RequestCustomer;
import co.com.bancolombia.utilities.model.customer.ResponseCustomerContact;
import co.com.bancolombia.utilities.model.exceptions.AmortizationInternalException;
import co.com.bancolombia.utilities.model.gateways.CustomerContactGateway;
import co.com.bancolombia.utilities.model.utils.Constant;
import co.com.bancolombia.utilities.model.utils.ExceptionEnum;
import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCacheable;
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
public class CustomerContactAdapter implements CustomerContactGateway {

    @Value("${routesLF.routeCustomerContact}")
    private String routeCustomerContact;

    @Value("${header.X-IBM-Client-Id}")
    private String xIbmClientId;

    @Value("${header.X-IBM-Client-Secret}")
    private String xIbmClientSecret;

    private final WebClient webClient;

    private static final TechLogger logger = LoggerFactory.getLog("CustomerContactAdapter");

    public CustomerContactAdapter(@Value("${routesLF.urlBase}") String baseUrl) throws SSLException {
        SslContext sslContext = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(Constant.NUMBER_FIVE))
                .secure(sslSpec -> sslSpec.sslContext(sslContext))
                .resolver(DefaultAddressResolverGroup.INSTANCE);

        webClient = WebClient.builder()
                .baseUrl(baseUrl).clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

    @ReactiveRedisCacheable(
            cacheName = "customerContact", key = "'id_' + #request.data.customerDocumentId", timeout = 300L)
    public Mono<ResponseCustomerContact> retrieveCustomerContact(
            RequestCustomer request, String messageId) {
        return webClient
                .post()
                .uri(routeCustomerContact)
                .header(Constant.X_IBM_CLIENT_SECRET, xIbmClientSecret)
                .header(Constant.X_IBM_CLIENT_ID, xIbmClientId)
                .header(Constant.MESSAGE_ID, messageId)
                .accept(MediaType.APPLICATION_JSON)
                .header(Constant.CONTENT_TYPE, Constant.CONTENT_TYPE_V1)
                .body(Mono.just(request), RequestCustomer.class)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new AmortizationInternalException(
                        ExceptionEnum.LI003.name(), ExceptionEnum.LI003.name(),
                        ExceptionEnum.LI003.getMessage(), "", "", messageId)))
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new AmortizationInternalException(
                        ExceptionEnum.LI004.name(), ExceptionEnum.LI004.name(),
                        ExceptionEnum.LI004.getMessage(), "", "", messageId)))
                .bodyToMono(ResponseCustomerContact.class)
                .retryWhen(retry(messageId, ExceptionEnum.LI004))
                .doOnSuccess(e -> logger.info(messageId));
    }
}
