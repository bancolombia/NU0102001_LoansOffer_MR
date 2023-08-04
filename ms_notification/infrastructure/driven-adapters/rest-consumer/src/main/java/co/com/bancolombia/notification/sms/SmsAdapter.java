package co.com.bancolombia.notification.sms;

import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import co.com.bancolombia.notification.model.commons.Constant;
import co.com.bancolombia.notification.model.sms.SmsRQ;
import co.com.bancolombia.notification.model.sms.SmsRS;
import co.com.bancolombia.notification.model.sms.gateways.SmsGateways;
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
public class SmsAdapter implements SmsGateways {

    private static TechLogger log = LoggerFactory.getLog(SmsAdapter.class.getName());

    @Value("${adapter.sms.url}")
    private String url;

    @Value("${header.X-IBM-Client-Secret}")
    private String xIbmClientSecret;

    @Value("${header.X-IBM-Client-Id}")
    private String xIbmClientId;

    @Value("${adapter.sms.urlBase}")
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
    public Mono<SmsRS> send(SmsRQ rq, String priority, String messageId) {
        try {
            WebClient webclient2 = createWebClient();
            return webclient2
                    .post()
                    .uri(url)
                    .header(Constant.X_IBM_CLIENT_SECRET, xIbmClientSecret)
                    .header(Constant.X_IBM_CLIENT_ID, xIbmClientId)
                    .header(Constant.MESSAGE_ID, messageId)
                    .header(Constant.PRIORITY, priority)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(Constant.CONTENT_TYPE, "application/json")
                    .body(Mono.just(rq), SmsRQ.class)
                    .retrieve()
                    .bodyToMono(SmsRS.class).doOnSuccess(e -> log.error(e.getData().getMessageId()));
        } catch (SSLException e) {
            log.error(e.getMessage());
            return Mono.error(e);
        }
    }
}
