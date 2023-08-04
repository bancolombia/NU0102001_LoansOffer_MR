package co.com.bancolombia.libreinversion.gestionarpagare.test;

import co.com.bancolombia.libreinversion.encoding.Jaxb2SoapEncoder;
import co.com.bancolombia.libreinversion.gestionarpagare.GestionarPagareElectAdapter;
import co.com.bancolombia.libreinversion.model.offer.selltestutil.UtilTestDeceval;
import co.com.bancolombia.libreinversion.model.offer.selltestutil.UtilTestSellRQ;
import co.com.bancolombia.libreinversion.model.offer.selltestutil.UtilTestSellRS;
import co.com.bancolombia.libreinversion.model.enums.FuncPagareEnum;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.header.RequestHeader;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.creategiradorpn.CrearGiradorPN;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.createpay.CreatePay;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.signaturepay.FirmarPagare;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.response.creategiradorpn.CrearGiradorPNResponse;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.response.createpay.CrearPagareResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.test.StepVerifier;

import java.io.IOException;

@RunWith(SpringRunner.class)
@WebFluxTest
@ContextConfiguration(classes = {GestionarPagareElectAdapter.class})
public class GestionarPagareElectAdapterTest {

    private String url;
    private CrearGiradorPN crearGiradorPN;
    private CreatePay createPay;
    private FirmarPagare firmarPagare;
    private String resFirmarPagare;
    private CrearPagareResponse resCreatePay;
    private CrearGiradorPNResponse resCreateGirador;
    private MockWebServer mockWebServer;
    private GestionarPagareElectAdapter adapter;


    @Before
    public void init() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        adapter = new GestionarPagareElectAdapter();
        url = String.format("http://localhost:%s", mockWebServer.getPort());
        ReflectionTestUtils.setField(adapter, "soapUrlPagElectronico", url, String.class);
    }

    @AfterAll
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }


    @Test
    public void testCrearGiradorPN() {
        crearGiradorPN = UtilTestDeceval.buildCrearGiradorPN();
        resCreateGirador = UtilTestSellRS.buildCrearGiradorPNResponse();
        String bodyRsp = Jaxb2SoapEncoder.convetObjectXmlString(resCreateGirador);

        mockWebServer.enqueue(new MockResponse()
                .setBody(UtilTestSellRS.buildXMLDecevalRespAddBody(bodyRsp))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_XML));

        StepVerifier.create(adapter.crearGiradorPN(crearGiradorPN, RequestHeader.from(FuncPagareEnum.CREAR_GIRADOR)))
                .expectNext(resCreateGirador)
                .verifyComplete();
    }

    @Test
    public void testCrearPagare() {
        createPay = UtilTestDeceval.buildCreatePay();
        resCreatePay = UtilTestSellRS.buildCrearPagareResponse();
        String bodyRsp = Jaxb2SoapEncoder.convetObjectXmlString(resCreatePay);

        mockWebServer.enqueue(new MockResponse()
                .setBody(UtilTestSellRS.buildXMLDecevalRespAddBody(bodyRsp))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_XML));

        StepVerifier.create(adapter.crearPagare(createPay, RequestHeader.from(FuncPagareEnum.CREAR_PAGARE)))
                .expectNext(resCreatePay)
                .verifyComplete();
    }

    @Test
    public void testFirmaPagare() {
        firmarPagare = UtilTestDeceval.buildFirmarPagare();
        resFirmarPagare = "200 ok";

        mockWebServer.enqueue(new MockResponse()
                .setBody("200 ok")
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_XML));

        StepVerifier.create(adapter.firmaPagare(firmarPagare, RequestHeader.from(FuncPagareEnum.FIRMAR_PAGARE)))
                .expectNext(resFirmarPagare)
                .verifyComplete();
    }
}