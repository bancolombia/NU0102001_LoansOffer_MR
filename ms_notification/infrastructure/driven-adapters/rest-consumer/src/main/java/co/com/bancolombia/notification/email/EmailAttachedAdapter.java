package co.com.bancolombia.notification.email;

import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import co.com.bancolombia.notification.model.commons.Constant;
import co.com.bancolombia.notification.model.email.attachment.EmailAttachedRQ;
import co.com.bancolombia.notification.model.email.attachment.EmailAttachedRS;
import co.com.bancolombia.notification.model.email.gateways.EmailAttachedGateway;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Component
public class EmailAttachedAdapter implements EmailAttachedGateway {

    private static TechLogger log = LoggerFactory.getLog(EmailAttachedAdapter.class.getName());
    @Value("${adapter.email-attached.url}")
    private String url;

    @Value("${header.X-IBM-Client-Secret}")
    private String xIbmClientSecret;

    @Value("${header.X-IBM-Client-Id}")
    private String xIbmClientId;

    @Value("${adapter.email-attached.urlBase}")
    private String urlBase;

    public WebClient createWebClient() throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
        return WebClient.builder().baseUrl(urlBase).clientConnector(new ReactorClientHttpConnector(httpClient)).build();

    }

    @Override
    public Mono<EmailAttachedRS> send(EmailAttachedRQ requestBody, String priority, String msgId) {
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
                    .body(Mono.just(requestBody), EmailAttachedRQ.class)
                    .retrieve()
                    .bodyToMono(EmailAttachedRS.class)
                    .doOnSuccess(e -> log.info(e.getData().get(Constant.FIRST_ARRAY_POSITION).getResponseMessage()));
        } catch (SSLException e) {
            log.error(e.getCause().toString(), e.getMessage());
        }
        return null;
    }
}
