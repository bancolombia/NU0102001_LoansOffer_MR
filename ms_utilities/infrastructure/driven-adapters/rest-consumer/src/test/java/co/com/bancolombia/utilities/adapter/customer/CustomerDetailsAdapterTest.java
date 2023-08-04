package co.com.bancolombia.utilities.adapter.customer;

import co.com.bancolombia.utilities.model.customer.RequestCustomer;
import co.com.bancolombia.utilities.model.customer.RequestCustomerData;
import co.com.bancolombia.utilities.model.customer.ResponseCustomerDetails;
import co.com.bancolombia.utilities.model.customer.ResponseCustomerDetailsData;
import co.com.bancolombia.utilities.model.installments.Error;
import co.com.bancolombia.utilities.model.installments.Failure;
import co.com.bancolombia.utilities.model.installments.Meta;
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
@ContextConfiguration(classes = {CustomerDetailsAdapter.class})
public class CustomerDetailsAdapterTest {

    private MockWebServer mockWebServer;
    private ObjectMapper mapper;
    private ResponseCustomerDetails response;
    private Mono<ResponseCustomerDetails> responseMono;

    @Before
    public void init() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        CustomerDetailsAdapter adapter = new CustomerDetailsAdapter(baseUrl);

        RequestCustomer request = RequestCustomer.builder()
                .data(RequestCustomerData.builder()
                        .customerDocumentType("TIPDOC_FS001")
                        .customerDocumentId("244010333932009")
                        .queryType("")
                        .build())
                .build();

        response = ResponseCustomerDetails.builder()
                .data(ResponseCustomerDetailsData.builder()
                        .occupation("OCCUP_1")
                        .build())
                .build();

        mapper = new ObjectMapper();
        String msgId = "cf6f7b7c-5d94-4f8a-afc4-6b796da4bfbc";
        responseMono = adapter.retrieveCustomerDetails(request, msgId);
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void shouldRetrieveCustomerDetailsAdapter() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setBody(mapper.writeValueAsString(response)));

        StepVerifier.create(responseMono)
                .expectNextMatches(data -> data.getData()
                        .getOccupation()
                        .equals("OCCUP_1"))
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