package co.com.bancolombia.libreinversion.config.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "adapter.aws.s3")
public class S3ConnectionProperties {

    private String bucketName;
    private String region;
    private String endpoint;


}