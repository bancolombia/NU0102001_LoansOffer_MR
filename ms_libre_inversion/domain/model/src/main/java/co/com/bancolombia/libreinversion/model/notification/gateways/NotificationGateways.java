package co.com.bancolombia.libreinversion.model.notification.gateways;

import co.com.bancolombia.libreinversion.model.notification.rest.ResponseRetrieveInfo;
import co.com.bancolombia.libreinversion.model.notification.rest.RetrieveInformationRQ;
import reactor.core.publisher.Mono;

public interface NotificationGateways {


    Mono<ResponseRetrieveInfo> callRetrieveInformation(RetrieveInformationRQ request,
                                                       String msgId, Long time);

}
