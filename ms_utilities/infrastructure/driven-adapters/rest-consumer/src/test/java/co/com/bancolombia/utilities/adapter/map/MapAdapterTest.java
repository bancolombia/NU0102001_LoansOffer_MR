package co.com.bancolombia.utilities.adapter.map;

import co.com.bancolombia.utilities.model.product.Attribute;
import co.com.bancolombia.utilities.model.product.ResponseData;
import co.com.bancolombia.utilities.model.product.RuleRequest;
import co.com.bancolombia.utilities.model.product.RuleResponse;
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
public class MapAdapterTest {

    private MockWebServer mockWebServer;
    private ObjectMapper mapper;
    private RuleResponse response;
    private Mono<RuleResponse> responseMono;

    @Before
    public void init() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        MapAdapter adapter = new MapAdapter(baseUrl);

        List<Attribute> attributes = new ArrayList<>();
        attributes.add(Attribute.builder().name("segmento").value("subSegmento").build());
        RuleRequest request = RuleRequest.builder()
                .attributes(attributes)
                .productCode("40")
                .ruleName("Regla_SEGMEN_05_SUBSEG_09")
                .build();

        response = RuleResponse.builder().data(ResponseData.builder().valid(true).build()).build();
        mapper = new ObjectMapper();
        responseMono = adapter.ruleValidate(request);
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void shouldRetrieveLoansAdapter() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setBody(mapper.writeValueAsString(response)));

        StepVerifier.create(responseMono)
                .expectNextMatches(r -> r.getData().isValid())
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
    }
}