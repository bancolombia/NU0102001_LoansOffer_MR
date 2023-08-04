package com.bancolombia.libreinversion.apis.test;

import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.DisbursementRQ;
import co.com.bancolombia.libreinversion.model.offer.selltestutil.UtilTestDisbursement;
import co.com.bancolombia.libreinversion.request.DisbursementsAdapter;
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
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@WebFluxTest
@ContextConfiguration(classes = {DisbursementsAdapter.class})
public class DisbursementsAdapterTest {

    private DisbursementsAdapter adapter;
    private MockWebServer mockWebServer;

    @Before
    public void init() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        adapter = new DisbursementsAdapter(baseUrl);
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void test() throws Exception {
        DisbursementRQ disbursementRQ = UtilTestDisbursement.buildDisbursementRQ();
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));

        StepVerifier.create(adapter.requestDisbursement(disbursementRQ, "123456"))
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
    }
}
