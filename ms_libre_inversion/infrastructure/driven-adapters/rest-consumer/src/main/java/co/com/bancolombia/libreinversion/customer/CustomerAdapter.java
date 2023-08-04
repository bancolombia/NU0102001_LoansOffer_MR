package co.com.bancolombia.libreinversion.customer;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.customer.gateways.CustomerDataGateways;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerCommercial;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerBasic;
import co.com.bancolombia.libreinversion.model.customer.rest.RequestBodyData;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerDetail;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerContact;
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

@Component
public class CustomerAdapter implements CustomerDataGateways {

    private static final TechLogger logger = LoggerFactory.getLog("CustomerDataRest");

    private String routeDetail;

    private String routeBasic;

    private String routeContact;

    private String routeCommercial;

    private String xIbmClientSecret;

    private String xIbmClientId;

    private final WebClient webClient;

    public CustomerAdapter(String baseUrl)
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
    public Mono<ResponseCustomerBasic> callPersonalData(RequestBodyData request, String msgId, Long time) {
        return getRequest(request,msgId, routeBasic)
                .bodyToMono(ResponseCustomerBasic.class);
    }

    @Override

    public Mono<ResponseCustomerDetail> callDetailInformation(RequestBodyData request, String msgId, Long time) {

        return getRequest(request,msgId, routeDetail)
                .bodyToMono(ResponseCustomerDetail.class);
    }

    @Override
    public Mono<ResponseCustomerContact> callContactInformation(RequestBodyData request, String msgId, Long time) {
        return getRequest(request,msgId, routeContact)
                .bodyToMono(ResponseCustomerContact.class);
    }

    @Override
    @ReactiveRedisCacheable(
            cacheName = "customer_commercial_data", key = "'id_' + #request.data.customerDocumentId", timeout = 300L)
    public Mono<ResponseCustomerCommercial> callCommercialData(RequestBodyData request, String msgId) {
        return getRequest(request,msgId, routeCommercial)
                .bodyToMono(ResponseCustomerCommercial.class);
    }

    private WebClient.ResponseSpec getRequest(RequestBodyData request, String msgId, String route){
        return webClient
                .post()
                .uri(route)
                .header(Constant.X_IBM_CLIENT_SECRET, xIbmClientSecret)
                .header(Constant.X_IBM_CLIENT_ID, xIbmClientId)
                .header(Constant.MESSAGE_ID, msgId)
                .accept(MediaType.APPLICATION_JSON)
                .header(Constant.CONTENT_TYPE, Constant.CONTENT_TYPE_V1)
                .body(Mono.just(request), RequestBodyData.class)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new LibreInversionException(
                        Constant.MDM_CLIENT_ERROR, Constant.ERROR_LI001, Constant.MDM_CLIENT_ERROR, "", "", msgId)))
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new LibreInversionException(
                        Constant.MDM_SERVER_ERROR, Constant.ERROR_LI001, Constant.MDM_CLIENT_ERROR, "", "", msgId)));
    }
}
