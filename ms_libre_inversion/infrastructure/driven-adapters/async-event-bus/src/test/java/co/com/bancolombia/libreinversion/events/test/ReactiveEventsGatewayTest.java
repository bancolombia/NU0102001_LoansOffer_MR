package co.com.bancolombia.libreinversion.events.test;

import co.com.bancolombia.libreinversion.events.ReactiveEventsGateway;
import co.com.bancolombia.libreinversion.model.offer.selltestutil.UtilTestSellRQ;
import co.com.bancolombia.libreinversion.model.events.QueueMessageEmail;
import co.com.bancolombia.libreinversion.model.events.QueueMsgEmailAttached;
import co.com.bancolombia.libreinversion.model.events.gateways.EventsGateway;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReactiveEventsGatewayTest {

    @Mock
    private EventsGateway eventsGateway;
    @Mock
    private SqsAsyncClient sqsAsyncClient;
    @InjectMocks
    private ReactiveEventsGateway reactiveEventsGateway;

    @Before
    public void init() {
        ReflectionTestUtils.setField(reactiveEventsGateway, "urlAwsSqs", "Test", String.class);
        CompletableFuture<SendMessageResponse> messageResponse = CompletableFuture.completedFuture(
                SendMessageResponse.builder()
                        .messageId("testId")
                        .sequenceNumber("200 ok")
                        .build());
        when(sqsAsyncClient.sendMessage(any(SendMessageRequest.class))).thenReturn(messageResponse);
    }

    @Test
    public void testSendBasic() {
        QueueMessageEmail data = UtilTestSellRQ.buildQueueMessageEmail();
        StepVerifier.create(reactiveEventsGateway.sendEmailBasic(data)).expectNext(true).verifyComplete();
    }

    @Test
    public void testSendAttached() {
        QueueMsgEmailAttached attached = UtilTestSellRQ.buildQueueMsgEmailAttached();
        StepVerifier.create(reactiveEventsGateway.sendEmailAttached(attached)).expectNext(true).verifyComplete();
    }
}
