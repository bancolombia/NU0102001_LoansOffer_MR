package co.com.bancolombia.libreinversion.model.customer.gateways;

import co.com.bancolombia.libreinversion.model.customer.rest.RequestBodyData;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerBasic;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerDetail;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerContact;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerCommercial;

import reactor.core.publisher.Mono;

public interface CustomerDataGateways {

    Mono<ResponseCustomerBasic> callPersonalData(RequestBodyData requestBodyData,
                                                 String msgId, Long time);

    Mono<ResponseCustomerDetail> callDetailInformation(RequestBodyData rqBodyData,
                                                       String msgId, Long time);

    Mono<ResponseCustomerContact> callContactInformation(RequestBodyData rqBodyData,
                                                         String msgId, Long time);

    Mono<ResponseCustomerCommercial> callCommercialData(RequestBodyData rqBodyData, String msgId);
}
