package co.com.bancolombia.libreinversion.model.events.gateways;

import co.com.bancolombia.libreinversion.model.events.QueueMessageEmail;
import co.com.bancolombia.libreinversion.model.events.QueueMsgEmailAttached;
import reactor.core.publisher.Mono;

public interface EventsGateway {
    Mono<Boolean> sendEmailBasic(QueueMessageEmail message);

    Mono<Boolean> sendEmailAttached(QueueMsgEmailAttached message);
}
