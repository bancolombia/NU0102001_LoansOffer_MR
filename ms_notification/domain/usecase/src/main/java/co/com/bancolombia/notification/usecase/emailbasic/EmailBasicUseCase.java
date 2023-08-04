package co.com.bancolombia.notification.usecase.emailbasic;

import co.com.bancolombia.notification.model.email.EmailBasicRQ;
import co.com.bancolombia.notification.model.email.gateways.EmailBasicGateways;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class EmailBasicUseCase {

    private final EmailBasicGateways emailBasicGateways;


    public Mono<Boolean> send(EmailBasicRQ request, String priority, String messageId) {
        return emailBasicGateways
                .send(request, priority, messageId)
                .thenReturn(Boolean.TRUE);
    }
}
