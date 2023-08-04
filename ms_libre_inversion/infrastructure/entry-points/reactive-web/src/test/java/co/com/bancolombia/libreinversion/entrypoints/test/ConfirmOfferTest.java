package co.com.bancolombia.libreinversion.entrypoints.test;


import co.com.bancolombia.libreinversion.api.handler.ConfirmHandler;
import co.com.bancolombia.libreinversion.api.router.ConfirmRouter;
import co.com.bancolombia.libreinversion.model.account.rest.Identification;
import co.com.bancolombia.libreinversion.model.customer.Customer;
import co.com.bancolombia.libreinversion.model.document.Document;
import co.com.bancolombia.libreinversion.model.offer.Offer;
import co.com.bancolombia.libreinversion.model.offer.selltestutil.UtilTestSellRQ;
import co.com.bancolombia.libreinversion.model.request.Insurances;
import co.com.bancolombia.libreinversion.model.request.*;
import co.com.bancolombia.libreinversion.usecase.redis.RedisUseCase;
import co.com.bancolombia.libreinversion.usecase.request.ConfirmOfferCompleteUseCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ConfirmRouter.class, ConfirmHandler.class})

@TestPropertySource(properties = {
        "spring.webflux.base-path=/api/v1",
        "entry-point.confirm=/confirm",
})
@WebFluxTest
public class ConfirmOfferTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ConfirmOfferCompleteUseCase confirmOfferCompleteUseCase;

    @MockBean
    private RedisUseCase redisUseCase;


    @Value("${spring.webflux.base-path}")
    private String basePath;

    @Value("${entry-point.confirm}")
    private String routeConfirm;

    ConfirmOfferRQ confirmOfferRQ;
    private static int STATUS_HTTP = 200;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
    }

    @Before
    public void init() {

        confirmOfferRQ = ConfirmOfferRQ
                .builder()
                .legalTrace("12345")
                .customer(getCustomer())
                .offer(getOffer())
                .insurances(getInsurance())
                .partial(true)
                .build();


        Mockito.when(confirmOfferCompleteUseCase.comfirmComplete(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(getResponseConfirm()));

        Mockito.when(redisUseCase.setData(Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(confirmOfferRQ));
    }

    @Test
    public void testParcial() {
        confirmParcial();
    }

    @Test
    public void testComplete() {
        confirmComplete();
    }

    public void confirmParcial() {

        webTestClient
                .post()
                .uri((routeConfirm))
                .accept(MediaType.APPLICATION_JSON)
                .header("message-id", "eda2e01f-2353-4810-84c8-16472c3c4414")
                .body(BodyInserters.fromPublisher(Mono.just(confirmOfferRQ), ConfirmOfferRQ.class))
                .exchange().expectStatus()
                .isOk()
                .expectBody(ConfirmOfferRQ.class)
                .consumeWith((response) -> {
                    Assert.assertEquals(response.getStatus().value(), STATUS_HTTP);
                });
    }

    public void confirmComplete() {
        webTestClient
                .post()
                .uri((routeConfirm))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Mono.just(confirmOfferRQ.toBuilder().partial(false).build()), ConfirmOfferRQ.class))
                .exchange().expectStatus()
                .isOk()
                .expectBody(ConfirmOfferResponse.class)
                .consumeWith((response) -> {
                    Assert.assertEquals(getResponseConfirm(), response.getResponseBody());
                });
    }

    private Customer getCustomer() {
        return Customer
                .builder()
                .identification(getIdentification())
                .build();
    }

    private Identification getIdentification() {
        return Identification.builder().type("demo").number("demo").build();
    }

    private Offer getOffer() {

        return Offer.builder()
                .id("123456777")
                .amount("560000")
                .term("123")
                .interestRateType("F")
                .disbursementDestination(getDisburment())
                .paymentDay(1)
                .build();
    }

    private DisbursementDestination getDisburment() {
        return DisbursementDestination.builder().destination(getDestination()).build();
    }

    private List<Destination> getDestination() {
        List<Destination> destinations = new ArrayList<>();

        Destination des = Destination.builder().destinationId("123456789")
                .destinationType("demo")
                .amount(3000)
                .beneficiary(UtilTestSellRQ.getAccountBeneficiary()).build();
        destinations.add(des);

        des = Destination.builder().destinationId("123456789")
                .destinationType("demo")
                .amount(3000)
                .beneficiary(null).build();
        destinations.add(des);

        return destinations;
    }

    private List<Insurances> getInsurance() {

        List<Insurances> insurances = new ArrayList<>();
        Insurances insurance = Insurances
                .builder()
                .type("demo")
                .beneficiaries(UtilTestSellRQ.getBeneficiary())
                .build();
        insurances.add(insurance);

        return insurances;
    }

    private ConfirmOfferResponse getResponseConfirm() {

        List<Document> document = new ArrayList<>();
        document.add(Document.builder().format("html").url(getUrl()).build());
        document.add(Document.builder().format("html").url(getUrl()).build());
        document.add(Document.builder().format("html").url(getUrl()).build());

        return ConfirmOfferResponse.builder()
                .data(
                        ConfirmResponseData.builder()
                                .documents(
                                        DocumentResponse.builder()
                                                .document(document)
                                                .build()
                                )
                                .build()
                )
                .build();
    }

    private String getUrl() {
        URL url = null;
        String output = null;

        try {
            url = new URL("https", "demo", "/");
            output = url.toExternalForm();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return output;
    }
}
