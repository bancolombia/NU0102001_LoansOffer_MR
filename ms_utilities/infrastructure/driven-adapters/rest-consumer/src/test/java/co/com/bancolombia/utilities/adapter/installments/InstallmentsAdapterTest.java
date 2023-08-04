package co.com.bancolombia.utilities.adapter.installments;

import co.com.bancolombia.utilities.model.installments.Error;
import co.com.bancolombia.utilities.model.installments.Failure;
import co.com.bancolombia.utilities.model.installments.FeeConcept;
import co.com.bancolombia.utilities.model.installments.Header;
import co.com.bancolombia.utilities.model.installments.Insurance;
import co.com.bancolombia.utilities.model.installments.Meta;
import co.com.bancolombia.utilities.model.installments.RegularFeeConcept;
import co.com.bancolombia.utilities.model.installments.RequestInstallments;
import co.com.bancolombia.utilities.model.installments.RequestInstallmentsData;
import co.com.bancolombia.utilities.model.installments.ResponseInstallments;
import co.com.bancolombia.utilities.model.installments.ResponseInstallmentsData;
import co.com.bancolombia.utilities.model.installments.TopLevelLinks;
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
@ContextConfiguration(classes = {InstallmentsAdapter.class})
public class InstallmentsAdapterTest {

    private MockWebServer mockWebServer;
    private ObjectMapper mapper;
    private ResponseInstallments response;
    private Mono<ResponseInstallments> responseMono;

    @Before
    public void init() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        InstallmentsAdapter adapter = new InstallmentsAdapter(baseUrl);

        RequestInstallments request = RequestInstallments.builder()
                .data(RequestInstallmentsData.builder()
                        .rediscountType("0")
                        .loanId("9430082543")
                        .creditPlan("P04")
                        .amount(new BigDecimal("170000.23"))
                        .term(60)
                        .paymentFrequency(1)
                        .basisPoints(new BigDecimal("6.7"))
                        .nominalInterestRate(new BigDecimal("1.1"))
                        .build())
                .build();

        List<FeeConcept> feeConcepts = new ArrayList<>();
        feeConcepts.add(FeeConcept.builder().amount(new BigDecimal("500")).type("1").build());

        List<RegularFeeConcept> regularFeeConcepts = new ArrayList<>();
        regularFeeConcepts.add(RegularFeeConcept.builder().amount(new BigDecimal("500")).type("1").build());

        response = ResponseInstallments.builder()
                .meta(Meta.builder().build())
                .data(ResponseInstallmentsData.builder()
                        .header(Header.builder().id("1").build())
                        .feeConcepts(feeConcepts)
                        .regularFeeConcepts(regularFeeConcepts)
                        .build())
                .links(TopLevelLinks.builder().build())
                .build();

        mapper = new ObjectMapper();
        String msgId = "cf6f7b7c-5d94-4f8a-afc4-6b796da4bfbc";
        responseMono = adapter.retrieveInstallments(request, msgId);
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
                .expectNextMatches(data -> data.getData()
                        .getHeader()
                        .getId()
                        .equals("1"))
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