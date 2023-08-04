package co.com.bancolombia.notification.api;

import co.com.bancolombia.notification.model.commons.Constant;
import co.com.bancolombia.notification.model.email.attachment.EmailAttachedRQ;
import co.com.bancolombia.notification.model.email.EmailBasicRQ;
import co.com.bancolombia.notification.usecase.emailattachment.EmailAttachedUseCase;
import co.com.bancolombia.notification.usecase.emailbasic.EmailBasicUseCase;
import co.com.bancolombia.notification.usecase.sms.SmsUseCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        Router.class, HandlerEmailAttached.class, HandlerSms.class, HealthHandler.class, HandlerEmailBasic.class})
@WebFluxTest(controllers = Router.class)
public class HandlerEmailSendTest {

    private String basePath = "/api/v1/email/";
    private EmailAttachedRQ emailAttachedRQ;
    private EmailBasicRQ emailBasicRQ;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private EmailAttachedUseCase emailAttacdUseCase;
    @MockBean
    private SmsUseCase smsUseCase;
    @MockBean
    private EmailBasicUseCase EmailBasicUseCase;


    @Before
    public void init() {
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
        emailAttachedRQ = EmailAttachedRQ.formTest();
        emailBasicRQ = EmailBasicRQ.formTest();
        Mockito.when(emailAttacdUseCase.send(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(Boolean.TRUE));
        Mockito.when(EmailBasicUseCase.send(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(Boolean.TRUE));
    }

    @Test
    public void testSendAttached() {
        webTestClient
                .post()
                .uri((basePath + "attached"))
                .accept(MediaType.APPLICATION_JSON)
                .header(Constant.MESSAGE_ID, "eda2e01f-2353-4810-84c8-16472c3c4414")
                .header(Constant.PRIORITY, "2")
                .body(BodyInserters.fromPublisher(Mono.just(emailAttachedRQ), EmailAttachedRQ.class))
                .exchange()
                .expectBody(Boolean.class)
                .consumeWith((response) -> {
                    Assert.assertEquals(true, response.getResponseBody());
                });
    }

    @Test
    public void testSendBasic() {
        webTestClient
                .post()
                .uri((basePath + "basic"))
                .accept(MediaType.APPLICATION_JSON)
                .header(Constant.MESSAGE_ID, "eda2e01f-2353-4810-84c8-16472c3c4414")
                .header(Constant.PRIORITY, "2")
                .body(BodyInserters.fromPublisher(Mono.just(emailBasicRQ), EmailBasicRQ.class))
                .exchange()
                .expectBody(Boolean.class)
                .consumeWith((response) -> {
                    Assert.assertEquals(true, response.getResponseBody());
                });
    }
}
