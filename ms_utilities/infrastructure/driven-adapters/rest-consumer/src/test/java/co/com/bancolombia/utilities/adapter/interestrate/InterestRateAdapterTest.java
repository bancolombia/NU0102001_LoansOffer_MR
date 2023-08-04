package co.com.bancolombia.utilities.adapter.interestrate;

import co.com.bancolombia.utilities.model.installments.Error;
import co.com.bancolombia.utilities.model.installments.Failure;
import co.com.bancolombia.utilities.model.installments.Meta;
import co.com.bancolombia.utilities.model.interestrate.FixedRateLoans;
import co.com.bancolombia.utilities.model.interestrate.RangeType;
import co.com.bancolombia.utilities.model.interestrate.RateRange;
import co.com.bancolombia.utilities.model.interestrate.RequestInterestRate;
import co.com.bancolombia.utilities.model.interestrate.RequestInterestRateData;
import co.com.bancolombia.utilities.model.interestrate.ResponseInterestRate;
import co.com.bancolombia.utilities.model.interestrate.ResponseInterestRateData;
import co.com.bancolombia.utilities.model.interestrate.VariableRateLoans;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
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

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@WebFluxTest
@ContextConfiguration(classes = {InterestRateAdapter.class})
public class InterestRateAdapterTest {

    private MockWebServer mockWebServer;
    private ObjectMapper mapper;
    private ResponseInterestRate response;
    private Mono<ResponseInterestRate> responseMono;

    @Before
    public void init() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        InterestRateAdapter adapter = new InterestRateAdapter(baseUrl);

        List<FixedRateLoans> fixedRateLoans = new ArrayList<>();
        fixedRateLoans.add(FixedRateLoans.builder().fixedRateLoanId("P99").build());

        List<VariableRateLoans> variableRateLoans = new ArrayList<>();
        variableRateLoans.add(VariableRateLoans.builder().variableRateLoanType("P99").build());

        RequestInterestRate request = RequestInterestRate.builder()
                .data(RequestInterestRateData.builder()
                        .customerSegment("S1")
                        .customerRelaibility("G1")
                        .fixedRateLoans(fixedRateLoans)
                        .variableRateLoans(variableRateLoans)
                        .amount(new BigDecimal("1"))
                        .loanTerm(1)
                        .build())
                .build();

        List<RangeType> rangeTypeList = new ArrayList<>();
        rangeTypeList.add(RangeType.builder()
                .monthlyRate(1.799917)
                .arrearsRate(0.21599)
                .effectiveAnnualRate(0.238708)
                .annualNominalMonthlyRate(0.21599)
                .rateType("F")
                .fixedTermDepositRate(0.0)
                .build());

        List<RateRange> rateRangeList = new ArrayList<>();
        rateRangeList.add(RateRange.builder()
                .rangeType(rangeTypeList)
                .build());

        response = ResponseInterestRate.builder()
                .data(ResponseInterestRateData.builder()
                        .rateRange(rateRangeList)
                        .build())
                .build();

        mapper = new ObjectMapper();
        String msgId = "cf6f7b7c-5d94-4f8a-afc4-6b796da4bfbc";
        responseMono = adapter.retrieveInterestRate(request, msgId);
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void shouldRetrieveInterestRateAdapter() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setBody(mapper.writeValueAsString(response)));

        StepVerifier.create(responseMono)
                .expectNextMatches(data -> data.getData()
                        .getRateRange().get(0)
                        .getRangeType().get(0)
                        .getRateType()
                        .equals("F"))
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
    }

    @Test
    public void shouldGetError() {
        Error error = Error.builder().code("01").detail("").build();

        List<Error> errors = new ArrayList<>();
        errors.add(error);

        Failure failure = Failure.builder()
                .meta(Meta.builder().build())
                .status(new BigDecimal("200"))
                .title("Ok")
                .errors(errors)
                .build();

        Assertions.assertEquals("01", error.getCode());
        Assertions.assertEquals(new BigDecimal("200"), failure.getStatus());
        Assertions.assertEquals("Ok", failure.getTitle());
    }
}