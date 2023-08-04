package co.com.bancolombia.libreinversion.usecase.customer;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.commons.ErrorEnum;
import co.com.bancolombia.libreinversion.model.customer.CustomerData;
import co.com.bancolombia.libreinversion.model.customer.CustomerFactory;
import co.com.bancolombia.libreinversion.model.customer.gateways.CustomerDataGateways;
import co.com.bancolombia.libreinversion.model.customer.rest.RequestBodyData;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerCommercial;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerContact;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerDetail;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.libreinversion.model.notification.gateways.NotificationGateways;
import co.com.bancolombia.libreinversion.model.notification.rest.ResponseRetrieveInfo;
import co.com.bancolombia.libreinversion.model.notification.rest.RetrieveInformationRQ;
import co.com.bancolombia.libreinversion.model.product.Attribute;
import co.com.bancolombia.libreinversion.model.product.City;
import co.com.bancolombia.libreinversion.model.product.RuleRequest;
import co.com.bancolombia.libreinversion.model.product.RuleResponse;
import co.com.bancolombia.libreinversion.model.product.gateways.MAPGateways;
import co.com.bancolombia.libreinversion.model.request.ConfirmFactory;
import co.com.bancolombia.libreinversion.model.request.RetrieveOfferRQ;
import co.com.bancolombia.libreinversion.model.request.gateways.RedisGateways;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class CustomerUseCase {

    private final CustomerDataGateways customerGateways;
    private final NotificationGateways notificationGateways;
    private final MAPGateways mapGateways;
    private final RedisGateways redisGateways;

    public Mono<CustomerData> getPersonalData(RetrieveOfferRQ request, String msgId) {
        return validateIdType(request.getCustomer().getIdentification().getType(),
                request.getCustomer().getIdentification().getNumber(),
                CustomerFactory.getProduct(request), msgId)
                .flatMap(data -> getCustomerData(data, CustomerFactory.getProduct(request), msgId));
    }

    private Mono<CustomerData> getCustomerData(CustomerData data, String productId, String msgId) {
        RequestBodyData rqCustomerData = CustomerFactory.getRqCustomer(data.getDocumentType(), data.getDocumentId());
        RetrieveInformationRQ rqNotificationData = CustomerFactory
                .getRqAlert(data.getDocumentType(), data.getDocumentId());

        return mapGateways.getTimeOffer().flatMap(rule -> Mono.just(rule.getData()))
                .flatMap(param -> {
                    Long time = Long.parseLong(param.getUtilLoad().get(Constant.OFFER_TIME_PARAM)
                            .toString()) * Constant.HOUR_TODAY;
                    return Mono.zip(

                    )
                            .flatMap(tupp -> CustomerFactory.validateAlertIndicators(tupp, msgId))
                            .flatMap(tupple -> setDataCache(time, tupple,
                                    rqCustomerData.getData().getCustomerDocumentId()))
                            .flatMap(tuple -> validateBuildCustomer(tuple, productId, data, msgId));
                });
    }

    private Mono<CustomerData> validateBuildCustomer(Tuple4<ResponseCustomerCommercial,
            ResponseCustomerContact, ResponseCustomerDetail, ResponseRetrieveInfo> tuple,
                                                     String productId, CustomerData data,
                                                     String msgId) {
        return validaDate(tuple, msgId, productId)
                .flatMap(customer -> validateSegment(tuple.getT1().getData().getSegment(), productId, msgId)
                        .flatMap(res -> CustomerFactory.getAge(tuple.getT3().getData().getBirthDate()))
                        .flatMap(age -> validateAgeRule(age, productId, msgId))
                        .flatMap(age -> validateCity(CustomerFactory.getCityCode(tuple.getT2()), msgId))
                        .flatMap(res -> CustomerFactory.validateContactData(tuple.getT2(), tuple.getT4(), msgId))
                        .flatMap(customerData ->
                            redisGateways.getData(Constant.APLICATION_STATUS + "" + data.getDocumentId())
                        ).flatMap(applicationStatus -> {
                            if (applicationStatus.toString().equals(Constant.ENABLED)
                                || applicationStatus.toString().equals(Constant.SELL)
                                    || applicationStatus.toString().equals(Constant.CONFIRM)) {
                                return Mono.just(applicationStatus.toString());
                            }
                            return Mono.just(Constant.RETRIEVE);
                        })
                        .flatMap(status ->
                            Mono.just(data.toBuilder().names(CustomerFactory.getNames(tuple.getT3()))
                                    .applicationStatus(status).build())
                        ));
    }

    private Mono<CustomerData> validateIdType(String idType, String idNumber, String productId, String msgId) {
        List<Attribute> attr = new ArrayList<>(
                Arrays.asList(Attribute.builder().name("").value(idType).build()));
        return runRule(attr, "", productId)
                .filter(r -> r.getData().isValid())
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(new LibreInversionException(ErrorEnum.MSG_LI006.getName(),
                                ErrorEnum.MSG_LI006.getName(), ErrorEnum.MSG_LI006.getMessage(), "", "", msgId))))
                .thenReturn(CustomerData.builder()
                        .build());
    }

    private Mono<RuleResponse> validateSegment(String segment, String productId, String msgId) {
        List<Attribute> attr = new ArrayList<>(
                Arrays.asList(Attribute.builder().name("").value(segment).build()));
        return runRule(attr, "", productId)
                .filter(r -> r.getData().isValid())
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(new LibreInversionException(ErrorEnum.MSG_LI008.getName(),
                                ErrorEnum.MSG_LI008.getName(), ErrorEnum.MSG_LI008.getMessage(), "", "", msgId))))
                .flatMap(Mono::just);
    }

    private Mono<Integer> validateAgeRule(Integer age, String productId, String msgId) {
        List<Attribute> attr = new ArrayList<>(
                Arrays.asList(Attribute.builder().name("").value(age.toString()).build()));

        return runRule(attr, "", productId)
                .filter(r -> r.getData().isValid())
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(new LibreInversionException(ErrorEnum.MSG_LI007.getName(),
                                ErrorEnum.MSG_LI007.getName(), ErrorEnum.MSG_LI007.getMessage(), "", "", msgId))))
                .thenReturn(age);
    }

    private Mono<List<City>> validateCity(String cityCode, String msgId) {
        return mapGateways.getAllowedCities().flatMap(cities ->
            Flux.fromIterable(cities)
                    .filter(c -> c.getCode().equals(cityCode))
                    .switchIfEmpty(Mono.defer(() ->
                            Mono.error(new LibreInversionException(ErrorEnum.MSG_LI009.getName(),
                                    ErrorEnum.MSG_LI009.getName(), ErrorEnum.MSG_LI009.getMessage(), "", "", msgId))))
                    .flatMap(Mono::just).collectList()
        );
    }

    private Mono<RuleResponse> runRule(List<Attribute> attr, String ruleName, String productId) {
        return mapGateways.ruleValidate(RuleRequest
                .builder().attributes(attr)
                .productCode(productId).ruleName(ruleName).build());
    }

    private Mono<Object> validaDate(Tuple4<ResponseCustomerCommercial,
            ResponseCustomerContact, ResponseCustomerDetail, ResponseRetrieveInfo> tuple,
                                    String msgId, String productId) {

        return Mono.just(tuple).flatMap(tup ->
            Mono.zip(
                    validaEnrollmentDate(tup.getT4(), msgId, productId),
                    validaDateLastUpdate(tup.getT2(), msgId, productId)
            )
        );
    }

    private Mono<RuleResponse> validaEnrollmentDate(ResponseRetrieveInfo responseRetrieveInfo,
                                                    String msgId, String productId) {

        return Mono.just(responseRetrieveInfo).flatMap(respo -> {
            String diferentInDay = ConfirmFactory.getDateDiferentInDay(
                    responseRetrieveInfo.getData().getEnrollmentDate());

            List<Attribute> attr = new ArrayList<>(
                    Arrays.asList(Attribute.builder()
                            .name(Constant.CONDITION_ENROLLMENT_DATE).value(diferentInDay).build()));

            return runRule(attr, Constant.RULE_EROLLMENT_DATE, productId);
        }).filter(ruleResponse -> ruleResponse.getData().isValid())
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(new LibreInversionException(ErrorEnum.MSG_LI018.getName(),
                                ErrorEnum.MSG_LI018.getName(), ErrorEnum.MSG_LI018.getMessage(), "", "", msgId))));
    }

    private Mono<RuleResponse> validaDateLastUpdate(ResponseCustomerContact responseCustomerContact,
                                                    String msgId, String productId) {

        return Mono.just(responseCustomerContact).flatMap(respo -> {
            String diferentInDay = ConfirmFactory.getDateDiferentInDay(respo.getData()
                    .getContact().get(0).getDateLastUpdate());

            List<Attribute> attr = new ArrayList<>(
                    Arrays.asList(Attribute.builder()
                            .name(Constant.CONDITION_DATE_LAST_UPDATE).value(diferentInDay).build()));

            return runRule(attr, Constant.RULE_DATE_LAST_UPDATE, productId);
        }).filter(ruleResponse -> ruleResponse.getData().isValid())
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(new LibreInversionException(ErrorEnum.MSG_LI019.getName(),
                                ErrorEnum.MSG_LI019.getName(), ErrorEnum.MSG_LI019.getMessage(), "", "", msgId))));
    }

    private Mono<Tuple4<ResponseCustomerCommercial,
            ResponseCustomerContact,
            ResponseCustomerDetail,
            ResponseRetrieveInfo>> setDataCache(Long time,
                                                      Tuple4<ResponseCustomerCommercial,
                                                              ResponseCustomerContact,
                                                              ResponseCustomerDetail,
                                                              ResponseRetrieveInfo> tuple,
                                      String number) {
        return redisGateways.setData(Constant.CUSTOMERCOMMERCIALDATA_ID + number,
                tuple.getT1(), time)
                .flatMap(coun -> Mono.just(tuple));
    }
}
