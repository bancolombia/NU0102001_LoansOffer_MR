package co.com.bancolombia.libreinversion.usecase.test;


import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.notification.rest.ResponseRetrieveInfoData;
import co.com.bancolombia.libreinversion.model.offer.OffersOperation;
import co.com.bancolombia.libreinversion.model.product.ResponseData;
import co.com.bancolombia.libreinversion.model.product.RuleResponse;
import co.com.bancolombia.libreinversion.model.product.gateways.MAPGateways;
import co.com.bancolombia.libreinversion.model.request.RetrieveOfferRQ;
import co.com.bancolombia.libreinversion.model.request.gateways.RedisGateways;
import co.com.bancolombia.libreinversion.usecase.redis.RedisUseCase;
import co.com.bancolombia.libreinversion.usecase.request.ConfirmOfferCompleteUseCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class RedisUseCaseTest {


    @Mock
    private RedisGateways redisGateways;

    @Mock
    private MAPGateways mapGateways;

    @InjectMocks
    private RedisUseCase redisUseCase;

    private RuleResponse ruleResponse;


    @Before
    public void init() {

        Map<String, Object> map = new HashMap<>();
        ruleResponse = RuleResponse.builder()
                .data(ResponseData.builder().valid(true).utilLoad(map).build())
                .build();

        Mockito.doReturn(Mono.just(ruleResponse))
                .when(mapGateways).getTimeOffer();

        Mockito.doReturn(Mono.just(new Object()))
                .when(redisGateways)
                .getData(Mockito.any());

        Mockito.doReturn(Mono.just(new Object()))
                .when(redisGateways)
                .setData(Mockito.any(), Mockito.any(), Mockito.any());

    }

    @Test
    public void test() {

        Mono<Object> redis = redisUseCase
                .getData(Constant.REQUEST_SELL );

        Mono<Object> setData = redisUseCase
                .setData(Constant.REQUEST_SELL, new Object());

        Mono<List<OffersOperation>> operation = redisUseCase
                .getDataOperation(Constant.REQUEST_SELL);


        StepVerifier.create(redis)
                .expectSubscription()
                .thenConsumeWhile(x -> true)
                .verifyComplete();

        StepVerifier.create(setData)
                .expectSubscription()
                .thenConsumeWhile(x -> true)
                .verifyComplete();

        StepVerifier.create(setData)
                .expectSubscription()
                .thenConsumeWhile(x -> true)
                .verifyComplete();

        StepVerifier.create(operation)
                .expectSubscription()
                .thenConsumeWhile(x -> true)
                .verifyComplete();

        Mockito.verify(mapGateways, Mockito.atLeastOnce()).getTimeOffer();
        Mockito.verify(redisGateways, Mockito.atLeastOnce()).getData(Mockito.any());
        Mockito.verify(redisGateways, Mockito.atLeastOnce()).setData(Mockito.any(), Mockito.any(), Mockito.any());
    }
}
