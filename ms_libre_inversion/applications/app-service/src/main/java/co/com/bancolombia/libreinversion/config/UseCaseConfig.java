package co.com.bancolombia.libreinversion.config;

import co.com.bancolombia.libreinversion.model.account.gateways.AccountGateways;
import co.com.bancolombia.libreinversion.model.customer.gateways.CustomerDataGateways;
import co.com.bancolombia.libreinversion.model.events.gateways.EventsGateway;
import co.com.bancolombia.libreinversion.model.loanamount.gateways.LoanAmountGateway;
import co.com.bancolombia.libreinversion.model.loansdisbursements.gateway.DisbursementsGateWay;
import co.com.bancolombia.libreinversion.model.notification.gateways.NotificationGateways;
import co.com.bancolombia.libreinversion.model.product.gateways.MAPGateways;
import co.com.bancolombia.libreinversion.model.rate.gateways.InterestRateAdapterGateways;
import co.com.bancolombia.libreinversion.model.request.gateways.AmazonS3Gateways;
import co.com.bancolombia.libreinversion.model.request.gateways.DocManagementGateway;
import co.com.bancolombia.libreinversion.model.request.gateways.HtmlPdfGateways;
import co.com.bancolombia.libreinversion.model.request.gateways.RedisGateways;
import co.com.bancolombia.libreinversion.model.request.gateways.TemplateHtmlGateways;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.gateway.GestionarPagareElectV1Gateway;
import co.com.bancolombia.libreinversion.model.stoc.gateways.OpportunitiesGateways;
import co.com.bancolombia.libreinversion.usecase.disbursements.DisbursementsUseCase;
import co.com.bancolombia.libreinversion.usecase.acceptsell.AcceptSellUseCase;
import co.com.bancolombia.libreinversion.usecase.acceptsell.DocSellOfferUseCase;
import co.com.bancolombia.libreinversion.usecase.account.AccountUseCase;
import co.com.bancolombia.libreinversion.usecase.contactability.ContactabilityUseCase;
import co.com.bancolombia.libreinversion.usecase.customer.CustomerUseCase;
import co.com.bancolombia.libreinversion.usecase.notification.NotificationUseCase;
import co.com.bancolombia.libreinversion.usecase.redis.RedisUseCase;
import co.com.bancolombia.libreinversion.usecase.request.ConfirmOfferCompleteUseCase;
import co.com.bancolombia.libreinversion.usecase.soap.GestionarPagareElectUseCase;
import co.com.bancolombia.libreinversion.usecase.stoc.OpportunitiesUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CustomerUseCase createCustomerUseCase(CustomerDataGateways customerGateways,
                                                 MAPGateways mapGateways,
                                                 NotificationGateways notificationGateways,
                                                 RedisGateways redisGateways
    ) {
        return new CustomerUseCase(customerGateways,
                notificationGateways,
                mapGateways, redisGateways);
    }

    @Bean
    public AccountUseCase createAccountUseCase(
            AccountGateways accountGateways,
            MAPGateways mapGateways,
            RedisGateways redisGateways) {
        return new AccountUseCase(accountGateways, mapGateways, redisGateways);
    }

    @Bean
    public NotificationUseCase notificationUseCase(
            NotificationGateways notificationGateways,
            MAPGateways mapGateways) {
        return new NotificationUseCase(notificationGateways, mapGateways);
    }

    @Bean
    public OpportunitiesUseCase opportunitiesUseCase(MAPGateways mapGateways,
                                                     OpportunitiesGateways opGateways,
                                                     CustomerDataGateways customerGateways,
                                                     NotificationGateways notificationGateways,
                                                     InterestRateAdapterGateways interestRateAdapterGateways,
                                                     RedisGateways redisGateways,
                                                     LoanAmountGateway loanAmountGateway){
        return new OpportunitiesUseCase(
                mapGateways, opGateways,
                customerGateways,
                notificationGateways,
                interestRateAdapterGateways,
                redisGateways,
                loanAmountGateway
        );
    }

    @Bean
    public ConfirmOfferCompleteUseCase confirmOfferCompleteUseCase(
            MAPGateways mapGateways,
            HtmlPdfGateways htmlPdfGateways,
            TemplateHtmlGateways templateHtmlGateways,
            AmazonS3Gateways amazonS3Gateways,
            RedisGateways redisGateways
    ) {
        return new ConfirmOfferCompleteUseCase(mapGateways,
                htmlPdfGateways, templateHtmlGateways, amazonS3Gateways, redisGateways);
    }

    @Bean
    public GestionarPagareElectUseCase gestionarPagareElectUseCase(GestionarPagareElectV1Gateway
                                                                               gestionarPagareElectV1Gateway,
                                                                   MAPGateways mapGateways,
                                                                   RedisGateways redisGateways) {
        return new GestionarPagareElectUseCase(gestionarPagareElectV1Gateway, mapGateways, redisGateways);
    }

    @Bean
    public AcceptSellUseCase getAcceptSellUseCase(MAPGateways mapGateways, OpportunitiesGateways opportunitiesGateways,
                                                  RedisGateways redisGateways) {
        return new AcceptSellUseCase(mapGateways, redisGateways);
    }

    @Bean
    public RedisUseCase redisUseCase(
            RedisGateways redisGateways,
            MAPGateways mapGateways
    ) {
        return new RedisUseCase(redisGateways, mapGateways);
    }

    @Bean
    public ContactabilityUseCase getContactabilityUseCase(EventsGateway eventsGateway) {
        return new ContactabilityUseCase(eventsGateway);
    }

    @Bean
    public DocSellOfferUseCase getDocSellOfferUseCase(TemplateHtmlGateways templateHtmlGateways,
                                                      HtmlPdfGateways htmlPdfGateways,
                                                      DocManagementGateway docContributionGateway,
                                                      AmazonS3Gateways amazonS3Gateways) {
        return new DocSellOfferUseCase(templateHtmlGateways, htmlPdfGateways, docContributionGateway, amazonS3Gateways);
    }

    @Bean
    public DisbursementsUseCase getDisbursementsUseCase(DisbursementsGateWay disbursementsGateWay) {
        return new DisbursementsUseCase(disbursementsGateWay);
    }
}
