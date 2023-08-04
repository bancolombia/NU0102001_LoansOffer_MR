package co.com.bancolombia.libreinversion.entrypoints.test;


import co.com.bancolombia.libreinversion.api.handler.EnableHandler;
import co.com.bancolombia.libreinversion.api.router.EnableRouter;
import co.com.bancolombia.libreinversion.model.account.rest.Identification;
import co.com.bancolombia.libreinversion.model.customer.Customer;
import co.com.bancolombia.libreinversion.model.customer.PersonalData;
import co.com.bancolombia.libreinversion.model.document.Document;
import co.com.bancolombia.libreinversion.model.offer.EnableOfferData;
import co.com.bancolombia.libreinversion.model.product.InterestRate;
import co.com.bancolombia.libreinversion.model.product.Product;
import co.com.bancolombia.libreinversion.model.request.EnableOfferRQ;
import co.com.bancolombia.libreinversion.model.request.RetrieveOfferRQ;
import co.com.bancolombia.libreinversion.usecase.stoc.OpportunitiesUseCase;
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

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {EnableRouter.class, EnableHandler.class})

@TestPropertySource(properties = {
        "spring.webflux.base-path=/api/v1",
        "entry-point.enable=/enable",
})
@WebFluxTest
public class EnableOfferTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private OpportunitiesUseCase opportunitiesUseCase;


    @Value("${spring.webflux.base-path}")
    private String basePath;

    @Value("${entry-point.enable}")
    private String routerEnable;

    RetrieveOfferRQ retrieveOfferRQ;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
    }

    @Before
    public void init() {

        retrieveOfferRQ = RetrieveOfferRQ.builder()
                .customer(getCustomerId())
                .products(getProducts())
                .build();

        Mockito.when(opportunitiesUseCase.getBusinessOpportunities(Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(enableOfferData()));
    }

    @Test
    public void test() {
        enable();
    }

    public void enable() {

        webTestClient
                .post()
                .uri((routerEnable))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Mono.just(enableOfferRQ()), EnableOfferRQ.class))
                .exchange().expectStatus()
                .isOk()
                .expectBody(EnableOfferData.class)
                .consumeWith((response) -> {
                    Assert.assertEquals(enableOfferData(), response.getResponseBody());
                });
    }

    private Customer getCustomerId() {
        return Customer.builder()
                .identification(getIdentification())
                .build();
    }

    private Identification getIdentification() {
        return Identification.builder().number("demo").build();
    }

    private List<Product> getProducts() {
        List<Product> productList = new ArrayList<>();
        Product product = Product.builder()
                .name("demo")
                .interestRates(getInteresRate())
                .build();
        productList.add(product);

        return productList;
    }

    private List<InterestRate> getInteresRate() {

        List<InterestRate> interestRatesList = new ArrayList<>();
        InterestRate interestRate = InterestRate.builder()
                .type("F")
                .build();
        interestRatesList.add(interestRate);

        return interestRatesList;
    }

    private EnableOfferData enableOfferData() {

        return EnableOfferData.builder()
                .customer(getCustomerId())
                .products(getProducts())
                .build();
    }

    private EnableOfferRQ enableOfferRQ() {

        List<Document> documents = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        List<InterestRate> interestRates = new ArrayList<>();
        InterestRate interestRate = InterestRate.builder()
                .type("demo")
                .build();
        interestRates.add(interestRate);
        Product product = Product.builder()
                .interestRates(interestRates)
                .build();
        products.add(product);

        Document document = Document.builder()
                .url(null)
                .format("PDF")
                .build();
        documents.add(document);

        PersonalData personalData = PersonalData.builder()
                .housingType("P")
                .realEstateOwner(true)
                .build();

        return EnableOfferRQ.builder()
                .saleType("P")
                .legalTrace("12345")
                .customer(Customer.builder().identification(getIdentification())
                        .build())
                .products(products)
                .personalData(personalData)
                .documents(documents)
                .build();
    }
}
