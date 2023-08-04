package co.com.bancolombia.libreinversion.usecase.test;

import co.com.bancolombia.libreinversion.model.account.rest.Identification;
import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.customer.Customer;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.Beneficiary;
import co.com.bancolombia.libreinversion.model.offer.Offer;
import co.com.bancolombia.libreinversion.model.offer.selltestutil.UtilTestSellRQ;
import co.com.bancolombia.libreinversion.model.product.ResponseData;
import co.com.bancolombia.libreinversion.model.product.RuleResponse;
import co.com.bancolombia.libreinversion.model.product.gateways.MAPGateways;
import co.com.bancolombia.libreinversion.model.request.*;
import co.com.bancolombia.libreinversion.model.request.gateways.AmazonS3Gateways;
import co.com.bancolombia.libreinversion.model.request.gateways.HtmlPdfGateways;
import co.com.bancolombia.libreinversion.model.request.gateways.RedisGateways;
import co.com.bancolombia.libreinversion.model.request.gateways.TemplateHtmlGateways;
import co.com.bancolombia.libreinversion.usecase.request.ConfirmOfferCompleteUseCase;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ConfirmTestAmountException {

    @Mock
    private MAPGateways mapGateways;

    @Mock
    private HtmlPdfGateways htmlPdfGateways;

    @Mock
    private TemplateHtmlGateways templateHtmlGateways;

    @Mock
    private AmazonS3Gateways amazonS3Gateways;

    @Mock
    private RedisGateways redisGateways;

    @InjectMocks
    private ConfirmOfferCompleteUseCase confirmOfferCompleteUseCase;


    private ConfirmOfferRQ confirmOfferRQ;
    private RuleResponse ruleResponseDocumentos;

    private String messageId = "5";
    private String bucketName = "";
    private String html = "html";

    @Before
    public void init() {
        confirmOfferRQ = ConfirmOfferRQ
                .builder()
                .customer(getCustomer())
                .offer(getOffer())
                .insurances(getInsurance())
                .partial(true)
                .build();

        InputStream inputStreamInstruction = null;
        byte[] tempByte = null;

        tempByte = new byte[1024];
        inputStreamInstruction = IOUtils.toInputStream("some test data for my input stream", "UTF-8");

        Map<String, Object> mapDocument = new HashMap<>();

        ruleResponseDocumentos = RuleResponse.builder()
                .data(ResponseData.builder()
                        .utilLoad(mapDocument)
                        .valid(true).build())
                .build();


    }

    @Test
    public void test() {
        confirm();
    }

    private void confirm() {

        Mono<ConfirmOfferResponse> confirmOfferDataMono = confirmOfferCompleteUseCase
                .comfirmComplete(confirmOfferRQ, messageId, bucketName);

        StepVerifier.create(confirmOfferDataMono)
                .expectSubscription()
                .thenConsumeWhile(x -> true)
                .expectError()
                .verify();

    }

    private Customer getCustomer() {
        return Customer
                .builder()
                .identification(getIdentification())
                .build();
    }

    private Identification getIdentification() {
        return Identification.builder().build();
    }

    private Offer getOffer() {
        return Offer.builder()
                .disbursementDestination(DisbursementDestination.builder()
                        .destination(UtilTestSellRQ.getDestination()).build())
                .build();
    }

    private List<Insurances> getInsurance() {

        List<Insurances> insurances = new ArrayList<>();
        Insurances insurance = Insurances
                .builder()
                .build();
        insurances.add(insurance);

        return insurances;
    }

    public List<Destination> getDestination() {

        List<Destination> destinations = new ArrayList<>();

        Destination des1 = Destination.builder()
                .beneficiary(Beneficiary.builder()
                        .fullName(null)
                        .identification(Identification.builder()
                                .type(null).number(null).build()).build())
                .build();

        Destination des = Destination.builder()
                .destinationType(Constant.CUENTA_DE_AHORRO)
                .beneficiary(Beneficiary.builder()
                        .identification(getIdentification()).build())
                .build();

        destinations.add(des);
        destinations.add(des1);
        return destinations;
    }
}
