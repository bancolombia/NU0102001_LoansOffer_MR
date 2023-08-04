package co.com.bancolombia.notification.email;

import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import co.com.bancolombia.notification.model.commons.Constant;
import co.com.bancolombia.notification.model.email.EmailBasicRQ;
import co.com.bancolombia.notification.model.email.EmailBasicRS;
import co.com.bancolombia.notification.model.email.gateways.EmailBasicGateways;
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

@Component
public class EmailBasicAdapter implements EmailBasicGateways {

    private static TechLogger log = LoggerFactory.getLog(EmailBasicAdapter.class.getName());

    @Value("${adapter.email-basic.url}")
    private String url;

    @Value("${header.X-IBM-Client-Secret}")
    private String xIbmClientSecret;

    @Value("${header.X-IBM-Client-Id}")
    private String xIbmClientId;

    @Value("${adapter.email-basic.urlBase}")
    private String urlBase;

    public WebClient createWebClient() throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        HttpClient httpClient =
                HttpClient
                        .create()
                        .secure(t -> t.sslContext(sslContext))
                        .resolver(DefaultAddressResolverGroup.INSTANCE);

        return WebClient
                .builder()
                .baseUrl(urlBase)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Override
    public Mono<EmailBasicRS> send(EmailBasicRQ requestBody, String priority, String msgId) {
        WebClient webclient2;
        try {
            webclient2 = createWebClient();
            return webclient2
                    .post()
                    .uri(url)
                    .header(Constant.X_IBM_CLIENT_SECRET, xIbmClientSecret)
                    .header(Constant.X_IBM_CLIENT_ID, xIbmClientId)
                    .header(Constant.MESSAGE_ID, msgId)
                    .header(Constant.PRIORITY, priority)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(Constant.CONTENT_TYPE_BANCO_NAME, Constant.CONTENT_TYPE_BANCO_VALUE)
                    .body(Mono.just(requestBody), EmailBasicRQ.class)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new Exception("4xx")))
                    .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new Exception("5xx")))
                    .bodyToMono(EmailBasicRS.class)
                    .doOnSuccess(e -> log.info(e.getData().get(Constant.FIRST_ARRAY_POSITION).getResponseMessage()));
        } catch (SSLException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
