package co.com.bancolombia.notification.usecase.test;

import co.com.bancolombia.notification.model.email.attachment.EmailAttachedRQ;
import co.com.bancolombia.notification.model.email.attachment.EmailAttachedRS;
import co.com.bancolombia.notification.model.email.EmailBasicRSData;
import co.com.bancolombia.notification.model.email.gateways.EmailAttachedGateway;
import co.com.bancolombia.notification.usecase.emailattachment.EmailAttachedUseCase;
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

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class EmailAttachedUseCaseTest {

    private EmailAttachedRS responseMail;
    private EmailAttachedRQ emailAttachedRQ;
    @Mock
    private EmailAttachedGateway emailAttachGateway;
    @InjectMocks
    private EmailAttachedUseCase emailAttachedUseCase;


    @Before
    public void initModels() {
        responseMail = EmailAttachedRS.builder().data(Arrays.asList(EmailBasicRSData.builder().responseMessage("200 Ok").build())).build();
        emailAttachedRQ = EmailAttachedRQ.formTest();
    }

    @Test
    public void testSendAttachedMail() {
        when(emailAttachGateway.send(any(), any(), any()))
                .thenReturn(Mono.just(responseMail));
        StepVerifier.create(emailAttachedUseCase.send(emailAttachedRQ, "2", "fc25ad12-5b07-4b0f-9b7d-61c8c7dff2b5"))
                .expectNext(true)
                .verifyComplete();
    }
}
