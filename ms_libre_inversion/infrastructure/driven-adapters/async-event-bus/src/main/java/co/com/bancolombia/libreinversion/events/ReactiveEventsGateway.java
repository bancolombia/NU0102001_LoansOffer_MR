package co.com.bancolombia.libreinversion.events;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.commons.ErrorEnum;
import co.com.bancolombia.libreinversion.model.constants.SellConst;
import co.com.bancolombia.libreinversion.model.events.QueueMessageEmail;
import co.com.bancolombia.libreinversion.model.events.QueueMsgEmailAttached;
import co.com.bancolombia.libreinversion.model.events.gateways.EventsGateway;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
@RequiredArgsConstructor
public class ReactiveEventsGateway implements EventsGateway {

    private final SqsAsyncClient sqsAsyncClient;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final TechLogger log = LoggerFactory.getLog(ReactiveEventsGateway.class.getName());

    private String urlAwsSqs;

    @Override
    public Mono<Boolean> sendEmailBasic(QueueMessageEmail message) {
        final String method = "sendEmailBasic";
        try {
            return Mono.fromFuture(sqsAsyncClient.sendMessage(SendMessageRequest.builder()
                    .queueUrl(urlAwsSqs)
                    .messageBody(objectMapper.writeValueAsString(message))
                    .build())).onErrorResume(e -> Mono.error(buildError(method, e))).map(m -> {
                log.debug(m.toString());
                return true;
            });
        } catch (Exception e) {
            return Mono.error(buildError(method, e));
        }
    }

    @Override
    public Mono<Boolean> sendEmailAttached(QueueMsgEmailAttached message) {
        final String method = "sendEmailAttached";
        try {
            return Mono.fromFuture(sqsAsyncClient.sendMessage(SendMessageRequest.builder()
                            .queueUrl(urlAwsSqs)
                            .messageBody(objectMapper.writeValueAsString(message))
                            .build()))
                    .onErrorResume(e -> Mono.error(buildError(method, e))).map(m -> {
                        log.debug(m.toString());
                        return true;
                    });
        } catch (Exception e) {
            return Mono.error(buildError(method, e));
        }
    }

    private LibreInversionException buildError(String method, Throwable e) {
        return new LibreInversionException(
                ErrorEnum.MSG_LI022.getMessage(), ErrorEnum.MSG_LI022.getMessage(),
                Constant.CONTACTABILITY_CLIENT_ERROR, SellConst.SELL_OFFER, method, "");
    }
}
