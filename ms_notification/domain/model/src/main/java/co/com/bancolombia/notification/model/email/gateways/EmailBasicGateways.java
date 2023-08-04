package co.com.bancolombia.notification.model.email.gateways;

import co.com.bancolombia.notification.model.email.EmailBasicRQ;
import co.com.bancolombia.notification.model.email.EmailBasicRS;
import reactor.core.publisher.Mono;

public interface EmailBasicGateways {

    Mono<EmailBasicRS> send(EmailBasicRQ requestBody, String priority, String msgId);
}
