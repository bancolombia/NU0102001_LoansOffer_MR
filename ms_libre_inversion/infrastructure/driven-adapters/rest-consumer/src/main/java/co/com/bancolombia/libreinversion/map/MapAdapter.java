package co.com.bancolombia.libreinversion.map;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.libreinversion.model.product.RuleRequest;
import co.com.bancolombia.libreinversion.model.product.City;
import co.com.bancolombia.libreinversion.model.product.RuleResponse;
import co.com.bancolombia.libreinversion.model.product.Parameter;
import co.com.bancolombia.libreinversion.model.product.Attribute;
import co.com.bancolombia.libreinversion.model.product.Amount;
import co.com.bancolombia.libreinversion.model.product.gateways.MAPGateways;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
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
import java.util.List;

@Component
public class MapAdapter implements MAPGateways {

    private String clientId;

    private String clientSecret;

    private String routeRuleValidate;

    private String routeParameter;

    Resource citiesJson;

    private final WebClient webClient;


    public MapAdapter(String baseUrl)
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
    public Mono<RuleResponse> ruleValidate(RuleRequest request) {

        return webClient
                .post()
                .uri(routeRuleValidate)
                .body(Mono.just(request), RuleRequest.class)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new LibreInversionException(
                        Constant.MAP_CLIENT_ERROR, Constant.ERROR_LI004, Constant.MAP_CLIENT_ERROR, "", "", "")))
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new LibreInversionException(
                        Constant.MAP_SERVER_ERROR, Constant.ERROR_LI004, Constant.MAP_SERVER_ERROR, "", "", "")))
                .bodyToMono(RuleResponse.class);
    }

    @Override
    public Mono<RuleResponse> ruleValidateIncreaseMemory(RuleRequest request) {

        return webClient
                .mutate()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(Constant.BYTE_COUNT * Constant.ALLOCATE_BUFFER
                                * Constant.ALLOCATE_BUFFER)).build()
                .post()
                .uri(routeRuleValidate)
                .body(Mono.just(request), RuleRequest.class)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new LibreInversionException(
                        Constant.MAP_CLIENT_ERROR, Constant.ERROR_LI004, Constant.MAP_CLIENT_ERROR, "", "", "")))
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new LibreInversionException(
                        Constant.MAP_SERVER_ERROR, Constant.ERROR_LI004, Constant.MAP_SERVER_ERROR, "", "", "")))
                .bodyToMono(RuleResponse.class);
    }

    @Override
    public Mono<Parameter> getParameterByName(String name) {

        return webClient
                .get()
                .uri(routeParameter + "/name/" + name)
                .header("Content-Type", "application/json")
                .header("accept", "*/*")
                .header("client-id", clientId)
                .header("secret-id", clientSecret)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Parameter.class);
    }

    @Override
    public Mono<List<City>> getAllowedCities() {
        List<Attribute> attr = new ArrayList<>(
                Arrays.asList(Attribute.builder()
                        .name("productCode")
                        .value(Constant.PRODUCT_ID).build()));
        return runRuleWitIncreaseMemory(attr, Constant.CITI_ALLOWED_RULE, Constant.PRODUCT_ID)
                .flatMap(jsonCities -> {
                    ObjectMapper mapper = new ObjectMapper();
                    return  Mono.just(mapper.convertValue(
                            jsonCities.getData().getUtilLoad().get("CiudadesPermitidas"),
                            new TypeReference<List<City>>(){}
                    ));
                });
    }

    @Override
    public Mono<List<Amount>> getAmounts() {
        List<Attribute> attr = new ArrayList<>(
                Arrays.asList(Attribute.builder()
                        .name("productCode")
                        .value(Constant.PRODUCT_ID).build()));
        return runRule(attr, Constant.AMOUNT_RULE, Constant.PRODUCT_ID)
                .flatMap(amountJson -> {
                    ObjectMapper mapper = new ObjectMapper();
                    return  Mono.just(mapper.convertValue(
                            amountJson.getData().getUtilLoad().get("Montos"),
                            new TypeReference<List<Amount>>(){}
                    ));
                });
    }

    private Mono<RuleResponse> runRule(List<Attribute> attr, String ruleName, String productId) {

        return ruleValidate(RuleRequest
                .builder()
                .attributes(attr)
                .productCode(productId)
                .ruleName(ruleName).build());
    }

    private Mono<RuleResponse> runRuleWitIncreaseMemory(List<Attribute> attr, String ruleName, String productId) {
        return ruleValidateIncreaseMemory(RuleRequest
                .builder()
                .attributes(attr)
                .productCode(productId)
                .ruleName(ruleName).build());
    }

    @Override
    public Mono<RuleResponse> getTimeOffer() {
        List<Attribute> attr = new ArrayList<>(
                Arrays.asList(Attribute.builder()
                        .name(Constant.OFFER_TIME_PARAM_NAME)
                        .value(Constant.PRODUCT_ID).build()));
        return runRule(attr, Constant.OFFER_TIME_RULE, Constant.PRODUCT_ID);
    }
}
