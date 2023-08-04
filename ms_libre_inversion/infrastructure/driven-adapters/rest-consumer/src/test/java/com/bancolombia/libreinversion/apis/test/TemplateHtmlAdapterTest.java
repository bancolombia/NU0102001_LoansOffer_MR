package com.bancolombia.libreinversion.apis.test;


import co.com.bancolombia.libreinversion.model.account.rest.Identification;
import co.com.bancolombia.libreinversion.model.commons.AccountType;
import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.constants.SellConst;
import co.com.bancolombia.libreinversion.model.customer.Customer;
import co.com.bancolombia.libreinversion.model.customer.rest.MetaData;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerCommercial;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerCommercialData;
import co.com.bancolombia.libreinversion.model.offer.Offer;
import co.com.bancolombia.libreinversion.model.offer.selltestutil.UtilTestSellRQ;
import co.com.bancolombia.libreinversion.model.offer.selltestutil.UtilTestSellRS;
import co.com.bancolombia.libreinversion.model.product.ResponseData;
import co.com.bancolombia.libreinversion.model.product.RuleResponse;
import co.com.bancolombia.libreinversion.model.rate.RangeType;
import co.com.bancolombia.libreinversion.model.request.ConfirmOfferComplete;
import co.com.bancolombia.libreinversion.model.request.ConfirmOfferRQ;
import co.com.bancolombia.libreinversion.model.request.DepositAccount;
import co.com.bancolombia.libreinversion.model.request.DirectDebit;
import co.com.bancolombia.libreinversion.model.request.DisbursementDestination;
import co.com.bancolombia.libreinversion.model.request.Insurances;
import co.com.bancolombia.libreinversion.model.stoc.GeneralInformation;
import co.com.bancolombia.libreinversion.model.stoc.PreapprovedDetail;
import co.com.bancolombia.libreinversion.request.TemplateHtmlAdapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@WebFluxTest
@ContextConfiguration(classes = {TemplateHtmlAdapter.class})
public class TemplateHtmlAdapterTest {

    TemplateHtmlAdapter templateHtmlAdapter;
    ConfirmOfferComplete confirmOfferComplete;
    private ConfirmOfferRQ confirmOfferRQ;
    RuleResponse ruleResponse;

    @Before
    public void init() {

        confirmOfferRQ = ConfirmOfferRQ
                .builder()
                .legalTrace("12345")
                .customer(getCustomer())
                .directDebit(DirectDebit.builder()
                        .allowDirectDebit(SellConst.YES)
                        .depositAccount(DepositAccount.builder()
                                .type("CUENTA_DE_AHORRO")
                                .number("1234567890123456").build()).build())
                .offer(getOffer())
                .insurances(getInsurance())
                .build();

        Map<String, Object> mapDocument = new HashMap<>();

        mapDocument.put("PlanSD", "P59");
        mapDocument.put("LineaCreadito", "20");
        mapDocument.put("FrecuenciaPagoCapitalP59", "3");
        mapDocument.put("ClaseGarantiaP59", "2");
        mapDocument.put("PeriodoGraciaCapitalP59", "5");
        mapDocument.put("IndexacionP59", "5");
        mapDocument.put("TipoTasaP59", "5");
        mapDocument.put("PuntosAdicionalesDTFP59", "test");
        mapDocument.put("NMesesPeriodoGrciaP59", "test");
        mapDocument.put("ModalidadInteresP59", "test");
        mapDocument.put("FrecuenciaPagoInteresP59", "test");

        mapDocument.put(Constant.ACCOUTN_TYPE, getAccountType());


        ruleResponse = RuleResponse.builder()
                .data(ResponseData.builder()
                        .utilLoad(mapDocument)
                        .valid(true).build())
                .build();

        templateHtmlAdapter = new TemplateHtmlAdapter();

        confirmOfferComplete = ConfirmOfferComplete.builder()
                .responseCustomerCommercial(responseCustomerCommercial())
                .confirmOfferRQ(confirmOfferRQ)
                .rangeType(RangeType.builder()
                        .annualNominalMonthlyRate(new BigDecimal(.0365))
                        .effectiveAnnualRate(new BigDecimal(.0365))
                        .arrearsRate(new BigDecimal(.0365))
                        .build())
                .generalInformation(GeneralInformation.builder()
                        .preapprovedDetail(PreapprovedDetail.builder()
                                .expirationDate("2012/19/03")
                                .build())
                        .build())
                .ruleResponse(ruleResponse)
                .build();
    }

