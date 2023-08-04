package co.com.bancolombia.libreinversion.usecase.test;


import co.com.bancolombia.libreinversion.model.account.rest.Identification;
import co.com.bancolombia.libreinversion.model.customer.Customer;
import co.com.bancolombia.libreinversion.model.notification.gateways.NotificationGateways;
import co.com.bancolombia.libreinversion.model.notification.rest.ResponseRetrieveInfoData;
import co.com.bancolombia.libreinversion.model.product.InterestRate;
import co.com.bancolombia.libreinversion.model.product.Product;
import co.com.bancolombia.libreinversion.model.product.ResponseData;
import co.com.bancolombia.libreinversion.model.product.RuleResponse;
import co.com.bancolombia.libreinversion.model.product.gateways.MAPGateways;
import co.com.bancolombia.libreinversion.model.request.RetrieveOfferRQ;
import co.com.bancolombia.libreinversion.usecase.notification.NotificationUseCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class NotificacionesUseCaseTest {

    @Mock
    private NotificationGateways notificationGateways;

    @Mock
    private MAPGateways mapGateways;

    @InjectMocks
    private NotificationUseCase notificationUseCase;

    RuleResponse ruleResponseTimeOffer;
    RetrieveOfferRQ retrieveOfferRQ;
    private String messageId = "123";

    @Before
    public void init() {

        Map<String, Object> map = new HashMap<>();
        ruleResponseTimeOffer = RuleResponse.builder()
                .data(ResponseData.builder().valid(true).utilLoad(map).build())
                .build();

        retrieveOfferRQ = RetrieveOfferRQ.builder()
                .customer(getCustomerId())
                .products(getProducts())
                .build();

        Mockito.doReturn(Mono.just(ruleResponseTimeOffer))
                .when(mapGateways).getTimeOffer();

        Mockito.doReturn(Mono.just(UtilRetrieveTestBuild.responseRetrieveInfo()))
                .when(notificationGateways)
                .callRetrieveInformation(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void test() {

        Mono<ResponseRetrieveInfoData> customerDataRes = notificationUseCase
                .retrieveInformation(retrieveOfferRQ, messageId);

        StepVerifier.create(customerDataRes)
                .expectSubscription()
                .thenConsumeWhile(x -> true)
                .verifyComplete();

        Mockito.verify(mapGateways, Mockito.atLeastOnce()).getTimeOffer();
        Mockito.verify(notificationGateways, Mockito.atLeastOnce())
                .callRetrieveInformation(Mockito.any(), Mockito.any(), Mockito.any());
    }

    private Customer getCustomerId() {
        return Customer.builder()
               .identification(getIdentification())
                .build();
    }

    private Identification getIdentification() {
        return Identification.builder().build();
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
                .build();
        interestRatesList.add(interestRate);

        return interestRatesList;
    }

}
