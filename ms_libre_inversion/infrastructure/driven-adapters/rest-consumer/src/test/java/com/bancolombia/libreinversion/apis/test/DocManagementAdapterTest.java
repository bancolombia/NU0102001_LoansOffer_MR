package com.bancolombia.libreinversion.apis.test;

import co.com.bancolombia.libreinversion.docmanagement.DocManagementAdapter;
import co.com.bancolombia.libreinversion.model.offer.selltestutil.UtilTestSellRQ;
import co.com.bancolombia.libreinversion.model.docmanagement.SignDocumentsReq;
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
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@WebFluxTest
@ContextConfiguration(classes = {DocManagementAdapter.class})
public class DocManagementAdapterTest {

    private MockWebServer mockWebServer;
    private DocManagementAdapter adapter;
    private ObjectMapper mapper;

    @Before
    public void init() throws IOException {
        mapper = new ObjectMapper();
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        adapter = new DocManagementAdapter(baseUrl);
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void test() throws Exception {
        SignDocumentsReq signDocumentRQ = UtilTestSellRQ.buildSignDocumentsReq();
        byte[] signDocumentRS = "200 ok".getBytes();
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));

        StepVerifier.create(adapter.signDocument(signDocumentRQ, "123456", "prueba", "prueba123"))

                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
    }
}
