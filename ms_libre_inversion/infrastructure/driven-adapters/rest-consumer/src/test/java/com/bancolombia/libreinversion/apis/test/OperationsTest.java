package com.bancolombia.libreinversion.apis.test;

import co.com.bancolombia.libreinversion.model.offer.selltestutil.UtilTestSellRQ;
import co.com.bancolombia.libreinversion.redis.Operations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;

import java.io.IOException;

@RunWith(MockitoJUnitRunner.Silent.class)
@ContextConfiguration(classes = {Operations.class})
public class OperationsTest {

    private Operations operations;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;


    @Before
    public void init() throws IOException {
        operations = new Operations(redisTemplate);
    }

    @Test
    public void test() {
        Long TIME = 10L;
        String KEY = "asdf";
        StepVerifier.create(operations.getDataCacheCustomer(KEY)).expectNext(UtilTestSellRQ.buildConfirmOfferRQ());
        StepVerifier.create(operations.getData(KEY)).expectNext(UtilTestSellRQ.buildSellOfferRQ());
        StepVerifier.create(operations.setData(KEY, UtilTestSellRQ.buildSellOfferRQ(), TIME))
                .expectNext(UtilTestSellRQ.buildSellOfferRQ());
        StepVerifier.create(operations.deleteDataCacheCustomer(KEY))
                .verifyComplete();


    }
}
