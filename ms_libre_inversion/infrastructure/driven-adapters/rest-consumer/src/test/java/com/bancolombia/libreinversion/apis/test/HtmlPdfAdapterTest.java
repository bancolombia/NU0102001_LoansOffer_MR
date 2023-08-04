package com.bancolombia.libreinversion.apis.test;


import co.com.bancolombia.libreinversion.account.AccountAdapter;
import co.com.bancolombia.libreinversion.request.HtmlPdfAdapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.io.InputStream;

@RunWith(SpringRunner.class)
@WebFluxTest
@ContextConfiguration(classes = {HtmlPdfAdapter.class})
public class HtmlPdfAdapterTest {

    HtmlPdfAdapter htmlPdfAdapter;

    @Before
    public void init() {
        htmlPdfAdapter = new HtmlPdfAdapter();
    }

    @Test
    public void test() throws Exception {
        String html = "test";
        Mono<byte[]> response = htmlPdfAdapter.htmlToPdfa1b(html);
        StepVerifier.create(response)
                .expectNextMatches(data -> data.length > 0)
                .verifyComplete();

        InputStream inputStream = null;
        try {
            inputStream = new ClassPathResource("sRGB_CS_profile.icm").getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Mono<byte[]> responsepdf = htmlPdfAdapter.htmlToPdfa1b(inputStream);
        StepVerifier.create(responsepdf)
                .expectNextMatches(data -> data.length > 0)
                .verifyComplete();

        Mono<byte[]> responseEncryp = htmlPdfAdapter
                .htmlToPdfa1bEncrypt(html, "test", "123789");
        StepVerifier.create(responseEncryp)
                .expectNextMatches(data -> data.length > 0)
                .verifyComplete();

        byte[] responseEn = htmlPdfAdapter
                .basicHtmlToPdf(html, "test");
        StepVerifier.create(Mono.just(responseEn))
                .expectNextMatches(data -> data.length > 0)
                .verifyComplete();
    }
}
