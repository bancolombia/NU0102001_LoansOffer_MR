package com.bancolombia.libreinversion.apis.test;

import co.com.bancolombia.libreinversion.model.rate.FixedRateLoans;
import co.com.bancolombia.libreinversion.model.rate.LoanInteresRate;
import co.com.bancolombia.libreinversion.model.rate.LoanInteresRateRQ;
import co.com.bancolombia.libreinversion.model.rate.LoanInteresRateResponse;
import co.com.bancolombia.libreinversion.model.rate.LoanRate;
import co.com.bancolombia.libreinversion.model.rate.RangeType;
import co.com.bancolombia.libreinversion.model.rate.RateRange;
import co.com.bancolombia.libreinversion.rate.InterestRateAdapter;
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
@ContextConfiguration(classes = {InterestRateAdapter.class})
public class InteresRateAdapterTest {

    private MockWebServer mockWebServer;
    private LoanInteresRateResponse response;
    private ObjectMapper mapper;
    private Mono<LoanInteresRateResponse> responseMono;

    @Before
    public void init() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        InterestRateAdapter adapter = new InterestRateAdapter(baseUrl);

        List<FixedRateLoans> fixedRateLoans = new ArrayList<>();
        FixedRateLoans fixedRateLoa = FixedRateLoans.builder()
                .fixedRateLoanId("P59").build();
        fixedRateLoans.add(fixedRateLoa);

        LoanInteresRateRQ request = LoanInteresRateRQ.builder()
                .data(LoanInteresRate.builder()
                        .customerSegment("S2")
                        .customerRelaibility("P59")
                        .amount(new BigDecimal("1"))
                        .loanTerm(new BigDecimal("1"))
                        .fixedRateLoans(fixedRateLoans)
                        .build())
                .build();

        List<RateRange> rateRanges = new ArrayList<>();
        List<RangeType> rangeTypes = new ArrayList<>();
        RangeType rangeType = RangeType.builder()
                .fixedTermDepositRate(new BigDecimal("0.3265"))
                .arrearsRate(new BigDecimal("0.3265")).build();
        rangeTypes.add(rangeType);

        RateRange rateRange = RateRange.builder().rangeType(rangeTypes).build();
        rateRanges.add(rateRange);

        response = LoanInteresRateResponse.builder()
                .data(LoanRate.builder()
                        .rateRange(rateRanges).build())
                .build();

        mapper = new ObjectMapper();
        String messageId = "cf6f7b7c-5d94-4f8a-afc4-6b796da4bfbc";
        responseMono = adapter.callLoanInteresRate(request, messageId, "2101067991");
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
                .expectNextMatches(data -> data.getData().getRateRange().size() > 0)
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
