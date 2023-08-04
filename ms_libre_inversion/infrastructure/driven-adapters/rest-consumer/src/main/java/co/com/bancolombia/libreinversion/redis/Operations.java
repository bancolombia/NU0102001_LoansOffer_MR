package co.com.bancolombia.libreinversion.redis;


import co.com.bancolombia.libreinversion.model.request.ConfirmOfferRQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Component
public class Operations {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    ValueOperations<String, Object> operation;

    public Operations(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.operation = redisTemplate.opsForValue();
    }

    public Mono<ConfirmOfferRQ> getDataCacheCustomer(String key) {
        return Mono.just(key).flatMap(ke -> {
            Object obj = operation.get(ke);
            if (obj != null) {
                return Mono.just((ConfirmOfferRQ) obj);
            }
            return Mono.just(new ConfirmOfferRQ());
        });
    }

    public Mono<Void> deleteDataCacheCustomer(String key) {

        return Mono.just(key).flatMap(ke -> {
            if (redisTemplate.hasKey(ke).booleanValue()) {
                redisTemplate.delete(ke);
            }
            return Mono.empty();
        });
    }

    public Mono<Object> getData(String key) {
        return Mono.just(key).flatMap(ke -> {
            Object obj = operation.get(ke);
            if (obj != null) {
                return Mono.just(obj);
            }
            return Mono.just(new Object());
        });
    }

    public Mono<Object> setData(String key, Object bbject, Long timeUnit) {
        return Mono.just(bbject).flatMap(data -> {
            operation.set(key, data, timeUnit, TimeUnit.HOURS);
            return Mono.just(data);
        });
    }
}
