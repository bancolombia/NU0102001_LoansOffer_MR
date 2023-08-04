package com.bancolombia.libreinversion.apis.test;

import co.com.bancolombia.libreinversion.model.stoc.GeneralInfoPersonManage;
import co.com.bancolombia.libreinversion.model.stoc.PersonManagementInfo;
import co.com.bancolombia.libreinversion.model.stoc.PersonManagementRQ;
import co.com.bancolombia.libreinversion.stoc.OpportunitiesAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Assert;
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
import java.util.Objects;

@RunWith(SpringRunner.class)
@WebFluxTest
@ContextConfiguration(classes = {OpportunitiesAdapter.class})
public class OpportunitiesGestionAdapterTest {

    private MockWebServer mockWebServer;
    private String response;
    private ObjectMapper mapper;
    private Mono<String> responseMono;

    @Before
    public void init() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());

        OpportunitiesAdapter adapter = new OpportunitiesAdapter(baseUrl);

        PersonManagementRQ request = PersonManagementRQ.builder()
                .data(PersonManagementInfo.builder()
                        .generalInformation(GeneralInfoPersonManage.builder()
                                .contactChannelCode(5)
                                .opportunityId("5")
                                .stateCode(5)
                                .build())
                        .build())
                .build();

        response = "test";

        mapper = new ObjectMapper();
        String messageId = "cf6f7b7c-5d94-4f8a-afc4-6b796da4bfbc";
        responseMono = adapter.opportunitiesPersonManagement(request, messageId);
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
                .setBody(mapper.writeValueAsString(response)));

        StepVerifier.create(responseMono)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        Assert.assertEquals("POST", recordedRequest.getMethod());
    }

    @Test
    public void shouldGet4xxStatus() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setBody(mapper.writeValueAsString(response)));

        StepVerifier.create(responseMono).verifyError();
    }

    @Test
    public void shouldGet5xxStatus() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setBody(mapper.writeValueAsString(response)));

        StepVerifier.create(responseMono).verifyError();
    }
}
