package co.com.bancolombia.libreinversion.model.offer.sellutil;

import co.com.bancolombia.libreinversion.model.customer.rest.Contact;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerDetail;
import co.com.bancolombia.libreinversion.model.document.InfoDocument;
import co.com.bancolombia.libreinversion.model.enums.FuncPagareEnum;
import co.com.bancolombia.libreinversion.model.notification.rest.ResponseRetrieveInfo;
import co.com.bancolombia.libreinversion.model.product.RuleResponse;
import co.com.bancolombia.libreinversion.model.request.ConfirmOfferComplete;
import co.com.bancolombia.libreinversion.model.request.SellOfferRQ;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.ArchivoAdjunto;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.DatosAutenticacion;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.Identification;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.header.Destination;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.header.RequestHeader;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.header.UserId;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.creategiradorpn.CrearGiradorPN;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.creategiradorpn.DatosPersona;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.creategiradorpn.DireccionDomicilio;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.creategiradorpn.LugarDomicilio;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.createpay.CreatePay;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.createpay.DataPay;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.createpay.IdentificacionOtorgante;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.createpay.LugarDesembolso;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.signaturepay.FirmarPagare;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.signaturepay.IdentificacionFirmante;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.response.createpay.CrearPagareResponse;

import java.util.Optional;

public class BuildDeceval extends SellBuildBase {

    private BuildDeceval() {
        throw new IllegalStateException("Utility class");
    }

    public static Optional<CrearGiradorPN> buildCrearGiradorPN(ConfirmOfferComplete complete,
                                                               SellOfferRQ sellOfferRQ, RuleResponse ruleResponse) {
        try {
            utilMAP = ruleResponse.getData().getUtilLoad();
            DatosAutenticacion auth = DatosAutenticacion.builder()
                    .usuario(UtilSell.validObjectRuleNull(utilMAP.get(sellConst.MAP_DECEVAL_USUARIO)))
                    .codigoDepositante(UtilSell.validObjectRuleNull(
                            utilMAP.get(sellConst.MAP_DECEVAL_COD_DEPOSITANTE))
                    ).build();

            return Optional.of(CrearGiradorPN.builder().datosAutenticacion(auth)
                    .datosPersona(buildDatosPersona(complete, sellOfferRQ)).build());
        } catch (Exception e) {
            utilMAP = null;
            log.error(e.getMessage());
        }
        return Optional.empty();
    }

