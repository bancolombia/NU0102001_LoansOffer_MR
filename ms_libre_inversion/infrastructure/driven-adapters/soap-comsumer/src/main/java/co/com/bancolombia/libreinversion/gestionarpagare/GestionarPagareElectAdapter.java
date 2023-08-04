package co.com.bancolombia.libreinversion.gestionarpagare;

import co.com.bancolombia.libreinversion.call.CallSoap;
import co.com.bancolombia.libreinversion.config.WebClientConfig;
import co.com.bancolombia.libreinversion.encoding.Jaxb2SoapEncoder;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.header.RequestHeader;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.gateway.GestionarPagareElectV1Gateway;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.creategiradorpn.CrearGiradorPN;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.createpay.CreatePay;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.signaturepay.FirmarPagare;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.response.creategiradorpn.CrearGiradorPNResponse;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.response.createpay.CrearPagareResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.net.ssl.SSLException;


@Component
public class GestionarPagareElectAdapter implements GestionarPagareElectV1Gateway {

    private final WebClient webClient;
    @Value("${adapter-routes.urlGestionPagareElect}")
    private String soapUrlPagElectronico;

    public GestionarPagareElectAdapter() throws SSLException {
        webClient = WebClientConfig.webClient();
    }


    @Override
    public Mono<CrearGiradorPNResponse> crearGiradorPN(CrearGiradorPN requestCrearGiradorPN, RequestHeader header) {
        CallSoap<CrearGiradorPN, CrearGiradorPNResponse> callSoap = new CallSoap<>();
        callSoap.setObjRequest(requestCrearGiradorPN);
        callSoap.setObjResponse(CrearGiradorPNResponse.builder().build());
        String headString = Jaxb2SoapEncoder.convetObjectXmlString(header);

        return callSoap.call(webClient, soapUrlPagElectronico, headString)
                .flatMap(obj -> Mono.just((CrearGiradorPNResponse) obj));
    }

    @Override
    public Mono<CrearPagareResponse> crearPagare(CreatePay requestPay, RequestHeader header) {
        CallSoap<CreatePay, CrearPagareResponse> callSoap = new CallSoap<>();
        callSoap.setObjRequest(requestPay);
        callSoap.setObjResponse(CrearPagareResponse.builder().build());
        String headString = Jaxb2SoapEncoder.convetObjectXmlString(header);

        return callSoap.call(webClient, soapUrlPagElectronico, headString)
                .flatMap(obj -> Mono.just((CrearPagareResponse) obj));
    }

    @Override
    public Mono<String> firmaPagare(FirmarPagare firmaPagare, RequestHeader header) {
        CallSoap<FirmarPagare, String> callSoap = new CallSoap<>();
        callSoap.setObjRequest(firmaPagare);
        callSoap.setObjResponse("200");
        String headString = Jaxb2SoapEncoder.convetObjectXmlString(header);

        return callSoap.call(webClient, soapUrlPagElectronico, headString)
                .flatMap(obj -> Mono.just((String) obj));
    }

}

