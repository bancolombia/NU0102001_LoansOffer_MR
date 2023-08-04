package co.com.bancolombia.libreinversion.model.offer.selltestutil;

import co.com.bancolombia.libreinversion.model.constants.SellConst;
import co.com.bancolombia.libreinversion.model.customer.rest.Contact;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerContactData;
import co.com.bancolombia.libreinversion.model.document.Document;
import co.com.bancolombia.libreinversion.model.enums.TipoPagareEnum;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.response.DataRespDM;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.response.DisbursementRS;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.response.MetaRespDM;
import co.com.bancolombia.libreinversion.model.offer.LoanSell;
import co.com.bancolombia.libreinversion.model.offer.SellOfferData;
import co.com.bancolombia.libreinversion.model.product.ResponseData;
import co.com.bancolombia.libreinversion.model.product.RuleResponse;
import co.com.bancolombia.libreinversion.model.rate.LoanInteresRateResponse;
import co.com.bancolombia.libreinversion.model.rate.LoanRate;
import co.com.bancolombia.libreinversion.model.rate.RateRange;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.response.creategiradorpn.CrearGiradorPNResponse;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.response.creategiradorpn.DatosPersonaRespuesta;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.response.creategiradorpn.IdentificacionPersona;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.response.createpay.CrearPagareResponse;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.response.createpay.DatosPagareRespuesta;

import java.util.Arrays;
import java.util.HashMap;

public class UtilTestSellRS extends UtilTestBase {

    private static final String AMOUNT = "1000000";
    private static final String ID_TEST = "1234567";

    private UtilTestSellRS() {
        throw new IllegalStateException("Utility class");
    }

    public static RuleResponse buildRuleResponse() {
        utilLoad = new HashMap<>();
        utilLoad.put(SellConst.MAP_DECEVAL_USUARIO, "preuba");
        utilLoad.put(SellConst.MAP_DECEVAL_COD_DEPOSITANTE, "0");
        utilLoad.put(SellConst.MAP_CARDIF_COD_EMPLOYMENT, "886");
        utilLoad.put(SellConst.MAP_DOC_MANAGER_HEAD_USER_NAME, "preuba");
        utilLoad.put(SellConst.MAP_DOC_MANAGER_HEAD_USER_TOKEN, "preuba123");
        utilLoad.put(constant.TIME_EXP_DOC_MAP_RULE, "20");
        utilLoad.put(SellConst.NAME_DOCUMENT_SELL, "test");
        return RuleResponse.builder().code("14").message("pruebaRule").success(1)
                .data(ResponseData.builder().valid(true)
                        .utilLoad(utilLoad).build()).build();
    }

    public static ResponseCustomerContactData buildRespCustomerContData() {
        return ResponseCustomerContactData.builder()
                .contact(Arrays.asList(Contact.builder()
                        .address("CL 40 39 A 17")
                        .phoneNumber("7592788")
                        .cityCode("CIUDAD_05001004")
                        .countryCode("PAIS_CO")
                        .departmentCode("DPTO_CO_000005").build()))
                .build();
    }

    public static CrearPagareResponse buildCrearPagareResponse() {
        return CrearPagareResponse.builder().datosPagareRespuesta(
                DatosPagareRespuesta.builder()
                        .idClaseDefinicionDocumento(Integer.valueOf(ID_TEST))
                        .tipoPagare(TipoPagareEnum.DILIGENCIADO.getKey())
                        .numPagareEntidad("987654321")
                        .mensajeRespuesta("Mensaje de respuesta de prueba")
                        .idPagareDeceval(Long.valueOf(ID_TEST))
                        .build()
        ).build();
    }

    public static CrearGiradorPNResponse buildCrearGiradorPNResponse() {
        return CrearGiradorPNResponse.builder()
                .datosPersonaRespuesta(DatosPersonaRespuesta.builder()
                        .idCuenta(Integer.valueOf(ID_TEST))
                        .identificacion(IdentificacionPersona.builder()
                                .numeroDocumento("2101067981")
                                .tipoDocumento("TIPDOC_FS001").build())
                        .nombreCompleto("nombre prueba")
                        .primerApellido("apellido1")
                        .segundoApellido("apellido2")
                        .mensajeRespuesta("Mensaje respuesta para test")
                        .build())
                .build();
    }

    public static DisbursementRS buildDisbursementRS() {
        final int dayPayment = 2;
        final int installments = 130;
        return DisbursementRS.builder()
                .meta(MetaRespDM.builder()
                        ._applicationId("c4e6bd04-5149-11e7-b114-b2f933d5fe66")
                        ._requestDateTime("20-20-2021")
                        ._applicationId("acxff62e-6f12-42de-9012-3e7304418abd")
                        .build())
                .data(DataRespDM.builder()
                        .title("loanDisbursement")
                        .loanId("12354654")
                        .loanAmount(Integer.valueOf(AMOUNT))
                        .disbursementAmount(Integer.valueOf(AMOUNT))
                        .installments(installments)
                        .interestRateType("V")
                        .loanOriginDate("2021-12-12")
                        .loanDueDate("2020-12-12")
                        .nextPaymentDate("2020-12-12")
                        .paymentDay(dayPayment).build())
                .build();
    }

    public static LoanInteresRateResponse buildLoanInteresRateResponse() {
        return LoanInteresRateResponse.builder()
                .data(LoanRate.builder()
                        .rateRange(Arrays.asList(RateRange.builder()
                                .loanType("P59")
                                .rangeType(Arrays.asList(UtilTestSellRQ.buildRangeType()))
                                .build()))
                        .build())
                .build();
    }

    public static SellOfferData buildSellOfferData() {
        return SellOfferData.builder()
                .loans(LoanSell.builder()
                        .loanId("12345679")
                        .receipt("12345678901234567890").build())
                .documents(Arrays.asList(Document.builder()
                        .format("pdf")
                        .name("prueba.pdf")
                        .url("pruebas").build()))
                .build();
    }

    public static String buildXMLDecevalRespAddBody(String body) {
        return "";
    }
}
