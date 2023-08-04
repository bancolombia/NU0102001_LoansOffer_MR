package co.com.bancolombia.libreinversion.request;


import co.com.bancolombia.libreinversion.model.account.AccountFactory;
import co.com.bancolombia.libreinversion.model.account.rest.DepositAccountResponse;
import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerCommercial;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerContact;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerDetail;
import co.com.bancolombia.libreinversion.model.notification.rest.ResponseRetrieveInfo;
import co.com.bancolombia.libreinversion.model.offer.sellutil.UtilSell;
import co.com.bancolombia.libreinversion.model.request.ConfirmOfferComplete;
import co.com.bancolombia.libreinversion.model.request.ConfirmOfferRQ;
import co.com.bancolombia.libreinversion.model.request.gateways.RedisGateways;
import co.com.bancolombia.libreinversion.model.stoc.GeneralInformation;
import co.com.bancolombia.libreinversion.redis.Operations;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RedisAdapter implements RedisGateways {

    private final Operations operations;
    private static TechLogger log = LoggerFactory.getLog(RedisAdapter.class.getName());

    @Override
    public Mono<ConfirmOfferRQ> getDataCacheCustomer(String key) {
        return operations.getDataCacheCustomer(key);
    }

    @Override
    public Mono<Void> deleteDataCacheCustomer(String key) {
        return operations.deleteDataCacheCustomer(key);
    }

    @Override
    public Mono<Object> getData(String key) {
        return operations.getData(key);
    }

    @Override
    public Mono<Object> setData(String key, Object object, Long timeUnit) {
        return operations.setData(key, object, timeUnit);
    }

    @Override
    public Mono<ConfirmOfferComplete> getCompleteDataFromCache(String customerIdNumber) {
        ConfirmOfferComplete.ConfirmOfferCompleteBuilder confirmOfferComplete = ConfirmOfferComplete.builder();

        String idNumberPadLeft = AccountFactory.padLeftZeros(customerIdNumber, Constant.LENGTH_ACCOUNT_ID_NUMBER);

        return Mono.zip(
                getData(Constant.BUSINESOPORTUNITI_ID),
                getData(Constant.REQUEST_CONFIRM)
        ).flatMap(zip -> {
            confirmOfferComplete
                    .responseCustomerCommercial(castObject(zip.getT1(), ResponseCustomerCommercial.class))
                    .depositAccountResponse(castObject(zip.getT2(), DepositAccountResponse.class))
                    .build();
            return Mono.just(confirmOfferComplete.build());
        }).onErrorResume(e -> Mono.error(UtilSell
                .dataClientErrorProcess("getCompleteDataFromCache", "", e)));
    }

    @Override
    public <T> T castObject(Object obj, Class<T> type) {
        try {
            if (type != null) {
                return type.cast(obj);
            } else {
                return null;
            }
        } catch (RuntimeException e) {
            return null;
        }
    }
}
