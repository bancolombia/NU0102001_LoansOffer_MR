package co.com.bancolombia.libreinversion.usecase.redis;


import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.offer.EnableOfferData;
import co.com.bancolombia.libreinversion.model.offer.OffersOperation;
import co.com.bancolombia.libreinversion.model.product.ProductFactory;
import co.com.bancolombia.libreinversion.model.product.gateways.MAPGateways;
import co.com.bancolombia.libreinversion.model.request.ConfirmOfferRQ;
import co.com.bancolombia.libreinversion.model.request.SellOfferRQ;
import co.com.bancolombia.libreinversion.model.request.gateways.RedisGateways;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class RedisUseCase {

    private final RedisGateways redisGateways;
    private final MAPGateways mapGateways;

    public Mono<Object> getData(String key) {
        return redisGateways.getData(key);
    }

    public Mono<Object> setData(String key, Object object) {
        return mapGateways.getTimeOffer().flatMap(rule -> Mono.just(rule.getData()))
                .flatMap(param -> {
                    Long time = Long.parseLong(ProductFactory.getfirstValue(param)) * Constant.HOUR_TODAY;
                    return redisGateways.setData(key, object, time);
                });
    }

    public Mono<List<OffersOperation>> getDataOperation(String idNumber) {
        List<OffersOperation> listOffersOperation = new ArrayList<>();
        OffersOperation.OffersOperationBuilder objects = OffersOperation.builder();
        return Mono.just(idNumber)
                .flatMap(data -> {

                    redisGateways.getData(Constant.RESPONSE_ENABLE + "" + idNumber)
                            .flatMap(dataCache -> {
                                if ((dataCache instanceof EnableOfferData)) {
                                    objects.enable((EnableOfferData) dataCache).build();
                                }
                                return Mono.just(objects.build());
                            }).subscribe();

                    redisGateways.getData(Constant.REQUEST_CONFIRM + "" + idNumber)
                            .flatMap(dataCache -> {
                                if ((dataCache instanceof ConfirmOfferRQ)) {
                                    objects.confirm((ConfirmOfferRQ) dataCache).build();
                                }
                                return Mono.just(objects.build());
                            }).subscribe();

                    redisGateways.getData(Constant.REQUEST_SELL + "" + idNumber)
                            .flatMap(dataCache -> {
                                if ((dataCache instanceof SellOfferRQ)) {
                                    objects.sell((SellOfferRQ) dataCache).build();
                                }
                                return Mono.just(objects.build());
                            }).subscribe();
                    listOffersOperation.add(objects.build());
                    return Mono.just(listOffersOperation);
                });
    }
}
