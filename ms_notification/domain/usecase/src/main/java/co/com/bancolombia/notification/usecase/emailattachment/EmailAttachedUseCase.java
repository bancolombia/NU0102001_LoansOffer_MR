package co.com.bancolombia.notification.usecase.emailattachment;

import co.com.bancolombia.notification.model.email.attachment.EmailAttachedRQ;
import co.com.bancolombia.notification.model.email.gateways.EmailAttachedGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class EmailAttachedUseCase {

    private final EmailAttachedGateway emailAttachGateway;

    public Mono<Boolean> send(EmailAttachedRQ request, String priority, String msgId) {
        return emailAttachGateway.send(request, priority, msgId)
                .thenReturn(Boolean.TRUE);
    }
}
