package co.com.bancolombia.notification.usecase.test;

import co.com.bancolombia.notification.model.sms.SmsRQData;
import co.com.bancolombia.notification.model.sms.SmsRS;
import co.com.bancolombia.notification.model.sms.SmsRQ;
import co.com.bancolombia.notification.model.sms.SmsRSData;
import co.com.bancolombia.notification.model.sms.SmsMessage;
import co.com.bancolombia.notification.model.sms.gateways.SmsGateways;
import co.com.bancolombia.notification.usecase.sms.SmsUseCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class SmsUseCaseTest {

    private final String msgId = "fc25ad12-5b07-4b0f-9b7d-61c8c7dff2b5";
    private SmsRS smsRS;
    private SmsRQ smsRQ;
    @Mock
    private SmsGateways smsGateways;
    @InjectMocks
    private SmsUseCase smsUseCase;


    @Before
    public void initModels() {
        smsRS = smsRS.builder().data(SmsRSData.builder()
                .messageId(msgId)
                .totalSendedAlerts(0).build()).build();
        smsRQ = SmsRQ.builder().data(SmsRQData.builder()
                .smsMessage(SmsMessage.builder()
                        .emergingMessageId(msgId)
                        .priorityId("2")
                        .transactionId("test")
                        .phoneNumbers(Arrays.asList(123456798))
                        .build()).build()).build();
    }

    @Test
    public void testSms() {
        when(smsGateways.send(any(), any(), any())).thenReturn(Mono.just(smsRS));
        StepVerifier.create(smsUseCase.send(smsRQ, msgId, "2"))
                .expectNext(true).verifyComplete();
    }
}
