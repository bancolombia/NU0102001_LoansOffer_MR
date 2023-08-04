package co.com.bancolombia.libreinversion.account;

import co.com.bancolombia.libreinversion.model.account.gateways.AccountGateways;
import co.com.bancolombia.libreinversion.model.account.rest.DepositAccountData;
import co.com.bancolombia.libreinversion.model.account.rest.DepositAccountRQ;
import co.com.bancolombia.libreinversion.model.account.rest.DepositAccountResponse;
import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.commons.ErrorEnum;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
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
import java.util.ArrayList;
import java.util.Arrays;

@Component
public class AccountAdapter implements AccountGateways {

    private static final TechLogger logger = LoggerFactory.getLog("AccountAdapter");

    private String routeAccounts;

    private String xIbmClientSecret;

    private String xIbmClientId;

    private final WebClient webClient;

    public AccountAdapter(String baseUrl)
            throws SSLException {

        SslContext sslContext = SslContextBuilder
                .forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        HttpClient httpClient = HttpClient.create()
                .secure(sslSpec -> sslSpec.sslContext(sslContext))
                .resolver(DefaultAddressResolverGroup.INSTANCE);
        webClient = WebClient
                .builder().baseUrl(baseUrl).clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

    @Override
    public Mono<DepositAccountResponse> retrieveDepositAccounts(DepositAccountData args, String msgId) {

        return webClient
                .post()
                .uri(routeAccounts)
                .body(buildRequest(args), DepositAccountRQ.class)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new LibreInversionException(
                        ErrorEnum.MSG_LI010.getMessage(),
                        ErrorEnum.MSG_LI010.getName(),
                        ErrorEnum.MSG_LI010.getMessage(), "", "", msgId)))
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new LibreInversionException(
                        Constant.ACCOUNT_SERVER_ERROR,
                        Constant.ERROR_LI002, Constant.ACCOUNT_SERVER_ERROR, "", "", msgId)))
                .bodyToMono(DepositAccountResponse.class);
    }

    private Mono<DepositAccountRQ> buildRequest(DepositAccountData args){
        DepositAccountRQ rq = DepositAccountRQ.builder().data(new ArrayList<>(Arrays.asList(args))).build();
        return Mono.just(rq);
    }
}
