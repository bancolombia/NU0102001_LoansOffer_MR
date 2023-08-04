package co.com.bancolombia.libreinversion.usecase.test;


import co.com.bancolombia.libreinversion.model.account.rest.Identification;
import co.com.bancolombia.libreinversion.model.account.rest.Property;
import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.commons.TextLegalInformation;
import co.com.bancolombia.libreinversion.model.customer.CustomerData;
import co.com.bancolombia.libreinversion.model.customer.Customer;
import co.com.bancolombia.libreinversion.model.customer.PersonalData;
import co.com.bancolombia.libreinversion.model.customer.gateways.CustomerDataGateways;
import co.com.bancolombia.libreinversion.model.customer.rest.CustomerRQ;
import co.com.bancolombia.libreinversion.model.customer.rest.MetaData;
import co.com.bancolombia.libreinversion.model.customer.rest.RequestBodyData;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerCommercial;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerCommercialData;
import co.com.bancolombia.libreinversion.model.document.Document;
import co.com.bancolombia.libreinversion.model.loanamount.LoanAmountTestUtil;
import co.com.bancolombia.libreinversion.model.loanamount.gateways.LoanAmountGateway;
import co.com.bancolombia.libreinversion.model.notification.gateways.NotificationGateways;
import co.com.bancolombia.libreinversion.model.notification.rest.CustomerIdentification;
import co.com.bancolombia.libreinversion.model.notification.rest.CustomerIdentificationRQ;
import co.com.bancolombia.libreinversion.model.notification.rest.RetrieveInformationRQ;
import co.com.bancolombia.libreinversion.model.offer.EnableOfferData;
import co.com.bancolombia.libreinversion.model.product.Amount;
import co.com.bancolombia.libreinversion.model.product.Attribute;
import co.com.bancolombia.libreinversion.model.product.InterestRate;
import co.com.bancolombia.libreinversion.model.product.Product;
import co.com.bancolombia.libreinversion.model.product.ResponseData;
import co.com.bancolombia.libreinversion.model.product.RuleRequest;
import co.com.bancolombia.libreinversion.model.product.RuleResponse;
import co.com.bancolombia.libreinversion.model.product.gateways.MAPGateways;
import co.com.bancolombia.libreinversion.model.rate.LoanInteresRateResponse;
import co.com.bancolombia.libreinversion.model.rate.LoanRate;
import co.com.bancolombia.libreinversion.model.rate.RangeType;
import co.com.bancolombia.libreinversion.model.rate.RateRange;
import co.com.bancolombia.libreinversion.model.rate.gateways.InterestRateAdapterGateways;
import co.com.bancolombia.libreinversion.model.request.EnableOfferRQ;
import co.com.bancolombia.libreinversion.model.request.RetrieveOfferRQ;
import co.com.bancolombia.libreinversion.model.request.gateways.RedisGateways;
import co.com.bancolombia.libreinversion.model.stoc.AlternativeCapacities;
import co.com.bancolombia.libreinversion.model.stoc.GeneralConditions;
import co.com.bancolombia.libreinversion.model.stoc.GeneralInformation;
import co.com.bancolombia.libreinversion.model.stoc.PreapprovedDetail;
import co.com.bancolombia.libreinversion.model.stoc.gateways.OpportunitiesGateways;
import co.com.bancolombia.libreinversion.usecase.stoc.OpportunitiesUseCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class EnableOfferTest {


    @Mock
    private OpportunitiesGateways opportunitiesGateway;

    @Mock
    private InterestRateAdapterGateways interestRateAdapterGateways;

    @Mock
    private RedisGateways redisGateways;

    @Mock
    private CustomerDataGateways customerGateways;

    @Mock
    private NotificationGateways notificationGateways;

    @Mock
    private MAPGateways mapGateways;

    @Mock
    private LoanAmountGateway loanAmountGateway;

    @InjectMocks
    private OpportunitiesUseCase opportunitiesUseCase;

    RetrieveOfferRQ retrieveOfferRQ;
    CustomerData customerData;
    RuleResponse ruleResponseIdType;
    RuleResponse ruleResponseSDI;
    RuleResponse ruleResponseSDE;
    RuleResponse ruleResponseTimeOffer;
    RequestBodyData rqBodyData;
    RetrieveInformationRQ retrieveInformationRQ;
    RetrieveInformationRQ rqNotificationData;
    EnableOfferRQ enableOfferRQ;

    private String messageId = "123";

    @Before
    public void init() {

        List<Document> documents = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        List<InterestRate> interestRates = new ArrayList<>();
        InterestRate interestRate = InterestRate.builder()
                .type("F")
                .build();
        interestRates.add(interestRate);
        Product product = Product.builder()
                .interestRates(interestRates)
                .build();
        products.add(product);

        Document document = Document.builder()
                .url(null)
                .build();
        documents.add(document);

        PersonalData personalData = PersonalData.builder()
                .realEstateOwner(true)
                .otherIncomes(null)
                .variableIncome(true)
                .variableSalary(null)
                .rentFeesIncomes(null)
                .freelancerIncomes(null)
                .totalPayrollDeductions(null)
                .alternativeCellPhoneNumber(null)
                .build();

        enableOfferRQ = EnableOfferRQ.builder()
                .customer(Customer.builder()
                        .identification(getIdentification())
                        .build())
                .products(products)
                .personalData(personalData)
                .documents(documents)
                .build();

        retrieveOfferRQ = RetrieveOfferRQ.builder()
                .customer(getCustomerId())
                .products(getProducts())
                .build();

        rqBodyData = RequestBodyData.builder()
                .data(getData())
                .build();

        customerData = CustomerData.builder()
                .companyNames(null)
                .build();

        Map<String, Object> mapinv = new HashMap<>();

        Map<String, Object> mapinvSDI = new HashMap<>();

        Map<String, Object> mapinvSDE = new HashMap<>();

        Map<String, Object> mapPlanes = new HashMap<>();

        RuleResponse ruleResponsePlanes = RuleResponse.builder()
                .data(ResponseData.builder().utilLoad(mapPlanes).valid(true).build()).build();

        ruleResponseIdType = RuleResponse.builder()
                .data(ResponseData.builder().utilLoad(mapinv).valid(true).build()).build();

        ruleResponseSDI = RuleResponse.builder()
                .data(ResponseData.builder().valid(true).utilLoad(mapinvSDI).build())
                .build();

        ruleResponseSDE = RuleResponse.builder()
                .data(ResponseData.builder().valid(true).utilLoad(mapinvSDE).build())
                .build();

        List<TextLegalInformation> text = new ArrayList<>();
        text.add(TextLegalInformation.builder().key("test").detail("test").build());
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

        Mockito.doReturn(Mono.just(ruleResponseTimeOffer)).when(mapGateways).getTimeOffer();
        Mockito.doReturn(Mono.just(ruleResponseIdType)).when(mapGateways).ruleValidate(Mockito.any());
        Mockito.doReturn(Mono.just(getAmount())).when(mapGateways).getAmounts();

        Mockito.doReturn(Mono.just(responseCustomerCommercial()))
                .when(opportunitiesGateway)
                .getBusinessOportunities(Mockito.any(), Mockito.any());

        Mockito.doReturn(Mono.just(UtilRetrieveTestBuild.responseCustomerContact()))
                .when(customerGateways)
                .callContactInformation(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.doReturn(Mono.just(UtilRetrieveTestBuild.responseCustomerCommercial()))
                .when(customerGateways)
                .callCommercialData(Mockito.any(), Mockito.any());

        Mockito.doReturn(Mono.just(UtilRetrieveTestBuild.responseCustomerDetail()))
                .when(customerGateways)
                .callDetailInformation(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.doReturn(Mono.just(UtilRetrieveTestBuild.responseRetrieveInfo()))
                .when(notificationGateways)
                .callRetrieveInformation(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.doReturn(Mono.just(getInteresRateReponse()))
                .when(interestRateAdapterGateways)
                .callLoanInteresRate(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.doReturn(Mono.just(LoanAmountTestUtil.response()))
                .when(loanAmountGateway)
                .getLoanAmount(Mockito.any(), Mockito.any());

        Mockito.doReturn(Mono.just(Constant.ENABLED))
                .when(redisGateways)
                .setData(Mockito.any(), Mockito.any(), Mockito.any());
    }

    private Identification getIdentification() {
        return Identification.builder().build();
    }

    private List<Amount> getAmount() {

        List<Amount> amounts = new ArrayList<>();

        amounts.add(
                Amount.builder()
                        .build()
        );

        amounts.add(
                Amount.builder()
                        .build()
        );

        amounts.add(
                Amount.builder()
                        .build()
        );

        return amounts;
    }

    @Test
    public void getPersonalData() {
        retriveCustomerData();
    }

    private void retriveCustomerData() {

        Mono<EnableOfferData> enableOfferDataMono = opportunitiesUseCase
                .getBusinessOpportunities(enableOfferRQ, messageId);

        StepVerifier.create(enableOfferDataMono)
                .expectSubscription()
                .thenConsumeWhile(x -> true)
                .verifyComplete();

        Mockito.verify(mapGateways, Mockito.atLeastOnce()).getTimeOffer();
        Mockito.verify(mapGateways, Mockito.atLeastOnce()).ruleValidate(Mockito.any());
        Mockito.verify(mapGateways, Mockito.atLeastOnce()).ruleValidate(Mockito.any());
        Mockito.verify(mapGateways, Mockito.atLeastOnce()).ruleValidate(Mockito.any());
        Mockito.verify(mapGateways, Mockito.atLeastOnce()).getAmounts();
        Mockito.verify(mapGateways, Mockito.atLeastOnce()).ruleValidate(Mockito.any());

        Mockito.verify(opportunitiesGateway, Mockito.atLeastOnce()).getBusinessOportunities(Mockito.any(), Mockito.any());
        Mockito.verify(customerGateways, Mockito.atLeastOnce()).callContactInformation(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.verify(customerGateways, Mockito.atLeastOnce())
                .callCommercialData(Mockito.any(), Mockito.any());

        Mockito.verify(customerGateways, Mockito.atLeastOnce()).callDetailInformation(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(notificationGateways, Mockito.atLeastOnce()).callRetrieveInformation(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.verify(interestRateAdapterGateways, Mockito.atLeastOnce()).callLoanInteresRate(
                Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.verify(loanAmountGateway, Mockito.atLeastOnce()).getLoanAmount(
                Mockito.any(), Mockito.any());
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

    private GeneralInformation responseCustomerCommercial() {

        List<AlternativeCapacities> alternativeCapacities = new ArrayList<>();

        AlternativeCapacities alternativeCapacities1 = AlternativeCapacities.builder()
                .build();
        alternativeCapacities.add(alternativeCapacities1);

        return GeneralInformation.builder()
                .reason("dfgdfgdfg")
                .commercialTeamCode(1)
                .priority(1)
                .preapprovedDetail(
                        PreapprovedDetail.builder()
                                .build()
                )
                .generalConditions(GeneralConditions.builder()
                        .build())
                .payrollDeductionConditions(null)
                .purchasePortfolioConditions(null)
                .mortgageConditions(null)
                .alternativeCapacities(alternativeCapacities)
                .build();
    }


    private LoanInteresRateResponse getInteresRateReponse() {

        List<RangeType> rangeType = new ArrayList<>();

        rangeType.add(RangeType.builder()
                .build());

        List<RateRange> rateRange = new ArrayList<>();
        RateRange rateR = RateRange.builder()
                .rangeType(rangeType)
                .build();
        rateRange.add(rateR);

        return LoanInteresRateResponse.builder()
                .meta(null)
                .data(LoanRate.builder()
                        .rateRange(rateRange)
                        .build())
                .build();
    }

    private List<Property> getEspecification() {

        List<Property> properties = new ArrayList<>();

        properties.add(
                Property.builder()
                        .value(false)
                        .build()
        );

        properties.add(
                Property.builder()
                        .value(false)
                        .build()
        );

        properties.add(
                Property.builder()
                        .value(false)
                        .build()
        );

        properties.add(
                Property.builder()
                        .value(false)
                        .build()
        );

        properties.add(
                Property.builder()
                        .build()
        );

        return properties;
    }
}
