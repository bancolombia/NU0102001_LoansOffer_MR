package co.com.bancolombia.libreinversion.stoc;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;

import co.com.bancolombia.libreinversion.model.stoc.GeneralInformation;
import co.com.bancolombia.libreinversion.model.stoc.OpportunitiesRQDataArgs;
import co.com.bancolombia.libreinversion.model.stoc.OpportunitiesRQ;
import co.com.bancolombia.libreinversion.model.stoc.OpportunitiesInquiry;
import co.com.bancolombia.libreinversion.model.stoc.PersonManagementRQ;
import co.com.bancolombia.libreinversion.model.stoc.OpportunitiesRQData;

import co.com.bancolombia.libreinversion.model.stoc.gateways.OpportunitiesGateways;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
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
import java.util.Optional;

@Component
public class OpportunitiesAdapter implements OpportunitiesGateways {

    private static final TechLogger logger = LoggerFactory.getLog("OpportunitiesAdapter");

    private String routeOpportunities;
    private String routeOpportunitiesPerManage;

    private String xIbmClientSecret;

    private String xIbmClientId;

    private final WebClient webClient;


    public OpportunitiesAdapter(String baseUrl)
            throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        HttpClient httpClient = HttpClient.create()
                .secure(sslSpec -> sslSpec.sslContext(sslContext))
                .resolver(DefaultAddressResolverGroup.INSTANCE);

        webClient = WebClient
                .builder().baseUrl(baseUrl).clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }


    public Mono<GeneralInformation> getBusinessOportunities(OpportunitiesRQDataArgs args, String msgId) {

        return webClient.post()
                .uri(routeOpportunities)
                .accept(MediaType.APPLICATION_JSON)
                .body(buildRequest(args), OpportunitiesRQ.class)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new LibreInversionException(
                        Constant.STOC_CLIENT_ERROR, Constant.ERROR_LI003, Constant.STOC_CLIENT_ERROR, "", "", msgId)))
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new LibreInversionException(
                        Constant.STOC_SERVER_ERROR, Constant.ERROR_LI003, Constant.STOC_SERVER_ERROR, "", "", msgId)))
                .bodyToMono(OpportunitiesInquiry.class)
                .flatMap(this::getResponse);
    }

    @Override
    public Mono<String> opportunitiesPersonManagement(PersonManagementRQ args, String msgId) {
        return webClient.post()
                .uri(routeOpportunities)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(args), PersonManagementRQ.class)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> response.bodyToMono(String.class).flatMap(s -> {
                    logger.error(s);
                    return Mono.error(new LibreInversionException(Constant.STOC_CLIENT_ERROR, Constant.ERROR_LI003,
                            Constant.STOC_CLIENT_ERROR, "", "", msgId));
                }))
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new LibreInversionException(
                        Constant.STOC_SERVER_ERROR, Constant.ERROR_LI003, Constant.STOC_SERVER_ERROR, "", "", msgId)))
                .bodyToMono(String.class);
    }

    private Mono<GeneralInformation> getResponse(OpportunitiesInquiry res) {
        Optional<GeneralInformation> op = res.getData().getBankingOpportunities()
                .getGeneralInformation().stream().findFirst();

        GeneralInformation generalInformation = GeneralInformation.builder().build();

        if (op.isPresent()) {
            generalInformation = op.get();
        }

        return Mono.just(generalInformation);
    }

    private Mono<OpportunitiesRQ> buildRequest(OpportunitiesRQDataArgs args) {
        return Mono.just(OpportunitiesRQ.builder().data(
                OpportunitiesRQData.builder().args(args).build()
        ).build());
    }
}
