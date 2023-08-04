package co.com.bancolombia.notification.model.email.gateways;

import co.com.bancolombia.notification.model.email.attachment.EmailAttachedRQ;
import co.com.bancolombia.notification.model.email.attachment.EmailAttachedRS;
import reactor.core.publisher.Mono;

public interface EmailAttachedGateway {
    Mono<EmailAttachedRS> send(EmailAttachedRQ requestBody, String priority, String msgId);
}
