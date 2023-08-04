package co.com.bancolombia.notification.events;

import co.com.bancolombia.notification.model.email.attachment.DataEmail;
import co.com.bancolombia.notification.model.email.attachment.EmailAttachedRQ;
import co.com.bancolombia.notification.model.email.EmailBasicData;
import co.com.bancolombia.notification.model.email.EmailBasicRQ;
import co.com.bancolombia.notification.model.queue.QueueMessage;
import co.com.bancolombia.notification.model.sms.SmsRQ;
import co.com.bancolombia.notification.usecase.emailattachment.EmailAttachedUseCase;
import co.com.bancolombia.notification.usecase.emailbasic.EmailBasicUseCase;
import co.com.bancolombia.notification.usecase.sms.SmsUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SqsAwsEntryPoint {

    private final SqsAsyncClient client;
    private final EmailBasicUseCase emailBasicUseCase;
    private final SmsUseCase smsUseCase;
    private final EmailAttachedUseCase emailAttachedUseCase;

    private static final Logger logger = LoggerFactory.getLogger(SqsAwsEntryPoint.class);

    public Mono<Boolean> sendMessage(QueueMessage message) {
        if (message.getType().equalsIgnoreCase("sms")) {
            return mapToSms(message.getData())
                    .flatMap(sms -> smsUseCase.send(sms, message.getPriority(), message.getMessageId()));

        } else if (message.getType().equalsIgnoreCase("email_basic")) {
            return mapToEmailBasic(message.getData())
                    .flatMap(email -> {
                        logger.info(String.format("emailBasicUseCase.send -> %s", email));
                        return emailBasicUseCase.send(email, message.getPriority(), message.getMessageId());
                    });
        } else if (message.getType().equalsIgnoreCase("email_attached")) {
            return mapToEmailAttached(message.getData())
                    .flatMap(emailAttached -> {
                        logger.info(String.format("emailAttachedUseCase.send -> %s", emailAttached));
                        return emailAttachedUseCase.send(emailAttached, message.getPriority(), message.getMessageId());
                    });
        }
        return null;
    }

    public Mono<Void> listen(String queueUrl) {
        return getMessages(queueUrl)
                .flatMap(response -> {
                    logger.info(String.format("listen: %s -> %s", queueUrl, response));
                    return Mono.just(response);
                })
                .flatMap(this::mapObject)
                .flatMap(tuple -> {
                    boolean success = tuple.getT2();
                    if (success) {
                        deleteMessage(queueUrl, tuple.getT1());
                    }
                    return Mono.just(true);
                })
                .then();
    }

    public Mono<Tuple2<Message, Boolean>> mapObject(Message message) {
        ObjectMapper objectMapper = new ObjectMapper();
        QueueMessage queueMessage = null;
        try {
            queueMessage = objectMapper.readValue(message.body(), QueueMessage.class);
            logger.info(String.format("mapObject -> %s", queueMessage));
        } catch (IOException e) {
            logger.error(String.format("Error mapeando el message: %s", e.getMessage()));
        }

        return Mono.zip(Mono.just(message), sendMessage(queueMessage));
    }

    public Mono<SmsRQ> mapToSms(Object message) {
        ObjectMapper objectMapper = new ObjectMapper();
        SmsRQ smsRQ = objectMapper.convertValue(message, SmsRQ.class);
        logger.info(String.format("mapToSms -> %s", smsRQ));
        return Mono.just(smsRQ);
    }

    public Mono<EmailBasicRQ> mapToEmailBasic(Object message) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<EmailBasicData> data = new ArrayList<>();
        EmailBasicData emailBasicData = objectMapper.convertValue(message, EmailBasicData.class);
        logger.info(String.format("mapToEmailBasic -> %s", emailBasicData));
        data.add(emailBasicData);
        return Mono.just(EmailBasicRQ.builder().data(data).build());
    }

    public Mono<DeleteMessageResponse> deleteMessage(String queueUrl, Message m) {
        DeleteMessageRequest deleteMessageRequest = getDeleteMessageRequest(queueUrl,
                m.receiptHandle());
        return Mono.fromFuture(client.deleteMessage(deleteMessageRequest))
                .doOnError(e -> {
                    logger.error(e.getMessage());
                });
    }

    public Flux<Message> getMessages(String queueUrl) {
        return getReceiveMessageRequest(queueUrl)
                .flatMap(req -> {
                    logger.info(String.format("getMessages: %s", queueUrl));
                    return Mono.fromFuture(client.receiveMessage(req))
                            .doOnError(e -> logger.info(e.getMessage()));
                })
                .flatMapMany(response -> Flux.fromIterable(response.messages()));
    }

    public Mono<ReceiveMessageRequest> getReceiveMessageRequest(String queueUrl) {
        return Mono.just(ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(20)
                .visibilityTimeout(30)
                .build());
    }

    public DeleteMessageRequest getDeleteMessageRequest(String queueUrl, String receiptHandle) {
        return DeleteMessageRequest.builder().queueUrl(queueUrl).receiptHandle(receiptHandle).build();
    }

    public Flux<Void> startListener(String queueUrl) {
        return listen(queueUrl)
                .doOnNext(it -> logger.info(String.format("startListener: %s", queueUrl)))
                .doOnSuccess(e -> logger.debug(String.format("startListener terminated: %s", queueUrl)))
                .onErrorResume(t -> Mono.empty())
                .doOnError(e -> logger.error(String.format("startListener error: %s -> %s", queueUrl, e)))
                .repeat();
    }

    public Mono<EmailAttachedRQ> mapToEmailAttached(Object message) {
        ObjectMapper objectMapper = new ObjectMapper();
        DataEmail attachedData = objectMapper.convertValue(message, DataEmail.class);
        logger.info(String.format("mapToEmailBasic -> %s", attachedData));
        return Mono.just(EmailAttachedRQ.builder().data(attachedData).build());
    }
}
