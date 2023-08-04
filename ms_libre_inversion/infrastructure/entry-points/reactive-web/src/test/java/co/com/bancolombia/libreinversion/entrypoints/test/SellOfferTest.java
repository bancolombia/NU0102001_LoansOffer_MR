package co.com.bancolombia.libreinversion.entrypoints.test;

import co.com.bancolombia.libreinversion.api.handler.SellHandler;
import co.com.bancolombia.libreinversion.api.router.SellRouter;
import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.document.InfoDocument;
import co.com.bancolombia.libreinversion.model.offer.SellOfferData;
import co.com.bancolombia.libreinversion.model.offer.selltestutil.UtilTestDeceval;
import co.com.bancolombia.libreinversion.model.offer.selltestutil.UtilTestSellRQ;
import co.com.bancolombia.libreinversion.model.offer.selltestutil.UtilTestSellRS;
import co.com.bancolombia.libreinversion.model.request.ConfirmOfferComplete;
import co.com.bancolombia.libreinversion.model.request.SellOfferRQ;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.creategiradorpn.CrearGiradorPN;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.createpay.CreatePay;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.signaturepay.FirmarPagare;
import co.com.bancolombia.libreinversion.usecase.acceptsell.AcceptSellUseCase;
import co.com.bancolombia.libreinversion.usecase.acceptsell.DocSellOfferUseCase;
import co.com.bancolombia.libreinversion.usecase.contactability.ContactabilityUseCase;
import co.com.bancolombia.libreinversion.usecase.disbursements.DisbursementsUseCase;
import co.com.bancolombia.libreinversion.usecase.soap.GestionarPagareElectUseCase;
import co.com.bancolombia.libreinversion.usecase.stoc.OpportunitiesUseCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SellRouter.class, SellHandler.class})
@TestPropertySource(properties = {"spring.webflux.base-path=/api/v1", "entry-point.sell=/sell"})
@WebFluxTest
public class SellOfferTest {

