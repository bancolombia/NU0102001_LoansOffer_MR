package co.com.bancolombia.libreinversion.usecase.notification;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.commons.IdTypeEnum;
import co.com.bancolombia.libreinversion.model.notification.gateways.NotificationGateways;
import co.com.bancolombia.libreinversion.model.notification.rest.CustomerIdentification;
import co.com.bancolombia.libreinversion.model.notification.rest.CustomerIdentificationRQ;
import co.com.bancolombia.libreinversion.model.notification.rest.ResponseRetrieveInfoData;
import co.com.bancolombia.libreinversion.model.notification.rest.RetrieveInformationRQ;
import co.com.bancolombia.libreinversion.model.product.gateways.MAPGateways;
import co.com.bancolombia.libreinversion.model.request.RetrieveOfferRQ;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class NotificationUseCase {

    private final NotificationGateways notificationGateways;
    private final MAPGateways mapGateways;

    public Mono<ResponseRetrieveInfoData> retrieveInformation(RetrieveOfferRQ request, String msgId) {

        return mapGateways.getTimeOffer().flatMap(rule -> Mono.just(rule.getData()))
                .flatMap(param -> {
                    Long time = Long.parseLong(param.getUtilLoad().get(Constant.OFFER_TIME_PARAM)
                            .toString()) * Constant.HOUR_TODAY;
                    return buildRequest(request)
                            .flatMap(rq -> notificationGateways.callRetrieveInformation(rq, msgId, time))
                            .flatMap(res -> Mono.just(res.getData()));
                });
    }

    private Mono<RetrieveInformationRQ> buildRequest(RetrieveOfferRQ request) {
        CustomerIdentification ci = CustomerIdentification.builder()
                .documentNumber(request.getCustomer().getIdentification().getNumber())
                .documentType(IdTypeEnum.valueOfType(request.getCustomer().getIdentification().getType()).name())
                .build();
        return Mono.just(RetrieveInformationRQ.builder()
                .data(CustomerIdentificationRQ.builder()
                        .customerIdentification(ci).build()).build());
    }

}
