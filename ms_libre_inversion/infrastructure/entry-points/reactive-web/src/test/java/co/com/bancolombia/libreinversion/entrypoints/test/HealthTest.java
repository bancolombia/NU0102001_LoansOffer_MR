package co.com.bancolombia.libreinversion.entrypoints.test;


import co.com.bancolombia.libreinversion.api.handler.HealthHandler;

import co.com.bancolombia.libreinversion.api.router.HealthRouter;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {HealthRouter.class, HealthHandler.class})

@TestPropertySource(properties = {
        "spring.webflux.base-path=/api/v1",
        "entry-point.health=/health",
})
@WebFluxTest
public class HealthTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private WebTestClient webTestClient;



    @Value("${spring.webflux.base-path}")
    private String basePath;

    @Value("${entry-point.health}")
    private String routeHealth;


    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
    }

    @Before
    public void init() {

    }

    @Test
    public void test() {
        health();
    }

    public void health() {

        webTestClient
                .get()
                .uri((routeHealth))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();
    }
}
