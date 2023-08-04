package co.com.bancolombia.libreinversion.docmanagement;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.commons.ErrorEnum;
import co.com.bancolombia.libreinversion.model.docmanagement.ErrorDMResp;
import co.com.bancolombia.libreinversion.model.docmanagement.SignDocumentsReq;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.libreinversion.model.request.gateways.DocManagementGateway;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Component
public class DocManagementAdapter implements DocManagementGateway {

    private static TechLogger log = LoggerFactory.getLog(DocManagementAdapter.class.getName());
    private final WebClient webClient;
    private String signDocuments;
    private String xIbmClientSecret;
    private String xIbmClientId;

    public DocManagementAdapter(@Value("${adapter-routes.urlBaseDocManagement}") String baseUrl) throws SSLException {
        SslContext sslContext = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        HttpClient httpClient = HttpClient.create()
                .secure(sslSpec -> sslSpec.sslContext(sslContext))
                .resolver(DefaultAddressResolverGroup.INSTANCE);
        webClient = WebClient.builder().baseUrl(baseUrl)
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(Constant.BYTE_COUNT * Constant.ALLOCATE_BUFFER * Constant.ALLOCATE_BUFFER))
                .clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

    @Override
    public Mono<byte[]> signDocument(SignDocumentsReq signDocumentsReq,
                                     String messageId, String xUsername,
                                     String xUsertoken) {
        LibreInversionException err = new LibreInversionException(ErrorEnum.MSG_LI020.getName()
                , ErrorEnum.MSG_LI020.getName(), ErrorEnum.MSG_LI020.getMessage(), "", "signDocument", messageId);
        try {
            return webClient.post()
                    .uri(signDocuments)
                    .header("message-id", messageId)
                    .body(Mono.just(signDocumentsReq), SignDocumentsReq.class)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, response -> printError(response, messageId))
                    .onStatus(HttpStatus::is5xxServerError, response -> printError(response, messageId))
                    .bodyToMono(byte[].class)
                    .onErrorResume(e -> {
                        log.error(e);
                        return Mono.error(err);
                    });
        } catch (RuntimeException e) {
            log.error(e);
            return Mono.error(err);
        }
    }

    private Mono<LibreInversionException> printError(ClientResponse response, String messageId) {
        return response.bodyToMono(ErrorDMResp.class).flatMap(errorDMResp -> {
            if (errorDMResp.getErrors() != null) {
                errorDMResp.getErrors().forEach(e -> log
                        .error(Constant.DOC_MANAGER_SERVER_ERROR + " : " + e.getCode() + " : " + e.getDetail()));
            }
            return Mono.error(new LibreInversionException(
                    Constant.DOC_MANAGER_SERVER_ERROR, response.statusCode().toString(),
                    Constant.DOC_MANAGER_SERVER_ERROR, "", "", messageId));
        });
    }
}
