package co.com.bancolombia.utilities.api.handler;

import co.com.bancolombia.utilities.api.router.RouterHealth;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RouterHealth.class, HandlerHealth.class})
@WebFluxTest
@TestPropertySource(properties = {
        "routes.basePath=/api/v1",
        "routes.health=/health"
})
public class HandlerHealthTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private WebTestClient client;

    @Value("${routes.basePath}")
    private String basePath;

    @Value("${routes.health}")
    private String routeHealth;

    @BeforeEach
    void setUp() {
        client = WebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    public void shouldGetHealth() {
        client.get().uri(basePath.concat(routeHealth))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class);
    }
}