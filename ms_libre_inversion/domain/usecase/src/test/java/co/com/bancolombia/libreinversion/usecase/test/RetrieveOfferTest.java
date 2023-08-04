package co.com.bancolombia.libreinversion.usecase.test;


import co.com.bancolombia.libreinversion.model.account.DepositAccounts;
import co.com.bancolombia.libreinversion.model.account.gateways.AccountGateways;
import co.com.bancolombia.libreinversion.model.account.rest.Identification;
import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.customer.CustomerData;
import co.com.bancolombia.libreinversion.model.customer.Customer;
import co.com.bancolombia.libreinversion.model.customer.gateways.CustomerDataGateways;
import co.com.bancolombia.libreinversion.model.customer.rest.CustomerRQ;
import co.com.bancolombia.libreinversion.model.customer.rest.RequestBodyData;
import co.com.bancolombia.libreinversion.model.notification.gateways.NotificationGateways;
import co.com.bancolombia.libreinversion.model.notification.rest.CustomerIdentification;
import co.com.bancolombia.libreinversion.model.notification.rest.CustomerIdentificationRQ;
import co.com.bancolombia.libreinversion.model.notification.rest.RetrieveInformationRQ;
import co.com.bancolombia.libreinversion.model.product.Attribute;
import co.com.bancolombia.libreinversion.model.product.City;
import co.com.bancolombia.libreinversion.model.product.InterestRate;
import co.com.bancolombia.libreinversion.model.product.Product;
import co.com.bancolombia.libreinversion.model.product.ResponseData;
import co.com.bancolombia.libreinversion.model.product.RuleRequest;
import co.com.bancolombia.libreinversion.model.product.RuleResponse;
import co.com.bancolombia.libreinversion.model.product.gateways.MAPGateways;
import co.com.bancolombia.libreinversion.model.request.RetrieveOfferRQ;
import co.com.bancolombia.libreinversion.model.request.gateways.RedisGateways;
import co.com.bancolombia.libreinversion.usecase.account.AccountUseCase;
import co.com.bancolombia.libreinversion.usecase.customer.CustomerUseCase;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class RetrieveOfferTest {

    @Mock
    private CustomerDataGateways customerGateways;

    @Mock
    private NotificationGateways notificationGateways;

    @Mock
    private AccountGateways accountGateways;

    @Mock
    private MAPGateways mapGateways;

    @Mock
    private RedisGateways redisGateways;

    @InjectMocks
    private CustomerUseCase customerUseCase;

    @InjectMocks
    private AccountUseCase accountUseCase;

    RetrieveOfferRQ retrieveOfferRQ;
    CustomerData customerData;
    RuleResponse ruleResponseIdType;
    RuleResponse ruleResponsegment;
    RuleResponse ruleResponseTimeOffer;
    RequestBodyData rqBodyData;
    RetrieveInformationRQ retrieveInformationRQ;
    RetrieveInformationRQ rqNotificationData;
    RuleRequest ruleRequest;

    private String messageId = "123";
    private String productId = "123";

    @Before
    public void init() {

        retrieveOfferRQ = RetrieveOfferRQ.builder()
                .customer(getCustomerId())
                .products(getProducts())
                .build();

        rqBodyData = RequestBodyData.builder()
                .data(getData())
                .build();

        customerData = CustomerData.builder()
                .build();

        ruleResponseIdType = RuleResponse.builder()
                .data(ResponseData.builder().valid(true).build())
                .build();

        ruleResponsegment = RuleResponse.builder()
                .data(ResponseData.builder().valid(true).build())
                .build();

        Map<String, Object> map = new HashMap<>();

        ruleResponseTimeOffer = RuleResponse.builder()
                .data(ResponseData.builder().valid(true).utilLoad(map).build())
                .build();

        retrieveInformationRQ = RetrieveInformationRQ.builder()
                .data(getDateRetriveInfo())
                .build();

        rqNotificationData = RetrieveInformationRQ.builder()
                .data(
                        CustomerIdentificationRQ.builder()
                                .customerIdentification(
                                        CustomerIdentification.builder()
                                                .build()
                                )
                                .build()
                )
                .build();


        List<City> cities = new ArrayList<>();

        cities.add(City.builder()
                .build());

        cities.add(City.builder()
                .build());

        cities.add(City.builder()
                .build());

        cities.add(City.builder()
                .build());

        cities.add(City.builder()
                .build());


        List<Attribute> attributes = getArrayList(retrieveOfferRQ.getCustomer().getIdentification().getType(), "idType");
        ruleRequest = ruleRequest(attributes, "RuleIdType", productId);

        List<Attribute> attributesSeg = getArrayList(retrieveOfferRQ.getCustomer().getIdentification().getType(), "idType");
        RuleRequest ruleRequestSet = ruleRequest(attributesSeg, "RuleSegment", productId);

        Mockito.doReturn(Mono.just(ruleResponseTimeOffer)).when(mapGateways).getTimeOffer();
//        Mockito.doReturn(Mono.just(ruleResponseIdType)).when(mapGateways).ruleValidate(Mockito.any());
        Mockito.doReturn(Mono.just(ruleResponsegment)).when(mapGateways).ruleValidate(Mockito.any());
        Mockito.doReturn(Mono.just(cities)).when(mapGateways).getAllowedCities();

        Mockito.doReturn(Mono.just(UtilRetrieveTestBuild.responseCustomerCommercial()))
                .when(customerGateways)
                .callCommercialData(Mockito.any(), Mockito.any());

        Mockito.doReturn(Mono.just(UtilRetrieveTestBuild.responseCustomerContact()))
                .when(customerGateways)
                .callContactInformation(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.doReturn(Mono.just(UtilRetrieveTestBuild.responseCustomerDetail()))
                .when(customerGateways)
                .callDetailInformation(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.doReturn(Mono.just(UtilRetrieveTestBuild.responseRetrieveInfo()))
                .when(notificationGateways)
                .callRetrieveInformation(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.doReturn(Mono.just(new Object()))
                .when(redisGateways)
                .setData(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.doReturn(Mono.just(UtilRetrieveTestBuild.depositAccountResponse()))
                .when(accountGateways)
                .retrieveDepositAccounts(Mockito.any(), Mockito.any());

        Mockito.doReturn(Mono.just(Constant.ENABLED))
                .when(redisGateways)
                .getData(Mockito.any());
    }

    @Test
    public void getPersonalData() {
        retriveCustomerData();
    }

    private void retriveCustomerData() {

        Mono<CustomerData> customerDataRes = customerUseCase
                .getPersonalData(retrieveOfferRQ, messageId);

        Mono<DepositAccounts> retrieveAccounts = accountUseCase
                .retrieveAccounts(retrieveOfferRQ, messageId);

        StepVerifier.create(customerDataRes)
                .expectSubscription()
                .thenConsumeWhile(x -> true)
                .verifyComplete();

        StepVerifier.create(retrieveAccounts)
                .expectSubscription()
                .thenConsumeWhile(x -> true)
                .verifyComplete();

        Mockito.verify(mapGateways, Mockito.atLeastOnce()).getTimeOffer();
        Mockito.verify(mapGateways, Mockito.atLeastOnce()).ruleValidate(Mockito.any());
        Mockito.verify(mapGateways, Mockito.atLeastOnce()).getAllowedCities();

        Mockito.verify(customerGateways, Mockito.atLeastOnce()).callCommercialData(Mockito.any(), Mockito.any());
        Mockito.verify(customerGateways, Mockito.atLeastOnce()).callContactInformation(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(customerGateways, Mockito.atLeastOnce()).callDetailInformation(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(notificationGateways, Mockito.atLeastOnce()).callRetrieveInformation(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.verify(redisGateways, Mockito.atLeastOnce())
                .setData(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.verify(accountGateways, Mockito.atLeastOnce())
                .retrieveDepositAccounts(Mockito.any(), Mockito.any());

        Mockito.verify(redisGateways, Mockito.atLeastOnce())
                .getData(Mockito.any());
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

    private Customer getCustomerId() {
        return Customer.builder()
                .identification(getIdentification())
                .build();
    }

    private Identification getIdentification() {
        return Identification.builder().build();
    }

    private List<Attribute> getArrayList(String value, String name) {
        List<Attribute> attr = new ArrayList<>(
                Arrays.asList(Attribute.builder()
                        .name(name)
                        .value(value).build()));
        return attr;
    }

    private RuleRequest ruleRequest(List<Attribute> attr, String ruleName, String productId) {
        return RuleRequest
                .builder()
                .attributes(attr)
                .productCode(productId)
                .ruleName(ruleName).build();
    }

    private CustomerRQ getData() {
        return CustomerRQ.builder()
                .build();
    }

    private CustomerIdentificationRQ getDateRetriveInfo() {
        return CustomerIdentificationRQ.builder()
                .customerIdentification(CustomerIdentification.builder()
                        .build())
                .build();
    }
}
