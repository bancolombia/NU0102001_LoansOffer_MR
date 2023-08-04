package co.com.bancolombia.utilities.model.gateways;

import co.com.bancolombia.utilities.model.customer.RequestCustomer;
import co.com.bancolombia.utilities.model.customer.ResponseCustomerContact;
import reactor.core.publisher.Mono;

public interface CustomerContactGateway {
    Mono<ResponseCustomerContact> retrieveCustomerContact(RequestCustomer request, String msgId);
}