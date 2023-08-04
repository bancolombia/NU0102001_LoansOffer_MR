package co.com.bancolombia.utilities.config;

import co.com.bancolombia.utilities.model.gateways.CustomerCommercialGateway;
import co.com.bancolombia.utilities.model.gateways.CustomerContactGateway;
import co.com.bancolombia.utilities.model.gateways.CustomerDetailsGateway;
import co.com.bancolombia.utilities.model.gateways.InstallmentsGateway;
import co.com.bancolombia.utilities.model.gateways.InterestRateGateway;
import co.com.bancolombia.utilities.model.gateways.MapGateway;
import co.com.bancolombia.utilities.usecase.AmortizationUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {
    @Bean
    public AmortizationUseCase amortizationUseCase(
            CustomerContactGateway customerContactGateway, CustomerCommercialGateway customerCommercialGateway,
            CustomerDetailsGateway customerDetailsGateway, InstallmentsGateway installmentsGateway,
            InterestRateGateway interestRateGateway, MapGateway mapGateway) {
        return new AmortizationUseCase(customerContactGateway, customerCommercialGateway,
                customerDetailsGateway, installmentsGateway, interestRateGateway, mapGateway);
    }
}