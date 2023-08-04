package co.com.bancolombia.libreinversion.config;


import co.com.bancolombia.libreinversion.config.model.S3ConnectionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClientBuilder;


@Configuration
public class AmazonS3Config {

    @Bean
    public S3AsyncClient s3AsyncClient(S3ConnectionProperties s3Properties) {
        return getBuilder(s3Properties)
                .credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                .build();
    }

    private S3AsyncClientBuilder getBuilder(S3ConnectionProperties s3Properties) {
        return S3AsyncClient.builder()
                .region(Region.of(s3Properties.getRegion()));
    }

}
