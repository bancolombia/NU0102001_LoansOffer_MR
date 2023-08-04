package co.com.bancolombia.notification.usecase.sms;

import co.com.bancolombia.notification.model.sms.SmsRQ;
import co.com.bancolombia.notification.model.sms.gateways.SmsGateways;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SmsUseCase {

    private final SmsGateways smsGateways;


    public Mono<Boolean> send(SmsRQ rq, String messageId, String priority) {
      return smsGateways.send(rq, messageId, priority)
              .thenReturn(Boolean.TRUE);
    }
}
