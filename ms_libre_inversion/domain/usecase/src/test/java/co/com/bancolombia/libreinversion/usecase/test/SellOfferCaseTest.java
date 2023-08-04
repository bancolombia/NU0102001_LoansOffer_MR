package co.com.bancolombia.libreinversion.usecase.test;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.commons.ErrorEnum;
import co.com.bancolombia.libreinversion.model.constants.SellConst;
import co.com.bancolombia.libreinversion.model.document.InfoDocument;
import co.com.bancolombia.libreinversion.model.events.gateways.EventsGateway;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.libreinversion.model.loansdisbursements.gateway.DisbursementsGateWay;
import co.com.bancolombia.libreinversion.model.offer.selltestutil.UtilTestDeceval;
import co.com.bancolombia.libreinversion.model.offer.selltestutil.UtilTestSellRQ;
import co.com.bancolombia.libreinversion.model.offer.selltestutil.UtilTestSellRS;
import co.com.bancolombia.libreinversion.model.product.ResponseData;
import co.com.bancolombia.libreinversion.model.product.RuleResponse;
import co.com.bancolombia.libreinversion.model.product.gateways.MAPGateways;
import co.com.bancolombia.libreinversion.model.rate.LoanInteresRateResponse;
import co.com.bancolombia.libreinversion.model.request.SellOfferRQ;
import co.com.bancolombia.libreinversion.model.request.gateways.AmazonS3Gateways;
import co.com.bancolombia.libreinversion.model.request.gateways.DocManagementGateway;
import co.com.bancolombia.libreinversion.model.request.gateways.HtmlPdfGateways;
import co.com.bancolombia.libreinversion.model.request.gateways.RedisGateways;
import co.com.bancolombia.libreinversion.model.request.gateways.TemplateHtmlGateways;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.gateway.GestionarPagareElectV1Gateway;
import co.com.bancolombia.libreinversion.model.stoc.GeneralInformation;
import co.com.bancolombia.libreinversion.usecase.acceptsell.AcceptSellUseCase;
import co.com.bancolombia.libreinversion.usecase.acceptsell.DocSellOfferUseCase;
import co.com.bancolombia.libreinversion.usecase.contactability.ContactabilityUseCase;
import co.com.bancolombia.libreinversion.usecase.disbursements.DisbursementsUseCase;
import co.com.bancolombia.libreinversion.usecase.soap.GestionarPagareElectUseCase;
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

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SellOfferCaseTest {

    private final Long TIME = 10L;
    private final String STATUS_200 = "200 Ok";
    @Mock
    private GestionarPagareElectV1Gateway gestionarPagareElectronico;
    @Mock
    private MAPGateways mapGateways;
    @Mock
    private RedisGateways redisGateways;
    @Mock
    private EventsGateway eventsGateway;
    @Mock
    private AmazonS3Gateways amazonS3Gateways;
    @Mock
    private DisbursementsGateWay disbursementsGateWay;
    @Mock
    private DocManagementGateway docManagementGateway;
    @Mock
    private TemplateHtmlGateways templateHtmlGateways;
    @Mock
    private HtmlPdfGateways htmlPdfGateways;
    @InjectMocks
    private GestionarPagareElectUseCase gestPagareElectUseCase;
    @InjectMocks
    private AcceptSellUseCase acceptSellUseCase;
    @InjectMocks
    private OpportunitiesUseCase opportunitiesUseCase;
    @InjectMocks
    private ContactabilityUseCase contactabilityUseCase;
    @InjectMocks
    private DocSellOfferUseCase docSellOfferUseCase;
    @InjectMocks
    private DisbursementsUseCase disbursementsUseCase;

    @Before
    public void init() {
        initPagareElectV1Gateway();
        when(redisGateways.getCompleteDataFromCache(any())).thenReturn(Mono.just(UtilTestSellRQ.buildConfirmOfferComplete()));
        when(disbursementsGateWay
                .requestDisbursement(any(), any())).thenReturn(Mono.just(UtilTestSellRS.buildDisbursementRS()));
        when(mapGateways.ruleValidate(any())).thenReturn(Mono.just(UtilTestSellRS.buildRuleResponse()));
        when(redisGateways.getData(any())).thenReturn(Mono.just(UtilTestSellRS.buildRuleResponse()));
        when(redisGateways.setData(anyString(), any(), anyLong()))
                .thenReturn(Mono.just(UtilTestSellRQ.buildSellOfferRQ()));
        Mockito.doReturn(Mono.just(UtilTestSellRQ.buildInfoDocument().getByteArray()))
                .when(amazonS3Gateways).getObjectAsByteArray(anyString(), anyString());
        Mockito.doReturn(Mono.just(Boolean.TRUE))
                .when(amazonS3Gateways).putObject(anyString(), any(), anyString());
        Mockito.doReturn(Mono.just(tiemOffer())).when(mapGateways).getTimeOffer();
        Mockito.doReturn(Mono.just(UtilTestSellRQ.buildInfoDocument().getByteArray()))
                .when(docManagementGateway)
                .signDocument(any(), anyString(), anyString(), anyString());
        Mockito.doReturn(Mono.just(Base64.getEncoder().encodeToString(UtilTestSellRQ.buildInfoDocument().getByteArray())))
                .when(templateHtmlGateways).setDataTemplateWeelcome(anyString(), any());
        Mockito.doReturn(Mono.just(Boolean.TRUE))
                .when(eventsGateway).sendEmailAttached(any());
        Mockito.doReturn(Mono.just(Boolean.TRUE))
                .when(eventsGateway).sendEmailBasic(any());
        Mockito.doReturn(Mono.just(STATUS_200))
                .when(templateHtmlGateways).setDataEmploymentInsuraceLetter(anyString(), any(), anyString(), any());
        Mockito.doReturn(UtilTestSellRQ.buildInfoDocument().getByteArray())
                .when(htmlPdfGateways).basicHtmlToPdf(anyString(), anyString());
        Mockito.doReturn(STATUS_200)
                .when(amazonS3Gateways).generatePresignedURL(anyString(), anyString(), anyLong());
    }

    private void initPagareElectV1Gateway() {
        when(gestionarPagareElectronico
                .crearGiradorPN(any(), any())).thenReturn(Mono.just(UtilTestSellRS.buildCrearGiradorPNResponse()));
        when(gestionarPagareElectronico
                .crearPagare(any(), any())).thenReturn(Mono.just(UtilTestSellRS.buildCrearPagareResponse()));
        when(gestionarPagareElectronico.firmaPagare(any(), any())).thenReturn(Mono.just(STATUS_200));
    }

    @Test
    public void testUseCase() {
        gestPagareElectUseCaseTest();
        decevalTest();
        disbursementsUseCaseTest();
        acceptSellUseCaseTest();
        opportunitiesUseCaseTest();
        contactabilityUseCaseTest();
        docSellOfferTest();
        getInteresRate();
        testWellcomeTemplateEmail();
        getloanType();
    }


    private void acceptSellUseCaseTest() {
        SellOfferRQ selOfferRQ = UtilTestSellRQ.buildSellOfferRQ();
        StepVerifier.create(acceptSellUseCase
                .validAvailabilityApp(selOfferRQ)).expectNext(UtilTestSellRS.buildRuleResponse());
        StepVerifier.create(acceptSellUseCase.setCaheQAndOperation(selOfferRQ, TIME))
                .expectNext(selOfferRQ);
        StepVerifier.create(acceptSellUseCase.queryRuleParamPgc(selOfferRQ))
                .expectNext(selOfferRQ).verifyComplete();
        StepVerifier.create(acceptSellUseCase.queryRuleParamDocumentManage()).expectNext(UtilTestSellRS.buildRuleResponse());
        StepVerifier.create(acceptSellUseCase.getTimeOffer()).expectNextMatches(aLong -> aLong != null).verifyComplete();
        StepVerifier.create(acceptSellUseCase.updateCacheSell(UtilTestSellRQ.buildCacheSell(), selOfferRQ, TIME))
                .expectNext(true);
        StepVerifier.create(acceptSellUseCase.loadCacheSell(selOfferRQ))
                .expectNextMatches(cacheSell -> cacheSell != null).verifyComplete();
        acceptSellUseCase.clearDataCustomer(selOfferRQ.getCustomer().getIdentification().getNumber());

        StepVerifier.create(acceptSellUseCase.getTimeUrlPreSigned("123456"))
                .expectNextMatches(aLong -> aLong != null).verifyComplete();
        StepVerifier.create(acceptSellUseCase.validAmmounts(UtilTestSellRQ.buildConfirmOfferComplete(), selOfferRQ, "987654"))
                .expectNextMatches(aBoolean -> aBoolean != null).verifyComplete();
    }

    private void getInteresRate() {
        when(redisGateways.getData(any())).thenReturn(Mono.just(UtilTestSellRS.buildRuleResponse()));
        Mockito.doReturn(UtilTestSellRS.buildLoanInteresRateResponse())
                .when(redisGateways).castObject(any(), eq(LoanInteresRateResponse.class));

        StepVerifier.create(acceptSellUseCase
                        .getInteresRateCache(UtilTestSellRQ.buildConfirmOfferComplete(), UtilTestSellRQ.buildSellOfferRQ()))
                .expectNextMatches(rangeType -> rangeType != null).verifyComplete();
    }

    private void getloanType() {
        when(redisGateways.getData(any())).thenReturn(Mono.just(UtilTestSellRS.buildRuleResponse()));
        Mockito.doReturn(UtilTestSellRS.buildLoanInteresRateResponse())
                .when(redisGateways).castObject(any(), eq(LoanInteresRateResponse.class));

        StepVerifier.create(acceptSellUseCase.getloanType(UtilTestSellRQ.buildConfirmOfferComplete(),
                        UtilTestSellRQ.buildSellOfferRQ()))
                .expectNextMatches(s -> s != null).verifyComplete();
    }

    private void gestPagareElectUseCaseTest() {
        StepVerifier.create(gestPagareElectUseCase.queryRuleParamDeceval()).expectNext(UtilTestSellRS.buildRuleResponse());
        StepVerifier.create(gestPagareElectUseCase.getCompleteDataCache("123456789"))
                .expectNextMatches(confirmOfferComplete -> confirmOfferComplete != null).verifyComplete();
        StepVerifier.create(gestPagareElectUseCase.queryRuleParamSellOffer()).expectNext(UtilTestSellRS.buildRuleResponse());
    }

    private void decevalTest() {
        StepVerifier.create(gestPagareElectUseCase.crearGiradorPN(any(), any()))
                .expectNext(UtilTestSellRS.buildCrearGiradorPNResponse());
        StepVerifier.create(gestPagareElectUseCase.crearPagare(any(), any()))
                .expectNext(UtilTestSellRS.buildCrearPagareResponse());
        StepVerifier.create(gestPagareElectUseCase.firmaPagare(any(), any())).expectNext(STATUS_200);
    }

    private void docSellOfferTest() {
        String bucketName = "loans-offer-dev-s3-documents";
        StepVerifier.create(docSellOfferUseCase.getDocumentsEmployment(bucketName, UtilTestSellRQ.buildSellOfferRQ(),
                UtilTestSellRQ.buildConfirmOfferComplete(),
                UtilTestSellRS.buildRuleResponse(), UtilTestSellRS.buildDisbursementRS())
        ).expectNextMatches(infoDocumentMap -> infoDocumentMap != null).verifyComplete();
        StepVerifier.create(docSellOfferUseCase.getDocumentsLiNativo(bucketName, UtilTestSellRQ.buildSellOfferRQ()))
                .expectNextMatches(infoDocMap -> infoDocMap != null).verifyComplete();
        StepVerifier.create(docSellOfferUseCase.mergeDocEmployment(UtilTestSellRQ.buildFiles()))
                .expectNextMatches(infoDocument -> infoDocument != null).verifyComplete();
        StepVerifier.create(docSellOfferUseCase.mergeDocLiNativo(UtilTestSellRQ.buildFiles(), "test.pdf"))
                .expectNextMatches(infoDocument -> infoDocument != null).verifyComplete();
        StepVerifier.create(docSellOfferUseCase.loadDocumentsLiNativo(bucketName, UtilTestSellRQ.buildFiles()))
                .expectNext(true).verifyComplete();
        StepVerifier.create(docSellOfferUseCase.loadFileManageDocSign(UtilTestSellRQ.buildFiles(),
                        UtilTestDeceval.buildCrearGiradorPN(), UtilTestSellRS.buildRuleResponse(), "123456"))
                .expectNext(UtilTestSellRQ.buildFiles()).verifyComplete();
        StepVerifier.create(docSellOfferUseCase.loadFileManageDocSign(UtilTestSellRQ.buildFiles(),
                        UtilTestDeceval.buildCrearGiradorPN(), UtilTestSellRS.buildRuleResponse(), "123456"))
                .expectNext(UtilTestSellRQ.buildFiles());
        StepVerifier.create(docSellOfferUseCase.addMetaDataPropDocument(UtilTestSellRQ.buildFiles()))
                .expectNextMatches(infoDoctMap -> infoDoctMap != null).verifyComplete();
        assertEquals(InfoDocument.class, docSellOfferUseCase
                .buildInfoDoc(UtilTestSellRQ.buildSellOfferRQ(), "prueba.pdf", "preuba".getBytes()).getClass());
        StepVerifier.create(docSellOfferUseCase.getDocumentsLiNativo(bucketName, UtilTestSellRQ.buildSellOfferRQ()))
                .expectNext(UtilTestSellRQ.buildFiles());
        assertEquals(InfoDocument.class, docSellOfferUseCase
                .addPassDocument(UtilTestSellRQ.buildInfoDocument(), UtilTestSellRQ.buildSellOfferRQ()).getClass());
        assertEquals(String.class, docSellOfferUseCase
                .generatePresignedURLDocsLi(bucketName, "prueba", TIME).getClass());
        StepVerifier.create(docSellOfferUseCase.awsS3PutObject(UtilTestSellRQ.buildInfoDocument(), bucketName))
                .expectNextMatches(aBoolean -> aBoolean).verifyComplete();
        StepVerifier.create(docSellOfferUseCase.getDocumentsS3(bucketName, "preuba"))
                .expectNextMatches(bytes -> bytes != null).verifyComplete();
    }

    private void testWellcomeTemplateEmail() {
        String bucketName = "loans-offer-dev-s3-documents";
        Mockito.doReturn(Mono.just(Base64.getEncoder().encodeToString(UtilTestSellRQ.buildInfoDocument().getByteArray())))
                .when(templateHtmlGateways).setDataTemplateWeelcome(anyString(), any());

        StepVerifier.create(docSellOfferUseCase.getWellcomeTemplateEmail(bucketName, UtilTestSellRQ.buildSellOfferRQ(),
                        UtilTestSellRQ.buildConfirmOfferComplete()))
                .expectNextMatches(infoDocument -> infoDocument != null).verifyComplete();
    }


    private void opportunitiesUseCaseTest() {
        GeneralInformation generalInformation = UtilTestSellRQ.buildConfirmOfferComplete().getGeneralInformation();
        String msgId = "123456789987654321";
        StepVerifier.create(opportunitiesUseCase
                .changeOpportunitiesToFinished(generalInformation, msgId)).expectNext(STATUS_200);
    }

    private void disbursementsUseCaseTest() {
        StepVerifier.create(disbursementsUseCase
                .execcuteDisbursement(any(), any())).expectNext(UtilTestSellRS.buildDisbursementRS());
    }

    private void contactabilityUseCaseTest() {
        String msgId = "123456789987654321";
        StepVerifier.create(contactabilityUseCase
                        .sendBasicMail(msgId, 2, UtilTestSellRQ.buildBodyMailBasicApiRQ()))
                .expectNext(STATUS_200).verifyComplete();
        StepVerifier.create(contactabilityUseCase
                        .sendAttachmentMail(msgId, 2, UtilTestSellRQ.buildBodyMailAttachedApiRQ()))
                .expectNext(STATUS_200).verifyComplete();
    }

    private RuleResponse tiemOffer() {
        Map<String, Object> map = new HashMap<>();
        return RuleResponse.builder()
                .data(ResponseData.builder().valid(true).utilLoad(map).build())
                .build();
    }

    @Test
    public void errorTest() {
        LibreInversionException err = new LibreInversionException(
                ErrorEnum.MSG_LI020.getMessage(), Constant.ERROR_LI001,
                ErrorEnum.MSG_LI020.getMessage(), SellConst.SELL_OFFER, SellConst.SELL_OFFER, "");

        Mockito.doReturn(Mono.error(err))
                .when(amazonS3Gateways).getObjectAsByteArray(anyString(), anyString());
        Mockito.doReturn(Mono.error(err))
                .when(amazonS3Gateways).putObject(anyString(), any(), anyString());

        String bucketName = "";
        StepVerifier.create(docSellOfferUseCase.getDocumentsS3(bucketName, "preuba"))
                .expectError(LibreInversionException.class).verify();
        StepVerifier.create(docSellOfferUseCase.awsS3PutObject(UtilTestSellRQ.buildInfoDocument(), bucketName))
                .expectError(LibreInversionException.class).verify();
    }
}
