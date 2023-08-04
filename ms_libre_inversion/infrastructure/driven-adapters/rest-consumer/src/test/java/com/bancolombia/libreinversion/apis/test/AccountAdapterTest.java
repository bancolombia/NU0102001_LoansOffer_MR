package com.bancolombia.libreinversion.apis.test;

import co.com.bancolombia.libreinversion.account.AccountAdapter;
import co.com.bancolombia.libreinversion.model.account.rest.AccountCustomer;
import co.com.bancolombia.libreinversion.model.account.rest.AccountRQ;
import co.com.bancolombia.libreinversion.model.account.rest.AccountResponse;
import co.com.bancolombia.libreinversion.model.account.rest.DepositAccountData;
import co.com.bancolombia.libreinversion.model.account.rest.DepositAccountResponse;
import co.com.bancolombia.libreinversion.model.account.rest.DepositAccountResponseData;
import co.com.bancolombia.libreinversion.model.account.rest.Identification;
import co.com.bancolombia.libreinversion.model.account.rest.Pagination;
import co.com.bancolombia.libreinversion.model.account.rest.Participant;
import co.com.bancolombia.libreinversion.model.commons.Constant;
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
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@WebFluxTest
@ContextConfiguration(classes = {AccountAdapter.class})
public class AccountAdapterTest {

    private MockWebServer mockWebServer;
    private DepositAccountResponse response;
    private ObjectMapper mapper;
    private Mono<DepositAccountResponse> responseMono;

    @Before
    public void init() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());

        AccountAdapter adapter = new AccountAdapter(baseUrl);

        DepositAccountData request = DepositAccountData.builder()
                .account(AccountRQ.builder()
                        .allowCredit(true)
                        .allowDebit(true)
                        .participant(Participant.builder()
                                .relation(Constant.TITULAR)
                                .build())
                        .build())
                .pagination(Pagination.builder()
                        .key(1)
                        .size(30)
                        .build())
                .customer(AccountCustomer.builder()
                        .identification(Identification.builder()
                                .type("CC")
                                .number("000002101067975")
                                .build())
                        .build())
                .build();

        List<DepositAccountResponseData> accounts = new ArrayList<>();
        accounts.add(DepositAccountResponseData.builder()
                .account(AccountResponse.builder()
                        .type("CUENTA_CORRIENTE")
                        .number("40617975010")
                        .allowCredit(true)
                        .allowDebit(true)
                        .build())
                .build());

        response = DepositAccountResponse.builder()
                .data(accounts)
                .build();

        mapper = new ObjectMapper();
        String messageId = "cf6f7b7c-5d94-4f8a-afc4-6b796da4bfbc";
        responseMono = adapter.retrieveDepositAccounts(request, messageId);
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
                .expectNextMatches(data -> data.getData()
                        .get(0)
                        .getAccount().getNumber()
                        .equals("40617975010"))
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
