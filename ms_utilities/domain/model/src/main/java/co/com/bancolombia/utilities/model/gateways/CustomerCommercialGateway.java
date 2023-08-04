package co.com.bancolombia.utilities.model.gateways;

import co.com.bancolombia.utilities.model.customer.RequestCustomer;
import co.com.bancolombia.utilities.model.customer.ResponseCustomerCommercial;
import reactor.core.publisher.Mono;

public interface CustomerCommercialGateway {
    Mono<ResponseCustomerCommercial> retrieveCustomerCommercial(RequestCustomer request, String msgId);
}