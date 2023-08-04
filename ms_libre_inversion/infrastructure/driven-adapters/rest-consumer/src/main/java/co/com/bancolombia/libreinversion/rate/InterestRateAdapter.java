package co.com.bancolombia.libreinversion.rate;


import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.libreinversion.model.rate.LoanInteresRateResponse;
import co.com.bancolombia.libreinversion.model.rate.LoanInteresRateRQ;
import co.com.bancolombia.libreinversion.model.rate.gateways.InterestRateAdapterGateways;
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

@Component
public class InterestRateAdapter implements InterestRateAdapterGateways {

    private String xIbmClientSecret;

    private String xIbmClientId;

    private String routeInterestRate;

    private final WebClient webClient;

    public InterestRateAdapter(@Value("${adapter-routes.urlBaseInterestRate}") String baseUrl)
            throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        HttpClient httpClient = HttpClient.create()
                .secure(sslSpec -> sslSpec.sslContext(sslContext))
                .resolver(DefaultAddressResolverGroup.INSTANCE);

        webClient = WebClient.builder().baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

    @Override
    @ReactiveRedisCacheable(
            cacheName = "interes_rate",
            key = "'id_' + #idNumber + #request.data.fixedRateLoans.get(0).fixedRateLoanId",
            timeout = 300L)
    public Mono<LoanInteresRateResponse> callLoanInteresRate(LoanInteresRateRQ request,
                                                             String msgId, String idNumber) {
        return getRequest(request, msgId, routeInterestRate)
                .bodyToMono(LoanInteresRateResponse.class);
    }

    private WebClient.ResponseSpec getRequest(LoanInteresRateRQ request, String msgId, String route){
        return webClient
                .post()
                .uri(route)
                .body(Mono.just(request), LoanInteresRateRQ.class)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new LibreInversionException(
                        Constant.INTERES_RATE_CLIENT_ERROR, Constant.ERROR_LI001,
                        Constant.INTERES_RATE_CLIENT_ERROR, "", "", msgId)))
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new LibreInversionException(
                        Constant.INTERES_RATE_SERVER_ERROR, Constant.ERROR_LI001,
                        Constant.INTERES_RATE_SERVER_ERROR, "", "", msgId)));
    }

}
