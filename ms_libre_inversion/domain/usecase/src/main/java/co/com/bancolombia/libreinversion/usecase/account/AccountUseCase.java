package co.com.bancolombia.libreinversion.usecase.account;

import co.com.bancolombia.libreinversion.model.account.AccountFactory;
import co.com.bancolombia.libreinversion.model.account.DepositAccounts;
import co.com.bancolombia.libreinversion.model.account.gateways.AccountGateways;
import co.com.bancolombia.libreinversion.model.account.rest.DepositAccountResponse;
import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.commons.ErrorEnum;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.libreinversion.model.product.gateways.MAPGateways;
import co.com.bancolombia.libreinversion.model.request.RetrieveOfferRQ;
import co.com.bancolombia.libreinversion.model.request.gateways.RedisGateways;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class AccountUseCase {

    private final AccountGateways accountGateways;
    private final MAPGateways mapGateways;
    private final RedisGateways redisGateways;

    public Mono<DepositAccounts> retrieveAccounts(RetrieveOfferRQ request, String msgId) {

        return mapGateways.getTimeOffer().flatMap(rule -> Mono.just(rule.getData()))
                .flatMap(param -> {
                    Long time = 5L;
                    Object especification = param.getUtilLoad().get(Constant.ESPECIFICATION_ACCOUNT);

                    return AccountFactory.buildRequest(request.getCustomer().getIdentification().getType(),
                            request.getCustomer().getIdentification().getNumber(), especification)
                            .flatMap(rq -> accountGateways.retrieveDepositAccounts(rq, msgId))
                            .switchIfEmpty(Mono.defer(() -> Mono.error(
                                    new LibreInversionException(ErrorEnum.MSG_LI010.getMessage(),
                                            ErrorEnum.MSG_LI010.getName(),
                                            ErrorEnum.MSG_LI010.getMessage(), "", "", msgId))))
                            .flatMap(count -> setDataCache(time, count,
                                    request.getCustomer().getIdentification().getNumber() ))
                            .flatMap(AccountFactory::buildResponse);
                });
    }

    private Mono<DepositAccountResponse> setDataCache(Long time,
                                                      DepositAccountResponse count,
                                                      String number) {
        return redisGateways.setData(Constant.DEPOSITACCOUNTS_ID +
                AccountFactory.padLeftZeros(number, Constant.LENGTH_ACCOUNT_ID_NUMBER),
                count, time).flatMap(coun -> Mono.just(count));
    }
}