    private List<AccountType> getAccountType() {

        List<AccountType> accountTypes = new ArrayList<>();
        accountTypes.add(AccountType.builder().key("CUENTA_DE_AHORRO")
                .value("Ahorros").build());

        accountTypes.add(AccountType.builder().key("CUENTA_CORRIENTE")
                .value("Corriente").build());
     return accountTypes;
    }

    @Test
    public void test() throws Exception {

        String html = "test html";

        Mono<String> response = templateHtmlAdapter.setDataToPay(html, Mockito.any(), confirmOfferComplete);

        StepVerifier.create(response)
                .expectNextMatches(data -> data.length() > 0)
                .verifyComplete();

        Mono<String> responseLetter = templateHtmlAdapter.setDataInstrutionLetter(html, Mockito.any(), confirmOfferComplete);

        StepVerifier.create(responseLetter)
                .expectNextMatches(data -> data.length() > 0)
                .verifyComplete();

        Mono<String> responseAnexo = templateHtmlAdapter.setDataOperationAnnex(html, ruleResponse.getData().getUtilLoad(),
                confirmOfferComplete);

        StepVerifier.create(responseAnexo)
                .expectNextMatches(data -> data.length() > 0)
                .verifyComplete();

        Mono<String> insuraceLetter = templateHtmlAdapter.setDataEmploymentInsuraceLetter(html,
                confirmOfferComplete, "123456", UtilTestSellRS.buildDisbursementRS());

        StepVerifier.create(insuraceLetter)
                .expectNextMatches(data -> data.length() > 0)
                .verifyComplete();

        Mono<String> templateWeelcome = templateHtmlAdapter.setDataTemplateWeelcome(html,
                UtilTestSellRQ.buildConfirmOfferComplete());

        StepVerifier.create(templateWeelcome)
                .expectNextMatches(data -> data.length() > 0)
                .verifyComplete();
    }

    public static ResponseCustomerCommercial responseCustomerCommercial() {

        return ResponseCustomerCommercial.builder()
                .data(
                        ResponseCustomerCommercialData.builder()
                                .mdmKey("25609610")
                                .fullName("LUIS IGNACIO MEDINA")
                                .role("ROLNEG_02")
                                .customerStatus("ESTADO_02")
                                .segment("SEGMEN_03")
                                .subSegment("SUBSEG_12")
                                .build()
                )
                .meta(
                        MetaData.builder()
                                ._applicationId("cb440ddd-383c-4995-a8b1-4030fe343f9b")
                                ._messageId("eda2e01f-2353-4810-84c8-16472c3c4414")
                                ._requestDateTime("2021-09-20T11:46:27-05:00")
                                .build()
                )
                .build();
    }

    private Customer getCustomer() {
        return Customer
                .builder()
                .identification(getIdentification())
                .companyIdNumber("NIT")
                .companyIdType("584369874")
                .build();
    }

    private Identification getIdentification() {
        return Identification.builder().type("TIPDOC_FS001")
                .number("2101067991").build();
    }

    private Offer getOffer() {

        return Offer.builder()
                .id("123456777")
                .amount("560000")
                .term("123")
                .term("123")
                .paymentDay(20)
                .disbursementDestination(DisbursementDestination.builder()
                        .destination(UtilTestSellRQ.getDestination())
                        .build())
                .interestRateType("F")
                .build();
    }

    private List<Insurances> getInsurance() {

        List<Insurances> insurances = new ArrayList<>();
        Insurances insurance = Insurances
                .builder()
                .type("SD")
                .beneficiaries(UtilTestSellRQ.getBeneficiary())
                .build();
        insurances.add(insurance);

        return insurances;
    }

}
