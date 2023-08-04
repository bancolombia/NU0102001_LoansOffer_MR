package co.com.bancolombia.libreinversion.model.account.gateways;

import co.com.bancolombia.libreinversion.model.account.rest.DepositAccountData;
import co.com.bancolombia.libreinversion.model.account.rest.DepositAccountResponse;
import reactor.core.publisher.Mono;

public interface AccountGateways {

    Mono<DepositAccountResponse> retrieveDepositAccounts(DepositAccountData args, String msgId);

}
