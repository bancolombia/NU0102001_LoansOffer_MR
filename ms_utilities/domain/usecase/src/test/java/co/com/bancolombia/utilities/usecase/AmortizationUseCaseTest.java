package co.com.bancolombia.utilities.usecase;

import co.com.bancolombia.utilities.model.Customer;
import co.com.bancolombia.utilities.model.Identification;
import co.com.bancolombia.utilities.model.InsuranceRequest;
import co.com.bancolombia.utilities.model.InsurancesRequest;
import co.com.bancolombia.utilities.model.Offer;
import co.com.bancolombia.utilities.model.RequestAmortization;
import co.com.bancolombia.utilities.model.RequestAmortizationData;
import co.com.bancolombia.utilities.model.ResponseAmortization;
import co.com.bancolombia.utilities.model.customer.Contact;
import co.com.bancolombia.utilities.model.customer.ResponseCustomerCommercial;
import co.com.bancolombia.utilities.model.customer.ResponseCustomerCommercialData;
import co.com.bancolombia.utilities.model.customer.ResponseCustomerContact;
import co.com.bancolombia.utilities.model.customer.ResponseCustomerContactData;
import co.com.bancolombia.utilities.model.customer.ResponseCustomerDetails;
import co.com.bancolombia.utilities.model.customer.ResponseCustomerDetailsData;
import co.com.bancolombia.utilities.model.gateways.CustomerCommercialGateway;
import co.com.bancolombia.utilities.model.gateways.CustomerContactGateway;
import co.com.bancolombia.utilities.model.gateways.CustomerDetailsGateway;
import co.com.bancolombia.utilities.model.gateways.InstallmentsGateway;
import co.com.bancolombia.utilities.model.gateways.InterestRateGateway;
import co.com.bancolombia.utilities.model.gateways.MapGateway;
import co.com.bancolombia.utilities.model.installments.FeeConcept;
import co.com.bancolombia.utilities.model.installments.RegularFeeConcept;
import co.com.bancolombia.utilities.model.installments.ResponseInstallments;
import co.com.bancolombia.utilities.model.installments.ResponseInstallmentsData;
import co.com.bancolombia.utilities.model.interestrate.RangeType;
import co.com.bancolombia.utilities.model.interestrate.RateRange;
import co.com.bancolombia.utilities.model.interestrate.ResponseInterestRate;
import co.com.bancolombia.utilities.model.interestrate.ResponseInterestRateData;
import co.com.bancolombia.utilities.model.product.ResponseData;
import co.com.bancolombia.utilities.model.product.RuleResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AmortizationUseCaseTest {

    @InjectMocks
    private AmortizationUseCase useCase;

    @Mock
    private CustomerContactGateway customerContactGateway;

    @Mock
    private CustomerCommercialGateway customerCommercialGateway;

    @Mock
    private CustomerDetailsGateway customerDetailsGateway;

    @Mock
    private InterestRateGateway interestRateGateway;

    @Mock
    private InstallmentsGateway installmentsGateway;

    @Mock
    private MapGateway mapGateway;
    private RequestAmortization request;
    String msgId = "cf6f7b7c-5d94-4f8a-afc4-6b796da4bfbc";
    List<InsuranceRequest> insurances = new ArrayList<>();
    InsurancesRequest insurancesRequest = InsurancesRequest.builder().insurance(insurances).build();

    @Before
    public void init() {
        request = RequestAmortization.builder()
                .data(RequestAmortizationData.builder()
                        .customer(Customer.builder()
                                .identification(Identification.builder()
                                        .type("TIPDOC_FS001")
                                        .number("12345678")
                                        .build())
                                .companyIdType("2")
                                .companyIdNumber("45678945").build())
                        .offer(Offer.builder()
                                .productId("14")
                                .amount(new BigDecimal("1000000"))
                                .term(48)
                                .interestRateType("F")
                                .insurances(insurancesRequest).build())
                        .amortizationSchedule(true)
                        .build())
                .build();

        ResponseCustomerCommercial customerCommercialResponse = ResponseCustomerCommercial.builder()
                .data(ResponseCustomerCommercialData.builder()
                        .segment("SEGMEN_05")
                        .subSegment("SUBSEG_09")
                        .build())
                .build();

        List<Contact> contactList = new ArrayList<>();
        contactList.add(Contact.builder().mobilPhone("3152627615").email("mdvera@bancolombia.com.co").build());

        ResponseCustomerContact customerContactResponse = ResponseCustomerContact.builder()
                .data(ResponseCustomerContactData.builder()
                        .contact(contactList)
                        .build())
                .build();

        ResponseCustomerDetails customerDetailsResponse = ResponseCustomerDetails.builder()
                .data(ResponseCustomerDetailsData.builder()
                        .birthDate(LocalDate.of(1991, 4, 19))
                        .occupation("OCCUP_1")
                        .build())
                .build();

        List<RangeType> rangeTypeList = new ArrayList<>();
        rangeTypeList.add(RangeType.builder()
                .minimumAmount(1000000.00)
                .maximumAmount(20000000.00)
                .minimumTerm(48)
                .maximumTerm(48)
                .monthlyRate(1.799917)
                .arrearsRate(0.21599)
                .effectiveAnnualRate(0.238708)
                .annualNominalMonthlyRate(0.21599)
                .rateType("F")
                .fixedTermDepositRate(0.0)
                .build());

        rangeTypeList.add(RangeType.builder()
                .minimumAmount(20000001.00)
                .maximumAmount(50000000.00)
                .minimumTerm(48)
                .maximumTerm(48)
                .monthlyRate(1.799917)
                .arrearsRate(0.21599)
                .effectiveAnnualRate(0.238708)
                .annualNominalMonthlyRate(0.21599)
                .rateType("F")
                .fixedTermDepositRate(0.0)
                .build());

        rangeTypeList.add(RangeType.builder()
                .minimumAmount(50000001.00)
                .maximumAmount(100000000.00)
                .minimumTerm(48)
                .maximumTerm(48)
                .monthlyRate(1.799917)
                .arrearsRate(0.21599)
                .effectiveAnnualRate(0.238708)
                .annualNominalMonthlyRate(0.21599)
                .rateType("F")
                .fixedTermDepositRate(0.0)
                .build());

        rangeTypeList.add(RangeType.builder()
                .minimumAmount(100000001.00)
                .maximumAmount(200000000.00)
                .minimumTerm(48)
                .maximumTerm(48)
                .monthlyRate(1.799917)
                .arrearsRate(0.21599)
                .effectiveAnnualRate(0.238708)
                .annualNominalMonthlyRate(0.21599)
                .rateType("F")
                .fixedTermDepositRate(0.0)
                .build());

        rangeTypeList.add(RangeType.builder()
                .minimumAmount(200000001.00)
                .maximumAmount(500000000.00)
                .minimumTerm(48)
                .maximumTerm(48)
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

        ResponseInterestRate interestRateResponse = ResponseInterestRate.builder()
                .data(ResponseInterestRateData.builder()
                        .rateRange(rateRangeList)
                        .build())
                .build();

        List<FeeConcept> feeConcepts = new ArrayList<>();
        feeConcepts.add(FeeConcept.builder()
                .type("totalCuota")
                .amount(new BigDecimal("52580"))
                .build());

        List<RegularFeeConcept> regularFeeConcepts = new ArrayList<>();
        regularFeeConcepts.add(RegularFeeConcept.builder()
                .type("01")
                .amount(new BigDecimal("33067"))
                .build());
        regularFeeConcepts.add(RegularFeeConcept.builder()
                .type("05")
                .amount(new BigDecimal("19513"))
                .build());
        regularFeeConcepts.add(RegularFeeConcept.builder()
                .type("09")
                .amount(new BigDecimal("19513"))
                .build());


        ResponseInstallments installmentsResponse = ResponseInstallments.builder()
                .data(ResponseInstallmentsData.builder()
                        .feeConcepts(feeConcepts)
                        .regularFeeConcepts(regularFeeConcepts)
                        .build())
                .build();

        RuleResponse mapResponse = RuleResponse.builder().data(ResponseData.builder().valid(true).build()).build();

        doReturn(Mono.just(customerContactResponse)).when(customerContactGateway).retrieveCustomerContact(any(), any());
        doReturn(Mono.just(customerDetailsResponse)).when(customerDetailsGateway).retrieveCustomerDetails(any(), any());
        doReturn(Mono.just(customerCommercialResponse)).when(customerCommercialGateway).retrieveCustomerCommercial(any(), any());
        doReturn(Mono.just(interestRateResponse)).when(interestRateGateway).retrieveInterestRate(any(), any());
        doReturn(Mono.just(installmentsResponse)).when(installmentsGateway).retrieveInstallments(any(), any());
        doReturn(Mono.just(mapResponse)).when(mapGateway).ruleValidate(any());
    }

    @Test
    public void shouldRetrieveAmortizationUseCaseBasic() {
        RuleResponse mapResponse = RuleResponse.builder().data(ResponseData.builder().valid(false).build()).build();
        doReturn(Mono.just(mapResponse)).when(mapGateway).ruleValidate(any());

        Mono<ResponseAmortization> responseMono = useCase.retrieveAmortization(request, msgId);

        StepVerifier.create(responseMono)
                .expectSubscription()
                .thenConsumeWhile(x -> true)
                .verifyComplete();

        verify(customerContactGateway, atLeastOnce()).retrieveCustomerContact(any(), any());
        verify(customerCommercialGateway, atLeastOnce()).retrieveCustomerCommercial(any(), any());
        verify(customerDetailsGateway, atLeastOnce()).retrieveCustomerDetails(any(), any());
        verify(interestRateGateway, atLeastOnce()).retrieveInterestRate(any(), any());
        verify(installmentsGateway, atLeastOnce()).retrieveInstallments(any(), any());
        verify(mapGateway, atLeastOnce()).ruleValidate(any());
    }

    @Test
    public void shouldRetrieveAmortizationUseCaseUnemployment() {
        List<InsuranceRequest> insurances = new ArrayList<>();
        insurances.add(InsuranceRequest.builder().type("SV").build());
        insurances.add(InsuranceRequest.builder().type("SD").build());
        InsurancesRequest insurancesRequest = InsurancesRequest.builder().insurance(insurances).build();
        request = RequestAmortization.builder()
                .data(RequestAmortizationData.builder()
                        .customer(Customer.builder()
                                .identification(Identification.builder()
                                        .type("TIPDOC_FS001")
                                        .number("12345678")
                                        .build())
                                .companyIdType("2")
                                .companyIdNumber("45678945").build())
                        .offer(Offer.builder()
                                .productId("14")
                                .amount(new BigDecimal("1000000"))
                                .term(48)
                                .interestRateType("F")
                                .insurances(insurancesRequest).build())
                        .amortizationSchedule(true)
                        .build())
                .build();

        Mono<ResponseAmortization> responseMono = useCase.retrieveAmortization(request, msgId);

        StepVerifier.create(responseMono)
                .expectSubscription()
                .thenConsumeWhile(x -> true)
                .verifyComplete();
    }

    @ParameterizedTest
    @ValueSource(strings = {"1000000", "20000001", "50000001", "100000001", "200000001"})
    public void shouldRetrieveAmortizationUseCaseAmount2(String amount) {
        request = RequestAmortization.builder()
                .data(RequestAmortizationData.builder()
                        .customer(Customer.builder().identification(Identification.builder().type("TIPDOC_FS001").number("12345678").build()).build())
                        .offer(Offer.builder().productId("14").interestRateType("F").amount(new BigDecimal(amount)).term(48).build())
                        .build())
                .build();

        Mono<ResponseAmortization> responseMono = useCase.retrieveAmortization(request, msgId);

        StepVerifier.create(responseMono)
                .expectSubscription()
                .thenConsumeWhile(x -> true)
                .verifyComplete();
    }

    @Test
    public void shouldRetrieveAmortizationUseCaseBirthDateNull() {
        ResponseCustomerDetails customerDetailsResponse = ResponseCustomerDetails.builder()
                .data(ResponseCustomerDetailsData.builder()
                        .birthDate(null)
                        .build()).build();

        doReturn(Mono.just(customerDetailsResponse)).when(customerDetailsGateway).retrieveCustomerDetails(any(), any());

        Mono<ResponseAmortization> responseMono = useCase.retrieveAmortization(request, msgId);

        StepVerifier.create(responseMono)
                .expectSubscription()
                .thenConsumeWhile(x -> true)
                .verifyComplete();
    }

    @Test
    public void shouldRetrieveAmortizationException() {

        request = RequestAmortization.builder()
                .data(RequestAmortizationData.builder()
                        .customer(Customer.builder().identification(Identification.builder().type("TIPDOC_FS001").number("12345678").build()).build())
                        .offer(Offer.builder().productId("14").amount(new BigDecimal("10000")).term(48).interestRateType("F").build())
                        .amortizationSchedule(true)
                        .build())
                .build();

        Mono<ResponseAmortization> responseMono = useCase.retrieveAmortization(request, msgId);

        StepVerifier.create(responseMono)
                .expectSubscription()
                .thenConsumeWhile(x -> true)
                .expectError();
    }
}