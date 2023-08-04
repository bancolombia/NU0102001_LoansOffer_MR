package co.com.bancolombia.notification.config;

import co.com.bancolombia.notification.model.email.gateways.EmailAttachedGateway;
import co.com.bancolombia.notification.model.email.gateways.EmailBasicGateways;
import co.com.bancolombia.notification.model.sms.gateways.SmsGateways;
import co.com.bancolombia.notification.usecase.emailattachment.EmailAttachedUseCase;
import co.com.bancolombia.notification.usecase.emailbasic.EmailBasicUseCase;
import co.com.bancolombia.notification.usecase.sms.SmsUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public EmailBasicUseCase emailBasicUseCase(EmailBasicGateways emailBasicGateways) {
        return new EmailBasicUseCase(emailBasicGateways);
    }

    @Bean
    public SmsUseCase smsUseCase(SmsGateways smsGateways) {
        return new SmsUseCase(smsGateways);
    }

    @Bean
    public EmailAttachedUseCase emailAttachedUseCase(EmailAttachedGateway emailAttachedGateway) {
        return new EmailAttachedUseCase(emailAttachedGateway);
    }

}
