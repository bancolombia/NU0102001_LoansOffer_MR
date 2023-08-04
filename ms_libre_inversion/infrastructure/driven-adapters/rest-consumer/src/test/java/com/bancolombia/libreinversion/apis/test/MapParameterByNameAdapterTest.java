package com.bancolombia.libreinversion.apis.test;


import co.com.bancolombia.libreinversion.map.MapAdapter;
import co.com.bancolombia.libreinversion.model.product.Parameter;
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
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@WebFluxTest
@ContextConfiguration(classes = {MapAdapter.class})
public class MapParameterByNameAdapterTest {

    private MockWebServer mockWebServer;
    private ObjectMapper mapper;
    private Parameter ruleResponse;
    private Mono<Parameter> ruleResponseMono;

    @Before
    public void init() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        MapAdapter adapter = new MapAdapter(baseUrl);

        mapper = new ObjectMapper();

        ruleResponse = Parameter.builder()
                .name("DiasOfertaLINativo")
                .build();

        ruleResponseMono = adapter.getParameterByName("DiasOfertaLINativo");
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
                .expectNextMatches(r -> r.getName().equals("DiasOfertaLINativo"))
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
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
