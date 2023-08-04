package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.gateway;

import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.header.RequestHeader;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.createpay.CreatePay;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.creategiradorpn.CrearGiradorPN;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.signaturepay.FirmarPagare;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.response.creategiradorpn.CrearGiradorPNResponse;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.response.createpay.CrearPagareResponse;
import reactor.core.publisher.Mono;

public interface GestionarPagareElectV1Gateway {

    Mono<CrearGiradorPNResponse> crearGiradorPN(CrearGiradorPN requestCrearGiradorPN, RequestHeader header);

    Mono<CrearPagareResponse> crearPagare(CreatePay requestPay, RequestHeader header);

    Mono<String> firmaPagare(FirmarPagare requestFirmaPagare, RequestHeader header);

}
