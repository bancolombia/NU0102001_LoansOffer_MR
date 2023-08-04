package co.com.bancolombia.libreinversion.model.request.gateways;

import reactor.core.publisher.Mono;

import java.io.InputStream;

public interface HtmlPdfGateways {

    Mono<byte[]>  htmlToPdfa1b(String html);
    Mono<byte[]>  htmlToPdfa1b(InputStream htmlInputStream);
    Mono<byte[]>  htmlToPdfa1bEncrypt(String html, String userPassword, String ownerPassword);
    byte[] basicHtmlToPdf(String html, String name);

}
