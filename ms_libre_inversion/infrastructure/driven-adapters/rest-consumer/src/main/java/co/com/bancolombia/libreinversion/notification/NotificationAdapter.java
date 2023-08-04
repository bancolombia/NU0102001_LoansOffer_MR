package co.com.bancolombia.libreinversion.notification;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.libreinversion.model.notification.gateways.NotificationGateways;
import co.com.bancolombia.libreinversion.model.notification.rest.ResponseRetrieveInfo;
import co.com.bancolombia.libreinversion.model.notification.rest.RetrieveInformationRQ;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCacheable;
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

@Component
public class NotificationAdapter implements NotificationGateways {

    private static final TechLogger logger = LoggerFactory.getLog("NotificationAdapter");

    private String routeRetrieve;

    private String xIbmClientSecret;

    private String xIbmClientId;

    private final WebClient webClient;


    public NotificationAdapter(String baseUrl)
            throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        HttpClient httpClient = HttpClient.create()
                .secure(sslSpec -> sslSpec.sslContext(sslContext))
                .resolver(DefaultAddressResolverGroup.INSTANCE);

        webClient = WebClient
                .builder().baseUrl(baseUrl).clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

    public Mono<ResponseRetrieveInfo> callRetrieveInformation(RetrieveInformationRQ request, String msgId, Long time) {

        return webClient
                .post()
                .uri(routeRetrieve)
                .body(Mono.just(request), RetrieveInformationRQ.class)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new LibreInversionException(
                        Constant.ALERT_CLIENT_ERROR, Constant.ERROR_LI003, Constant.ALERT_CLIENT_ERROR, "", "", msgId)))
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new LibreInversionException(
                        Constant.ALERT_SERVER_ERROR, Constant.ERROR_LI003, Constant.ALERT_SERVER_ERROR, "", "", msgId)))
                .bodyToMono(ResponseRetrieveInfo.class);
    }
}
