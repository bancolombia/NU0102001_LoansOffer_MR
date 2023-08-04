package com.bancolombia.libreinversion.apis.test;

import co.com.bancolombia.libreinversion.model.notification.rest.CustomerIdentification;
import co.com.bancolombia.libreinversion.model.notification.rest.CustomerIdentificationRQ;
import co.com.bancolombia.libreinversion.model.notification.rest.ResponseRetrieveInfo;
import co.com.bancolombia.libreinversion.model.notification.rest.ResponseRetrieveInfoData;
import co.com.bancolombia.libreinversion.model.notification.rest.RetrieveInformationRQ;
import co.com.bancolombia.libreinversion.notification.NotificationAdapter;
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

@RunWith(SpringRunner.class)
@WebFluxTest
@ContextConfiguration(classes = {NotificationAdapter.class})
public class NotificationAdapterTest {

    private MockWebServer mockWebServer;
    private ResponseRetrieveInfo response;
    private ObjectMapper mapper;
    private Mono<ResponseRetrieveInfo> responseMono;

    @Before
    public void init() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());

        NotificationAdapter adapter = new NotificationAdapter(baseUrl);

        RetrieveInformationRQ request = RetrieveInformationRQ.builder()
                .data(CustomerIdentificationRQ.builder()
                        .customerIdentification(CustomerIdentification.builder()
                                .documentType("CC")
                                .documentNumber("2101067991")
                                .build())
                        .build())
                .build();

        response = ResponseRetrieveInfo.builder()
                .data(ResponseRetrieveInfoData.builder()
                        .dynamicKeyIndicator("1")
                        .build())
                .build();

        mapper = new ObjectMapper();
        String messageId = "cf6f7b7c-5d94-4f8a-afc4-6b796da4bfbc";
        responseMono = adapter.callRetrieveInformation(request, messageId, 20L);
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void shouldGetBusinessOpportunitiesAdapter() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setBody(mapper.writeValueAsString(response)));

        StepVerifier.create(responseMono)
                .expectNextMatches(data -> data.getData().getDynamicKeyIndicator()
                        .equals("1"))
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
