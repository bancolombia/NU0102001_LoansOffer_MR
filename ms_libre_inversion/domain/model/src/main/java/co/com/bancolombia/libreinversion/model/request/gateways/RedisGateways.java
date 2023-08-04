package co.com.bancolombia.libreinversion.model.request.gateways;

import co.com.bancolombia.libreinversion.model.request.ConfirmOfferComplete;
import co.com.bancolombia.libreinversion.model.request.ConfirmOfferRQ;
import reactor.core.publisher.Mono;

public interface RedisGateways {

    Mono<ConfirmOfferRQ> getDataCacheCustomer(String key);
    Mono<Void> deleteDataCacheCustomer(String key);
    Mono<Object> getData(String key);
    Mono<Object> setData(String key, Object bbject, Long timeUnit);
    Mono<ConfirmOfferComplete> getCompleteDataFromCache(String customerIdNumber);
    <T> T castObject(Object obj, Class<T> type);
}
