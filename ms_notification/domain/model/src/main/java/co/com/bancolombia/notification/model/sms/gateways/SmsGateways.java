package co.com.bancolombia.notification.model.sms.gateways;

import co.com.bancolombia.notification.model.sms.SmsRQ;
import co.com.bancolombia.notification.model.sms.SmsRS;
import reactor.core.publisher.Mono;

public interface SmsGateways {

    Mono<SmsRS> send(SmsRQ rq, String priority, String messageId);
}
