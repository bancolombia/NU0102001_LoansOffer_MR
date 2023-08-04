package co.com.bancolombia.notification.events.config;

import co.com.bancolombia.notification.events.SqsAwsEntryPoint;
import co.com.bancolombia.notification.usecase.emailattachment.EmailAttachedUseCase;
import co.com.bancolombia.notification.usecase.emailbasic.EmailBasicUseCase;
import co.com.bancolombia.notification.usecase.sms.SmsUseCase;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
@RequiredArgsConstructor
public class SqsConfiguration {

    @Value("${sqs.queueUrl}")
    private String queueUrl;

    private static final Logger logger = LoggerFactory.getLogger(SqsConfiguration.class);

    private final EmailBasicUseCase emailBasicUseCase;
    private final SmsUseCase smsUseCase;
    private final EmailAttachedUseCase emailAttachedUseCase;

    @Bean
    public SqsAwsEntryPoint sqsAwsEntryPoint() {
        logger.info(String.format("sqsAwsEntryPoint %s", queueUrl));
        SqsAwsEntryPoint sqsAwsEntryPoint = new SqsAwsEntryPoint(sqsAsyncClient(), emailBasicUseCase, smsUseCase, emailAttachedUseCase);
        sqsAwsEntryPoint
                .startListener(queueUrl)
                .subscribe();
        return sqsAwsEntryPoint;
    }

    public SqsAsyncClient sqsAsyncClient() {
        return SqsAsyncClient.builder()
                .credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                .region(Region.US_EAST_1)
                .build();
    }
}
