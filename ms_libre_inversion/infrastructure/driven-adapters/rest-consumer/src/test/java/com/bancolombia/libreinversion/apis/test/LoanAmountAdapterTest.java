package com.bancolombia.libreinversion.apis.test;


import co.com.bancolombia.libreinversion.loanamount.LoanAmountAdapter;
import co.com.bancolombia.libreinversion.model.loanamount.*;
import co.com.bancolombia.libreinversion.model.stoc.*;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@WebFluxTest
@ContextConfiguration(classes = {LoanAmountAdapter.class})
public class LoanAmountAdapterTest {

    private MockWebServer mockWebServer;
    private SuccessResponseLoanAmount response;
    private ObjectMapper mapper;
    private Mono<SuccessResponseLoanAmount> responseMono;

    @Before
    public void init() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        LoanAmountAdapter adapter = new LoanAmountAdapter(baseUrl);
        response = LoanAmountTestUtil.response();

        mapper = new ObjectMapper();
        String messageId = "cf6f7b7c-5d94-4f8a-afc4-6b796da4bfbc";
        responseMono = adapter.getLoanAmount(LoanAmountTestUtil.request(), messageId);
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
                .expectNextMatches(data -> !data.getData().getQuota().isEmpty())
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

    @Test
    public void shouldGet504Status() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(504)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setBody(mapper.writeValueAsString(response)));
        StepVerifier.create(responseMono).verifyError();
    }
}
