package co.com.bancolombia.utilities.model.gateways;

import co.com.bancolombia.utilities.model.installments.RequestInstallments;
import co.com.bancolombia.utilities.model.installments.ResponseInstallments;
import reactor.core.publisher.Mono;

public interface InstallmentsGateway {

    Mono<ResponseInstallments> retrieveInstallments(RequestInstallments requestBody, String msgId);

}
