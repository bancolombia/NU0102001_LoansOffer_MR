package co.com.bancolombia.libreinversion.loanamount;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.commons.ErrorEnum;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.libreinversion.model.loanamount.LoanAmountTestUtil;
import co.com.bancolombia.libreinversion.model.loanamount.RequestLoanAmount;
import co.com.bancolombia.libreinversion.model.loanamount.SuccessResponseLoanAmount;
import co.com.bancolombia.libreinversion.model.loanamount.gateways.LoanAmountGateway;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCacheable;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

import java.time.Duration;

import static co.com.bancolombia.libreinversion.util.HandleRetry.retry;


@Component
public class LoanAmountAdapter implements LoanAmountGateway {

    private String routeLoanAmount;

    private String clientId;

    private String clientSecret;

    private final WebClient webClient;

    private static final TechLogger log = LoggerFactory.getLog("LoanAmountAdapter");

    public LoanAmountAdapter(String baseUrl) throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(Constant.TIMEOUT_SECONDS))
                .secure(sslSpec -> sslSpec.sslContext(sslContext))
                .resolver(DefaultAddressResolverGroup.INSTANCE);

        webClient = WebClient
                .builder().baseUrl(baseUrl).clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }


    public Mono<SuccessResponseLoanAmount> getLoanAmount(RequestLoanAmount request, String messageId) {
        return webClient
                .post()
                .uri(routeLoanAmount)
                .body(Mono.just(request), RequestLoanAmount.class)
                .retrieve()
                .onStatus(status -> status.value() == HttpStatus.GATEWAY_TIMEOUT.value(),
                        response -> Mono.error(new LibreInversionException(ErrorEnum.MSG_LI024.getName(),
                                ErrorEnum.MSG_LI024.getName(), ErrorEnum.MSG_LI024.getMessage(), "", "", messageId)))
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new LibreInversionException(
                        ErrorEnum.MSG_LI025.name(), ErrorEnum.MSG_LI025.name(),
                        ErrorEnum.MSG_LI025.getMessage(), "", "", messageId)))
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new LibreInversionException(
                        ErrorEnum.MSG_LI026.name(), ErrorEnum.MSG_LI026.name(),
                        ErrorEnum.MSG_LI026.getMessage(), "", "", messageId)))
                .bodyToMono(SuccessResponseLoanAmount.class)
                .flatMap(su -> Mono.just(LoanAmountTestUtil.response()))
                .retryWhen(retry(messageId, ErrorEnum.MSG_LI026));
    }
}
