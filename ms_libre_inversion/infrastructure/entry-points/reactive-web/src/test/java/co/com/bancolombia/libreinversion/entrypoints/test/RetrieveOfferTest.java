package co.com.bancolombia.libreinversion.entrypoints.test;


import co.com.bancolombia.libreinversion.api.handler.RetrieveHandler;
import co.com.bancolombia.libreinversion.api.router.RetrieveRouter;
import co.com.bancolombia.libreinversion.model.account.Account;
import co.com.bancolombia.libreinversion.model.account.DepositAccounts;
import co.com.bancolombia.libreinversion.model.account.rest.Identification;
import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.customer.CustomerData;
import co.com.bancolombia.libreinversion.model.customer.Customer;
import co.com.bancolombia.libreinversion.model.offer.OffersOperation;
import co.com.bancolombia.libreinversion.model.offer.RetrieveOfferData;
import co.com.bancolombia.libreinversion.model.product.InterestRate;
import co.com.bancolombia.libreinversion.model.product.Product;
import co.com.bancolombia.libreinversion.model.request.RetrieveOfferRQ;
import co.com.bancolombia.libreinversion.usecase.account.AccountUseCase;
import co.com.bancolombia.libreinversion.usecase.customer.CustomerUseCase;
import co.com.bancolombia.libreinversion.usecase.redis.RedisUseCase;
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
@ContextConfiguration(classes = {RetrieveRouter.class, RetrieveHandler.class})

@TestPropertySource(properties = {
        "spring.webflux.base-path=/api/v1",
        "entry-point.retrieve=/retrieve",
})
@WebFluxTest
public class RetrieveOfferTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AccountUseCase accountUseCase;

    @MockBean
    private CustomerUseCase customerUseCase;

    @MockBean
    private RedisUseCase redisUseCase;


    @Value("${spring.webflux.base-path}")
    private String basePath;

    @Value("${entry-point.retrieve}")
    private String routeRetrieve;

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

        Mockito.when(customerUseCase.getPersonalData(Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(responseCustomerData()));

        Mockito.when(accountUseCase.retrieveAccounts(Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(depositAccounts()));

        Mockito.when(redisUseCase.getDataOperation(Mockito.any()))
                .thenReturn(Mono.just(getDataOperation()));
    }

    @Test
    public void test() {
        retrieve();
    }

    public void retrieve() {

        webTestClient
                .post()
                .uri((routeRetrieve))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Mono.just(retrieveOfferRQ), RetrieveOfferRQ.class))
                .exchange().expectStatus()
                .isOk()
                .expectBody(RetrieveOfferData.class)
                .consumeWith((response) -> {
                    Assert.assertEquals(retrieveOfferData(), response.getResponseBody());
                });
    }

    private Customer getCustomerId() {
        return Customer.builder()
                .identification(getIdentification())
                .build();
    }

    private Identification getIdentification() {
        return Identification.builder() .type("TIPDOC_FS001").number("2101067981").build();
    }

    private List<Product> getProducts() {
        List<Product> productList = new ArrayList<>();
        Product product = Product.builder()
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

    private CustomerData responseCustomerData() {

        return CustomerData.builder()
                .companyNames(null)
                .applicationStatus(Constant.ENABLED)
                .build();
    }

    private RetrieveOfferData retrieveOfferData() {

        return RetrieveOfferData.builder()
                .customerData(responseCustomerData())
                .customer(getCustomerId())
                .depositAccounts(getAccount())
                .offers(getDataOperation())
                .header(null)
                .build();
    }

    private DepositAccounts depositAccounts() {

        return DepositAccounts.builder()
                .account(getAccount())
                .build();
    }

    private List<Account> getAccount() {
        List<Account> depositAccountResponses = new ArrayList<>();
        depositAccountResponses.add(
                Account.builder()
                        .type("CUENTA_DE_AHORRO")
                        .number("40677981025")
                        .build()
        );

        return depositAccountResponses;
    }

    private List<OffersOperation> getDataOperation() {
        List<OffersOperation> objectList = new ArrayList<>();
        return objectList;
    }
}
