package co.com.bancolombia.libreinversion.request;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.DisbursementRQ;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.response.DisbursementRS;
import co.com.bancolombia.libreinversion.model.loansdisbursements.gateway.DisbursementsGateWay;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Component
public class DisbursementsAdapter implements DisbursementsGateWay {

    private static TechLogger log = LoggerFactory.getLog(DisbursementsAdapter.class.getName());
    private final WebClient webClient;
    @Value("${adapter-routes.urlDisbursement}")
    private String urlDisbursement;

    public DisbursementsAdapter(@Value("${adapter-routes.urlBaseDisbursement}") String baseUrl) throws SSLException {
        SslContext sslContext = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        HttpClient httpClient = HttpClient.create()
                .secure(sslSpec -> sslSpec.sslContext(sslContext))
                .resolver(DefaultAddressResolverGroup.INSTANCE);
        webClient = WebClient.builder().baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

    public Mono<DisbursementRS> requestDisbursement(DisbursementRQ disbursement, String msgId) {
        return webClient.post()
                .uri(urlDisbursement)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(disbursement), DisbursementRQ.class)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, resp -> printError(resp, msgId))
                .onStatus(HttpStatus::is5xxServerError, resp -> printError(resp, msgId))
                .bodyToMono(DisbursementRS.class);
    }

    private Mono<LibreInversionException> printError(ClientResponse response, String messageId) {
        return response.bodyToMono(String.class).flatMap(errorDMResp -> {
            log.error(errorDMResp);
            return Mono.error(new LibreInversionException(
                    Constant.DISDURSEMENT_SERVER_ERROR, response.statusCode().toString(),
                    Constant.DISDURSEMENT_SERVER_ERROR, "", "", messageId));
        });
    }
}