    private static final Long TIME = 10L;
    private SellOfferRQ sellOfferRQ;
    private SellOfferData sellOfferData;
    @Mock
    private CrearGiradorPN crearGiradorPN;
    @Mock
    private CreatePay createPay;
    @Mock
    private FirmarPagare firmarPagare;
    @Mock
    private ConfirmOfferComplete confirmOfferCompMk;
    @Value("${entry-point.sell}")
    private String routeSell;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private GestionarPagareElectUseCase gestPagareElectUseCase;
    @MockBean
    private AcceptSellUseCase acceptSellUseCase;
    @MockBean
    private OpportunitiesUseCase opportunitiesUseCase;
    @MockBean
    private ContactabilityUseCase contactabilityUseCase;
    @MockBean
    private DocSellOfferUseCase docSellOfferUseCase;
    @MockBean
    private DisbursementsUseCase disbursementsUseCase;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
    }

    @Before
    public void init() {
        sellOfferRQ = UtilTestSellRQ.buildSellOfferRQ();
        sellOfferData = UtilTestSellRS.buildSellOfferData();
        decevalTest();
        docSellOfferTest();
        acceptSellUseCaseTest();
        gestPagareElectUseCaseTest();
        crearGiradorPN = UtilTestDeceval.buildCrearGiradorPN();
        createPay = UtilTestDeceval.buildCreatePay();
        firmarPagare = UtilTestDeceval.buildFirmarPagare();
        Mockito.when(disbursementsUseCase.execcuteDisbursement(Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(UtilTestSellRS.buildDisbursementRS()));
        Mockito.when(contactabilityUseCase.sendAttachmentMail(Mockito.anyString(), Mockito.anyInt(), Mockito.any()))
                .thenReturn(Mono.just("200 ok"));
        Mockito.when(opportunitiesUseCase.changeOpportunitiesToFinished(Mockito.any(), Mockito.anyString()))
                .thenReturn(Mono.just("200 ok"));
    }

    @Test
    public void sellHandlerTest() {
        webTestClient.post()
                .uri(routeSell)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Mono.just(sellOfferRQ), SellOfferRQ.class))
                .exchange().expectStatus()
                .isOk()
                .expectBody(SellOfferData.class)
                .consumeWith((response) -> {
                    Assert.assertNotNull(response.getResponseBody());
                });
    }

    private void acceptSellUseCaseTest() {
        Mockito.when(acceptSellUseCase.validAvailabilityApp(Mockito.any())).thenReturn(Mono.just(UtilTestSellRS.buildRuleResponse()));
        Mockito.when(acceptSellUseCase.setCaheQAndOperation(Mockito.any(), Mockito.anyLong())).thenReturn(Mono.just(sellOfferRQ));
        Mockito.when(acceptSellUseCase.queryRuleParamPgc(Mockito.any())).thenReturn(Mono.just(sellOfferRQ));
        Mockito.when(acceptSellUseCase.getInteresRateCache(Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(UtilTestSellRQ.buildRangeType()));
        Mockito.when(acceptSellUseCase.queryRuleParamDocumentManage()).thenReturn(Mono.just(UtilTestSellRS.buildRuleResponse()));
        Mockito.when(acceptSellUseCase.getTimeOffer()).thenReturn(Mono.just(10L));
        Mockito.when(acceptSellUseCase.loadCacheSell(UtilTestSellRQ.buildSellOfferRQ()))
                .thenReturn(Mono.just(UtilTestSellRQ.buildCacheSell()));
        Mockito.when(acceptSellUseCase.updateCacheSell(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(true));
        Mockito.when(acceptSellUseCase.getTimeUrlPreSigned(Mockito.anyString()))
                .thenReturn(Mono.just(TIME));
        Mockito.when(acceptSellUseCase.validAmmounts(Mockito.any(), Mockito.any(), Mockito.anyString()))
                .thenReturn(Mono.just(Boolean.TRUE));
        Mockito.when(acceptSellUseCase.getloanType(Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just("P59"));

    }

    private void gestPagareElectUseCaseTest() {
        Mockito.when(gestPagareElectUseCase.queryRuleParamDeceval()).thenReturn(Mono.just(UtilTestSellRS.buildRuleResponse()));
        Mockito.when(gestPagareElectUseCase.getCompleteDataCache(Mockito.any()))
                .thenReturn(Mono.just(UtilTestSellRQ.buildConfirmOfferComplete()));
        Mockito.when(gestPagareElectUseCase.queryRuleParamSellOffer()).thenReturn(Mono.just(UtilTestSellRS.buildRuleResponse()));
    }

    private void decevalTest() {
        Mockito.when(gestPagareElectUseCase.crearGiradorPN(Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(UtilTestSellRS.buildCrearGiradorPNResponse()));
        Mockito.when(gestPagareElectUseCase.crearPagare(Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(UtilTestSellRS.buildCrearPagareResponse()));
        Mockito.when(gestPagareElectUseCase.firmaPagare(Mockito.any(), Mockito.any())).thenReturn(Mono.just("200 Ok"));
    }

    private void docSellOfferTest() {
        Map<String, InfoDocument> files = new HashMap<>();
        files.put(Constant.OPERATION_ANNEX_DOCUMENT, UtilTestSellRQ.buildInfoDocument());
        files.put(Constant.INSTRUCTION_LETTER_DOCUMENT, UtilTestSellRQ.buildInfoDocument());
        files.put(Constant.PAY_LI_NATIVE_DOCUMENT, UtilTestSellRQ.buildInfoDocument());
        Mockito.when(docSellOfferUseCase.addMetaDataPropDocument(Mockito.any())).thenReturn(Mono.just(files));
        Mockito.when(docSellOfferUseCase.addPassDocument(Mockito.any(), Mockito.any())).thenReturn(UtilTestSellRQ.buildInfoDocument());
        Mockito.when(docSellOfferUseCase.getDocumentsEmployment(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(files));
        Mockito.when(docSellOfferUseCase.getDocumentsLiNativo(Mockito.any(), Mockito.any())).thenReturn(Mono.just(files));
        Mockito.when(docSellOfferUseCase.mergeDocEmployment(Mockito.any())).thenReturn(Mono.just(UtilTestSellRQ.buildInfoDocument()));
        Mockito.when(docSellOfferUseCase.mergeDocLiNativo(Mockito.any(), Mockito.any())).thenReturn(Mono.just(UtilTestSellRQ.buildInfoDocument()));
        Mockito.when(docSellOfferUseCase.getWellcomeTemplateEmail(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(UtilTestSellRQ.buildInfoDocument()));
        Mockito.when(docSellOfferUseCase.loadFileManageDocSign(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(files));
        Mockito.when(docSellOfferUseCase.awsS3PutObject(Mockito.any(), Mockito.anyString()))
                .thenReturn(Mono.just(true));
        Mockito.when(docSellOfferUseCase.generatePresignedURLDocsLi(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong()))
                .thenReturn("bdafds");
        Mockito.when(docSellOfferUseCase.loadDocumentsLiNativo(Mockito.anyString(), Mockito.any()))
                .thenReturn(Mono.just(true));
    }


}
