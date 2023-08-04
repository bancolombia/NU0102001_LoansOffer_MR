package co.com.bancolombia.utilities.config;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class HeadersFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        HttpHeaders httpHeaders = serverWebExchange.getResponse().getHeaders();
        httpHeaders.add("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        httpHeaders.add("Content-Security-Policy", "default-src 'self'; img-src data: https:; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; upgrade-insecure-requests;");
        httpHeaders.add("X-Content-Type-Options", "nosniff");
        httpHeaders.add("X-Frame-Options", "DENY");
        httpHeaders.add("X-XSS-Protection", "1; mode=block");
        httpHeaders.add("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        httpHeaders.add("Pragma", "no-cache");

        return webFilterChain.filter(serverWebExchange);
    }
}