    public static Optional<CreatePay> buildCreatePay(CrearGiradorPN crearGirador, InfoDocument infoDoc) {
        try {
            LugarDomicilio domicilio = crearGirador.getDatosPersona().getLugarDomicilio();
            CreatePay res = CreatePay.builder()
                    .datosAutenticacion(DatosAutenticacion.builder()
                            .usuario(crearGirador.getDatosAutenticacion().getUsuario())
                            .codigoDepositante(crearGirador.getDatosAutenticacion().getCodigoDepositante())
                            .build())
                    .datosPagare(DataPay.builder()
                            .nitEmisor(sellConst.NIT_ISSUER)
                            .idClaseDefinicionDocumento(sellConst.CLASS_DEFINITION_DOC)
                            .tipoPagare(tipoPagareEnum.EN_BLANCO.getKey())
                            .creditoReembolsableEn(tipoMonedaEnum.PESOS.getKey())
                            .empresaOtorgante(sellConst.COMPANY_GRANTING)
                            .lugarDesembolso(buildLugarDesembolso(domicilio))
                            .identificacionOtorgante(buildIdentificacionOtorgante(crearGirador))
                            .archivoAdjunto(ArchivoAdjunto.builder().nombreArchivo(infoDoc.getNameFile())
                                    .contenidoArchivo(encodeBase64(infoDoc.getByteArray())).build())
                            .build()).build();
            return Optional.of(res);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Optional.empty();
    }

    public static Optional<FirmarPagare> bulidFirmarPagare(CrearPagareResponse crearPagareResp,
                                                           CrearGiradorPN girador, CreatePay pagare,
                                                           SellOfferRQ sellOfferRQ) {
        try {
            utilLoan = sellOfferRQ.getLoans().stream().findFirst();
            FirmarPagare res = FirmarPagare.builder()
                    .datosAutenticacion(DatosAutenticacion.builder()
                            .usuario(girador.getDatosAutenticacion().getUsuario())
                            .codigoDepositante(girador.getDatosAutenticacion().getCodigoDepositante())
                            .build())
                    .idPagareDeceval(crearPagareResp.getDatosPagareRespuesta().getIdPagareDeceval())
                    .identificacionFirmante(IdentificacionFirmante.builder()
                            .tipoDocumento(girador.getDatosPersona().getIdentificacion().getTipoDocumento())
                            .numeroDocumento(girador.getDatosPersona().getIdentificacion()
                                    .getNumeroDocumento()).build()
                    )
                    .clave(utilLoan.isPresent() ? utilLoan.get().getSigningCode() : null)
                    .idRolFirmante(sellConst.ID_ROL_SIGNER)
                    .archivosAdjunto(arrays.asList(ArchivoAdjunto.builder()
                            .nombreArchivo(pagare.getDatosPagare().getArchivoAdjunto()
                                    .getNombreArchivo())
                            .contenidoArchivo(pagare.getDatosPagare().getArchivoAdjunto()
                                    .getContenidoArchivo()).build()))
                    .build();
            return Optional.of(res);
        } catch (Exception e) {
            utilLoan = null;
            log.error("bulidFirmarPagare: " + e);
        }
        return Optional.empty();
    }

    public static Optional<RequestHeader> buildRequestHeader(FuncPagareEnum funcPagare, RuleResponse ruleResponse,
                                                             String messageId) {
        try {
            if (ruleResponse != null) {
                utilMAP = ruleResponse.getData().getUtilLoad();

                RequestHeader res = RequestHeader.builder()
                        .systemId(UtilSell.validObjectRuleNull(utilMAP.get(sellConst.MAP_DECEVAL_SYSTEM_ID)))
                        .messageId(messageId)
                        .timestamp(strDate("yyyy-MM-dd HH:mm:ss"))
                        .userId(UserId.builder()
                                .userName(UtilSell.validObjectRuleNull(utilMAP.get(sellConst.MAP_DECEVAL_USER_NAME)))
                                .build())
                        .destination(Destination.builder()
                                .name(UtilSell.validObjectRuleNull(utilMAP.get(sellConst.MAP_DECEVAL_DES_NAME)))
                                .namespace(UtilSell.validObjectRuleNull(utilMAP.get(sellConst.MAP_DES_NAME_SPACE)))
                                .operation(funcPagare.getValue())
                                .build()
                        ).build();
                return Optional.of(res);
            }
        } catch (Exception e) {
            utilMAP = null;
            log.error(e.getMessage());
        }
        return Optional.empty();
    }

    public static DatosPersona buildDatosPersona(ConfirmOfferComplete complete, SellOfferRQ sellOfferRQ) {
        ResponseRetrieveInfo info = complete.getResponseRetrieveInfo();
        ResponseCustomerDetail detail = complete.getResponseCustomerDetail();
        Contact contact = complete.getResponseCustomerContact().getData().getContact().stream()
                .findFirst().orElse(Contact.builder().build());

        return DatosPersona.builder()
                .correoElectronico(customerFactory.getEmail(complete.getResponseCustomerContact(), info))
                .direccionDomicilio(DireccionDomicilio.builder()
                        .direccion(contact.getAddress() == null ? null : contact.getAddress())
                        .telefono(contact.getPhoneNumber() == null ? "" : contact.getPhoneNumber())
                        .fax("").build())
                .nombreCompleto(customerFactory.getNames(complete.getResponseCustomerDetail()))
                .primerApellido(customerFactory.getSurnames(detail))
                .identificacionEmisor(sellConst.NIT_ISSUER)
                .identificacion(Identification.builder()
                        .tipoDocumento(customerFactory
                                .getDocumentType(sellOfferRQ.getCustomer().getIdentification().getType()))
                        .numeroDocumento(sellOfferRQ.getCustomer().getIdentification().getNumber()).build())
                .numeroCelular(customerFactory.getMobileNumber(
                        complete.getResponseCustomerContact(), info))
                .lugarDomicilio(buildLugarDomicilio(contact))
                .fechaNacimiento(customerFactory.getBirthDate(detail))
                .build();
    }

    public static LugarDomicilio buildLugarDomicilio(Contact contact) {
        return LugarDomicilio.builder()
                .pais(customerFactory.getCountryCode(contact.getCountryCode()))
                .ciudad(customerFactory.getCityParentCode(contact.getCityCode()))
                .departamento(customerFactory.getDepartamentCode(
                        contact.getDepartmentCode())).build();
    }

    public static LugarDesembolso buildLugarDesembolso(LugarDomicilio domicilio) {
        return LugarDesembolso.builder()
                .ciudad(domicilio == null ? null : domicilio.getCiudad())
                .pais(domicilio == null ? null : domicilio.getPais())
                .departamento(domicilio == null ? null : domicilio.getDepartamento())
                .build();
    }

    public static IdentificacionOtorgante buildIdentificacionOtorgante(CrearGiradorPN crearGirador) {
        return IdentificacionOtorgante.builder()
                .tipoDocumento(crearGirador.getDatosPersona().getIdentificacion()
                        .getTipoDocumento())
                .numeroDocumento(crearGirador.getDatosPersona().getIdentificacion()
                        .getNumeroDocumento())
                .build();
    }
}
