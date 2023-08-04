package com.bancolombia.libreinversion.apis.test;

import co.com.bancolombia.libreinversion.model.offer.selltestutil.UtilTestSellRQ;
import co.com.bancolombia.libreinversion.redis.Operations;
import co.com.bancolombia.libreinversion.request.RedisAdapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class RedisAdapterTest {

    @Mock
    private Operations operations;
    @InjectMocks
    private RedisAdapter redisAdapter;

    @Before
    public void init() throws IOException {
        Mockito.doReturn(Mono.just(UtilTestSellRQ.buildSellOfferRQ())).when(operations).getData(any());
        Mockito.doReturn(Mono.just(UtilTestSellRQ.buildConfirmOfferRQ()))
                .when(operations).getDataCacheCustomer(anyString());
    }

    @Test
    public void test() {
        Long TIME = 10L;
        String KEY = "asdf";
        when(redisAdapter.getDataCacheCustomer(KEY)).thenReturn(Mono.just(UtilTestSellRQ.buildConfirmOfferRQ()));
        when(redisAdapter.getData(KEY)).thenReturn(Mono.just(UtilTestSellRQ.buildSellOfferRQ()));
        when(redisAdapter.setData(KEY, UtilTestSellRQ.buildSellOfferRQ(), TIME))
                .thenReturn(Mono.just(UtilTestSellRQ.buildSellOfferRQ()));
        StepVerifier.create(redisAdapter.getCompleteDataFromCache("123456789"))
                .expectNextMatches(confirmOfferComplete -> confirmOfferComplete!=null).verifyComplete();
    }
}
