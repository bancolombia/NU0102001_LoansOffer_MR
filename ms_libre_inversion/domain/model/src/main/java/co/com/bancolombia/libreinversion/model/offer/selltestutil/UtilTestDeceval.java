package co.com.bancolombia.libreinversion.model.offer.selltestutil;

import co.com.bancolombia.libreinversion.model.constants.SellConst;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.ArchivoAdjunto;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.DatosAutenticacion;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.Identification;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.creategiradorpn.CrearGiradorPN;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.creategiradorpn.DatosPersona;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.creategiradorpn.DireccionAlterna;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.creategiradorpn.DireccionDomicilio;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.creategiradorpn.DireccionOficina;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.creategiradorpn.LugarDomicilio;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.creategiradorpn.LugarExpedicionDocumento;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.createpay.CreatePay;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.createpay.DataPay;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.createpay.IdentificacionOtorgante;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.signaturepay.FirmarPagare;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.signaturepay.IdentificacionFirmante;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class UtilTestDeceval extends UtilTestBase {

    private UtilTestDeceval() {
        super();
        throw new IllegalStateException("Utility class");
    }

    public static CreatePay buildCreatePay() {
        final int numUnidadesUVR = 110;
        final int idClaseDefinicionDocument = 134;
        Date fecha = null;
        try {
            fecha = new SimpleDateFormat("yyyy-MM-dd").parse("2001-01-01");
        } catch (ParseException e) {
            log.error(e.getMessage());
        }
        return CreatePay.builder().datosAutenticacion(buildDatosAutenticacion()).datosPagare(
                DataPay.builder().nitEmisor(SellConst.NIT_ISSUER)
                        .idClaseDefinicionDocumento(idClaseDefinicionDocument)
                        .tipoPagare(VAL_TWO_TEST).numPagareEntidad("271657")
                        .numCredito(VAL_TWO_TEST.toString()).numReferencia("64687937")
                        .fechaDesembolso(fecha)
                        .identificacionOtorgante(IdentificacionOtorgante.builder()
                                .tipoDocumento(TYPE_DOC).numeroDocumento("1234567898").build())
                        .idCuentaOtorgante(Integer.valueOf(ID_TEST))
                        .creditoReembolsableEn(VAL_TWO_TEST.toString())
                        .empresaOtorgante(SellConst.COMPANY_GRANTING)
                        .numUnidadesUVR(numUnidadesUVR)
                        .baseLiquidacionTasa(VAL_ONE_TEST.toString())
                        .tasaInteres(VAL_ONE_TEST.toString())
                        .modalidad(VAL_ONE_TEST.toString())
                        .archivoAdjunto(ArchivoAdjunto.builder().nombreArchivo("nombreArchivo.jpg")
                                .contenidoArchivo("cid1268029690429").build()).build()).build();
    }

    public static FirmarPagare buildFirmarPagare() {
        return FirmarPagare.builder()
                .datosAutenticacion(buildDatosAutenticacion())
                .idPagareDeceval(Long.valueOf(ID_TEST))
                .identificacionFirmante(IdentificacionFirmante.builder().tipoDocumento(TYPE_DOC)
                        .numeroDocumento("1234567898").build())
                .motivo("FirmaValida")
                .idRolFirmante(VAL_FIVE_TEST)
                .archivosAdjunto(Arrays.asList(ArchivoAdjunto.builder()
                        .nombreArchivo("nombreArchivo12.jpg").contenidoArchivo("JKHjkhdf").build()))
                .build();
    }

    public static DatosAutenticacion buildDatosAutenticacion() {
        return DatosAutenticacion.builder()
                .usuario("pruebasbancolom2").codigoDepositante("7").build();
    }

    public static CrearGiradorPN buildCrearGiradorPN() {
        DatosPersona person = DatosPersona.builder()
                .identificacionEmisor("8909039388")
                .identificacion(Identification.builder().tipoDocumento(TYPE_DOC)
                        .numeroDocumento("15510510").build())
                .lugarExpedicionDocumento(LugarExpedicionDocumento.builder()
                        .pais("CO").departamento("05").ciudad("5001000").build())
                .primerApellido("Perez")
                .segundoApellido("Lopez").nombreCompleto("Bob")
                .fechaExpedicion("2002-01-02")
                .fechaNacimiento("2001-12-31").correoElectronico("bob@mail.com")
                .direccionDomicilio(DireccionDomicilio.builder()
                        .direccion("Calle 10").telefono("23456").fax("45678962").build())
                .direccionOficina(DireccionOficina.builder()
                        .direccion("direccion").telefono("1445684").fax("45612345").build())
                .direccionAlterna(DireccionAlterna.builder()
                        .direccion("direccionalterna").telefono("45678945").fax("fax").build())
                .lugarDomicilio(LugarDomicilio.builder()
                        .pais("CO").departamento("05").ciudad("5001000").build())
                .paisNacionalidad("CO")
                .salario(Double.valueOf(AMOUNT)).tiempoServicio("tiempoServicio").pensionado(true)
                .numeroCelular("3122689587").estadoCivil("S").build();

        return CrearGiradorPN.builder().datosAutenticacion(buildDatosAutenticacion())
                .datosPersona(person).build();
    }
}
