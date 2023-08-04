package co.com.bancolombia.libreinversion.model.loanamount.gateways;

import co.com.bancolombia.libreinversion.model.loanamount.RequestLoanAmount;
import co.com.bancolombia.libreinversion.model.loanamount.SuccessResponseLoanAmount;
import reactor.core.publisher.Mono;


public interface LoanAmountGateway {
    Mono<SuccessResponseLoanAmount> getLoanAmount(RequestLoanAmount request, String messageId);
}
