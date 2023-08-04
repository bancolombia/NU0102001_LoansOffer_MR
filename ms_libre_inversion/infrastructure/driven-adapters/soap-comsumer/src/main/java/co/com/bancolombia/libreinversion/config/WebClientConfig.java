package co.com.bancolombia.libreinversion.config;

import co.com.bancolombia.libreinversion.encoding.Jaxb2SoapDecoder;
import co.com.bancolombia.libreinversion.encoding.Jaxb2SoapEncoder;
import co.com.bancolombia.libreinversion.model.commons.Constant;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import javax.net.ssl.SSLException;
import java.util.concurrent.TimeUnit;


public class WebClientConfig {


    public static WebClient webClient() throws SSLException {
        TcpClient tcpClient = TcpClient.create();

        SslContext sslContext = SslContextBuilder
                .forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        HttpClient httpClient = HttpClient.create()
                .secure(sslSpec -> sslSpec.sslContext(sslContext))
                .resolver(DefaultAddressResolverGroup.INSTANCE);

        tcpClient
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Constant.TIMEOUT_HANDLER)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(Constant.TIMEOUT_HANDLER, TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(Constant.TIMEOUT_HANDLER, TimeUnit.MILLISECONDS));
                });

        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder().codecs(clientCodecConfigurer -> {
            clientCodecConfigurer.customCodecs().register(new Jaxb2SoapEncoder());
            clientCodecConfigurer.customCodecs().register(new Jaxb2SoapDecoder());
        }).build();

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient).wiretap(true).secure()))
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(exchangeStrategies)
                .build();
    }

}
