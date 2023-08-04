package com.bancolombia.libreinversion.apis.test;

import co.com.bancolombia.libreinversion.customer.CustomerAdapter;
import co.com.bancolombia.libreinversion.model.customer.rest.CustomerRQ;
import co.com.bancolombia.libreinversion.model.customer.rest.RequestBodyData;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerCommercial;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerCommercialData;
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
@ContextConfiguration(classes = {CustomerAdapter.class})
public class CustomerComercialDataAdapterTest {

    private MockWebServer mockWebServer;
    private ResponseCustomerCommercial response;
    private ObjectMapper mapper;
    private Mono<ResponseCustomerCommercial> responseMono;

    @Before
    public void init() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());

        CustomerAdapter adapter = new CustomerAdapter(baseUrl);

        RequestBodyData request = RequestBodyData
                                    .builder()
                .data(CustomerRQ.builder()
                        .customerDocumentType("TIPDOC_FS001")
                        .customerDocumentId("2101067991")
                        .build()).build();

        response = ResponseCustomerCommercial.builder()
                .data(ResponseCustomerCommercialData.builder().role("ROLNEG_02").build())
                .build();

        mapper = new ObjectMapper();
        String messageId = "cf6f7b7c-5d94-4f8a-afc4-6b796da4bfbc";
        responseMono = adapter.callCommercialData(request, messageId);
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
                .expectNextMatches(data -> data
                .getData().getRole()
                        .equals("ROLNEG_02"))
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
