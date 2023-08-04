package co.com.bancolombia.utilities.api.handler;

import co.com.bancolombia.utilities.api.router.RouterRetrieve;
import co.com.bancolombia.utilities.model.AmortizationSchedule;
import co.com.bancolombia.utilities.model.Customer;
import co.com.bancolombia.utilities.model.Fng;
import co.com.bancolombia.utilities.model.Identification;
import co.com.bancolombia.utilities.model.InstallmentData;
import co.com.bancolombia.utilities.model.InstallmentsData;
import co.com.bancolombia.utilities.model.InsuranceRequest;
import co.com.bancolombia.utilities.model.InsuranceResponse;
import co.com.bancolombia.utilities.model.InsurancesRequest;
import co.com.bancolombia.utilities.model.InsurancesResponse;
import co.com.bancolombia.utilities.model.Offer;
import co.com.bancolombia.utilities.model.Payment;
import co.com.bancolombia.utilities.model.RequestAmortization;
import co.com.bancolombia.utilities.model.RequestAmortizationData;
import co.com.bancolombia.utilities.model.ResponseAmortization;
import co.com.bancolombia.utilities.model.ResponseAmortizationData;
import co.com.bancolombia.utilities.usecase.AmortizationUseCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
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
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RouterRetrieve.class, HandlerRetrieve.class})
@WebFluxTest
@TestPropertySource(properties = {
        "routes.basePath=/api/v1",
        "routes.retrieve=/retrieve"
})
public class HandlerRetrieveTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private WebTestClient client;

    @MockBean
    private AmortizationUseCase useCase;

    private RequestAmortization request;

    @Value("${routes.basePath}")
    private String basePath;

    @Value("${routes.retrieve}")
    private String routeRetrieve;

    @BeforeEach
    public void setUp() {
        client = WebTestClient.bindToApplicationContext(context).build();
    }

    @Before
    public void init() {
        List<InsuranceRequest> insurancesRequestList = new ArrayList<>();
        insurancesRequestList.add(InsuranceRequest.builder().type("SV").build());

        InsurancesRequest insurancesRequest = InsurancesRequest.builder().insurance(insurancesRequestList).build();

        request = RequestAmortization.builder()
                .data(RequestAmortizationData.builder()
                        .customer(Customer.builder()
                                .identification(Identification.builder()
                                        .type("TIPDOC_FS001")
                                        .number("12345678")
                                        .build())
                                .customerReliability("G1").build())
                        .offer(Offer.builder()
                                .productId("14")
                                .interestRateType("F")
                                .amount(new BigDecimal("15000000"))
                                .term(24)
                                .insurances(insurancesRequest).build())
                        .amortizationSchedule(true).build())
                .build();

        List<InsuranceResponse> insurancesResponseList = new ArrayList<>();
        insurancesResponseList.add(InsuranceResponse.builder().type("TYPE_02").amount(new BigDecimal("22500")).build());

        InsurancesResponse insurancesResponse = InsurancesResponse.builder().insurance(insurancesResponseList).build();

        List<Payment> payments = new ArrayList<>();
        payments.add(Payment.builder()
                .installmentNumber(1)
                .installment(new BigDecimal("5"))
                .interestPayment(new BigDecimal("0.05"))
                .capitalPayment(new BigDecimal("5000"))
                .annualFngCommission(new BigDecimal("2000"))
                .insurances(insurancesResponse).build());

        List<InstallmentData> installmentData = new ArrayList<>();
        installmentData.add(InstallmentData.builder()
                .installment(new BigDecimal("1"))
                .paymentDay(1)
                .interestRate(new BigDecimal("1"))
                .monthOverdueInterestRate(new BigDecimal("0.01"))
                .arreasInterestRate(new BigDecimal("5"))
                .effectiveAnnualInterestRate(new BigDecimal("1.2"))
                .nominalAnnualInterestRate(new BigDecimal("0.05"))
                .interestRateType("TYPE_01")
                .variableInterestRateAdditionalPoints(new BigDecimal("3"))
                .expirationDate(LocalDate.of(2021, 12, 31))
                .insurances(insurancesResponse)
                .availabilityHandlingFee(new BigDecimal("20"))
                .fng(Fng.builder()
                        .commission(new BigDecimal("25"))
                        .annualCommission(new BigDecimal("30"))
                        .vat(new BigDecimal("0.0"))
                        .coverage(new BigDecimal("50")).build())
                .amortizationSchedule(AmortizationSchedule.builder()
                        .payment(payments)
                        .build())
                .build());

        ResponseAmortization response = ResponseAmortization.builder()
                .data(ResponseAmortizationData.builder()
                        .installmentsData(InstallmentsData.builder().installmentData(installmentData).build())
                        .build())
                .build();

        when(useCase.retrieveAmortization(any(), any())).thenReturn(Mono.just(response));
    }

    @Test
    public void shouldRetrieveAmortization() {
        client.post().uri(basePath.concat(routeRetrieve))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

}