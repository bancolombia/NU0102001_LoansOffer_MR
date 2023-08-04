package co.com.bancolombia.utilities.usecase;

import co.com.bancolombia.utilities.model.Customer;
import co.com.bancolombia.utilities.model.Identification;
import co.com.bancolombia.utilities.model.InsuranceRequest;
import co.com.bancolombia.utilities.model.InsurancesRequest;
import co.com.bancolombia.utilities.model.Offer;
import co.com.bancolombia.utilities.model.RequestAmortization;
import co.com.bancolombia.utilities.model.RequestAmortizationData;
import co.com.bancolombia.utilities.model.ResponseAmortization;
import co.com.bancolombia.utilities.model.customer.ResponseCustomerCommercial;
import co.com.bancolombia.utilities.model.customer.ResponseCustomerCommercialData;
import co.com.bancolombia.utilities.model.customer.ResponseCustomerDetails;
import co.com.bancolombia.utilities.model.customer.ResponseCustomerDetailsData;
import co.com.bancolombia.utilities.model.error.ErrorLog;
import co.com.bancolombia.utilities.model.exceptions.AmortizationBusinessException;
import co.com.bancolombia.utilities.model.exceptions.AmortizationInternalException;
import co.com.bancolombia.utilities.model.gateways.CustomerCommercialGateway;
import co.com.bancolombia.utilities.model.gateways.CustomerContactGateway;
import co.com.bancolombia.utilities.model.gateways.CustomerDetailsGateway;
import org.junit.Before;
import org.junit.Test;
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
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class ExceptionTest {

    @InjectMocks
    private AmortizationUseCase useCase;

    @Mock
    private CustomerCommercialGateway customerCommercialGateway;

    @Mock
    private CustomerContactGateway customerContactGateway;

    @Mock
    private CustomerDetailsGateway customerDetailsGateway;

    private RequestAmortization request;

    private final ErrorLog errorLog = ErrorLog.builder()
            .actionName("")
            .detailError("")
            .errorCode("")
            .messageId("")
            .serviceName("")
            .build();

    @Before
    public void init() {
        List<InsuranceRequest> insurances = new ArrayList<>();
        insurances.add(InsuranceRequest.builder().type("TYPE_02").build());

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
                                .productId("1")
                                .amount(new BigDecimal("1000000"))
                                .term(12)
                                .interestRateType("TYPE_01")
                                .gracePeriod(6)
                                .interestPaymentFrequency(1)
                                .capitalPaymentFrequency(1)
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

        ResponseCustomerDetails customerDetailsResponse = ResponseCustomerDetails.builder()
                .data(ResponseCustomerDetailsData.builder()
                        .birthDate(LocalDate.of(1991, 4, 19))
                        .occupation("OCCUP_1")
                        .build())
                .build();

        doReturn(Mono.just(customerDetailsResponse)).when(customerDetailsGateway).retrieveCustomerDetails(any(), any());
        doReturn(Mono.just(customerCommercialResponse)).when(customerCommercialGateway).retrieveCustomerCommercial(any(), any());
    }

    @Test
    public void shouldGetAmortizationBusinessException() {
        doReturn(Mono.error(() -> new AmortizationBusinessException(errorLog))).when(customerContactGateway).retrieveCustomerContact(any(), any());
        Mono<ResponseAmortization> responseMono = useCase.retrieveAmortization(request, "");

        StepVerifier.create(responseMono)
                .expectSubscription()
                .thenConsumeWhile(x -> true)
                .verifyError();
    }

    @Test
    public void shouldGetAmortizationInternalException() {
        doReturn(Mono.error(() -> new AmortizationInternalException(errorLog))).when(customerContactGateway).retrieveCustomerContact(any(), any());
        Mono<ResponseAmortization> responseMono = useCase.retrieveAmortization(request, "");

        StepVerifier.create(responseMono)
                .expectSubscription()
                .thenConsumeWhile(x -> true)
                .verifyError();
    }

    @Test
    public void shouldGetAmortizationBusinessException2() {
        doReturn(Mono.error(() -> new AmortizationBusinessException("", "", "", "", "", ""))).when(customerContactGateway).retrieveCustomerContact(any(), any());
        Mono<ResponseAmortization> responseMono = useCase.retrieveAmortization(request, "");

        StepVerifier.create(responseMono)
                .expectSubscription()
                .thenConsumeWhile(x -> true)
                .verifyError();
    }

    @Test
    public void shouldGetAmortizationInternalException2() {
        doReturn(Mono.error(() -> new AmortizationInternalException("", "", "", "", "", ""))).when(customerContactGateway).retrieveCustomerContact(any(), any());
        Mono<ResponseAmortization> responseMono = useCase.retrieveAmortization(request, "");

        StepVerifier.create(responseMono)
                .expectSubscription()
                .thenConsumeWhile(x -> true)
                .verifyError();
    }
}
