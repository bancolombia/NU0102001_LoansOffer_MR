package com.bancolombia.libreinversion.apis.test;


import co.com.bancolombia.libreinversion.map.MapAdapter;
import co.com.bancolombia.libreinversion.model.product.Amount;
import co.com.bancolombia.libreinversion.model.product.Attribute;
import co.com.bancolombia.libreinversion.model.product.ResponseData;
import co.com.bancolombia.libreinversion.model.product.RuleRequest;
import co.com.bancolombia.libreinversion.model.product.RuleResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@WebFluxTest
@ContextConfiguration(classes = {MapAdapter.class})
public class MapAmountAdapterTest {

    private MockWebServer mockWebServer;
    private ObjectMapper mapper;
    private RuleResponse ruleResponse;
    private Mono<RuleResponse> ruleResponseMono;

    @Before
    public void init() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        MapAdapter adapter = new MapAdapter(baseUrl);

        List<Attribute> attributes = new ArrayList<>();
        attributes.add(Attribute.builder().name("productCode")
                .value("14").build());
        RuleRequest request = RuleRequest.builder()
                .attributes(attributes)
                .productCode("14")
                .ruleName("Montos")
                .build();

        List<Amount> amounts = new ArrayList<>();
        amounts.add(Amount.builder()
                .factor("0.00145")
                .rateType("DTF")
                .build());
        Mono<List<Amount>> response = Mono.just(amounts);
        mapper = new ObjectMapper();

        ruleResponse = RuleResponse.builder()
                .data(ResponseData.builder().valid(true)
                        .build()).build();

        ruleResponseMono = adapter.ruleValidate(request);
        Mono<List<Amount>> responseMono = adapter.getAmounts();
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void test() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setBody(mapper.writeValueAsString(ruleResponse)));

        StepVerifier.create(ruleResponseMono)
                .expectNextMatches(r -> r.getData().isValid())
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
    }

    @Test
    public void shouldGet5xxStatus() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setBody(mapper.writeValueAsString(ruleResponse)));

        StepVerifier.create(ruleResponseMono).verifyError();
    }
}
