package co.com.bancolombia.libreinversion.model.request.gateways;

import co.com.bancolombia.libreinversion.model.docmanagement.SignDocumentsReq;
import reactor.core.publisher.Mono;

public interface DocManagementGateway {

    Mono<byte[]> signDocument(SignDocumentsReq signDocumentsReq, String messageId, String xUsername, String xUsertoken);
}
