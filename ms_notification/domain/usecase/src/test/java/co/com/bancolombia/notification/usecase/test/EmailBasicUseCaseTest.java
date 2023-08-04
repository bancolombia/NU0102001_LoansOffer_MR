package co.com.bancolombia.notification.usecase.test;

import co.com.bancolombia.notification.model.email.EmailBasicRQ;
import co.com.bancolombia.notification.model.email.EmailBasicRS;
import co.com.bancolombia.notification.model.email.EmailBasicRSData;
import co.com.bancolombia.notification.model.email.gateways.EmailBasicGateways;
import co.com.bancolombia.notification.usecase.emailbasic.EmailBasicUseCase;
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
public class EmailBasicUseCaseTest {

    private EmailBasicRS responseMail;
    private EmailBasicRQ emailBasicRQ;
    @Mock
    private EmailBasicGateways emailBasicGateways;
    @InjectMocks
    private EmailBasicUseCase emailBasicUseCase;


    @Before
    public void initModels() {
        responseMail = EmailBasicRS.builder().data(Arrays.asList(EmailBasicRSData.builder().responseMessage("200 Ok").build())).build();
        emailBasicRQ = EmailBasicRQ.formTest();
    }

    @Test
    public void testSendBasicMail() {
        when(emailBasicGateways.send(any(), any(), any())).thenReturn(Mono.just(responseMail));
        StepVerifier.create(emailBasicUseCase.send(emailBasicRQ, "2", "fc25ad12-5b07-4b0f-9b7d-61c8c7dff2b5"))
                .expectNext(true).verifyComplete();
    }
}
