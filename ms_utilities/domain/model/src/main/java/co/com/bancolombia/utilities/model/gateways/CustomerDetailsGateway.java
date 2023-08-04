package co.com.bancolombia.utilities.model.gateways;

import co.com.bancolombia.utilities.model.customer.RequestCustomer;
import co.com.bancolombia.utilities.model.customer.ResponseCustomerDetails;
import reactor.core.publisher.Mono;

public interface CustomerDetailsGateway {
    Mono<ResponseCustomerDetails> retrieveCustomerDetails(RequestCustomer request, String msgId);
}