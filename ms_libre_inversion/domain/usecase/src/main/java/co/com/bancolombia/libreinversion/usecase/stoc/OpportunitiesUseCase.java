package co.com.bancolombia.libreinversion.usecase.stoc;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.commons.ErrorEnum;
import co.com.bancolombia.libreinversion.model.commons.IdTypeEnum;
import co.com.bancolombia.libreinversion.model.constants.SellConst;
import co.com.bancolombia.libreinversion.model.customer.CustomerFactory;
import co.com.bancolombia.libreinversion.model.customer.ValidateCustomer;
import co.com.bancolombia.libreinversion.model.customer.gateways.CustomerDataGateways;
import co.com.bancolombia.libreinversion.model.customer.rest.RequestBodyData;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerDetail;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.libreinversion.model.loanamount.LoanAmountFactory;
import co.com.bancolombia.libreinversion.model.loanamount.Quota;
import co.com.bancolombia.libreinversion.model.loanamount.SuccessResponseLoanAmount;
import co.com.bancolombia.libreinversion.model.loanamount.gateways.LoanAmountGateway;
import co.com.bancolombia.libreinversion.model.notification.gateways.NotificationGateways;
import co.com.bancolombia.libreinversion.model.notification.rest.RetrieveInformationRQ;
import co.com.bancolombia.libreinversion.model.offer.EnableOfferData;
import co.com.bancolombia.libreinversion.model.product.gateways.MAPGateways;

import co.com.bancolombia.libreinversion.model.rate.LoanInteresRateResponse;
import co.com.bancolombia.libreinversion.model.rate.LoanInteresRateRQ;
import co.com.bancolombia.libreinversion.model.rate.LoanInteresRate;

import co.com.bancolombia.libreinversion.model.rate.gateways.InterestRateAdapterGateways;
import co.com.bancolombia.libreinversion.model.request.EnableOfferRQ;
import co.com.bancolombia.libreinversion.model.request.gateways.RedisGateways;
import co.com.bancolombia.libreinversion.model.stoc.GeneralInfoPersonManage;
import co.com.bancolombia.libreinversion.model.stoc.GeneralInformation;
import co.com.bancolombia.libreinversion.model.stoc.OpportunitiesRQDataArgs;
import co.com.bancolombia.libreinversion.model.stoc.StocFactory;
import co.com.bancolombia.libreinversion.model.stoc.PersonManagementRQ;
import co.com.bancolombia.libreinversion.model.stoc.PersonManagementInfo;
import co.com.bancolombia.libreinversion.model.stoc.gateways.OpportunitiesGateways;

import co.com.bancolombia.libreinversion.model.product.Attribute;
import co.com.bancolombia.libreinversion.model.product.Product;
import co.com.bancolombia.libreinversion.model.product.RuleRequest;
import co.com.bancolombia.libreinversion.model.product.RuleResponse;
import co.com.bancolombia.libreinversion.model.product.ResponseData;


import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class OpportunitiesUseCase {

    private final MAPGateways mapGateways;
    private final OpportunitiesGateways opportunitiesGateway;
    private final CustomerDataGateways customerGateways;
    private final NotificationGateways notificationGateways;
    private final InterestRateAdapterGateways interestRateAdapterGateways;
    private final RedisGateways redisGateways;
    private final LoanAmountGateway loanAmountGateway;


    public Mono<EnableOfferData> getBusinessOpportunities(EnableOfferRQ rq, String msgId) {

        return mapGateways.getTimeOffer().flatMap(rule -> Mono.just(rule.getData()))
                .flatMap(param -> {
                    Long time = Long.parseLong(param.getUtilLoad()
                            .get(Constant.OFFER_TIME_PARAM).toString()) * Constant.HOUR_TODAY;
                    Integer chCode = Integer.parseInt(param.getUtilLoad()
                            .get(Constant.CHANNEL_CODE_PARAM).toString());
                    return opportunitiesGateway.getBusinessOportunities(LoanAmountFactory.buildRequest(rq, chCode), msgId)
                            .flatMap(offer -> StocFactory.existOffer(offer, msgId))
                            .flatMap(offer -> getLimit(offer, rq, msgId, time))
                            .flatMap(data -> validateGroupRisk(data, msgId))
                            .flatMap(opor -> setDataCache(time, opor, rq.getCustomer().getIdentification().getNumber()))
                            .flatMap(data -> validateProduct(rq, data, msgId))
                            .flatMap(this::getCustomerData)
                            .flatMap(customerUserData -> {
                                redisGateways.setData(
                                        Constant.APLICATION_STATUS + "" +
                                                rq.getCustomer().getIdentification().getNumber(),
                                        Constant.ENABLED, time
                                ).subscribe();
                                return Mono.just(customerUserData);
                            })
                            .flatMap(customerUserData -> {
                                redisGateways.setData(Constant.RESPONSE_ENABLE + "" +
                                                rq.getCustomer().getIdentification().getNumber(),
                                        customerUserData, time
                                ).subscribe();
                                return Mono.just(customerUserData);
                            });
                });
    }

    private Mono<GeneralInformation> getLimit(GeneralInformation offer, EnableOfferRQ rq, String msgId, Long time) {
        return loanAmountGateway.getLoanAmount(LoanAmountFactory.getCustomer(rq), msgId)
                .flatMap(limit -> validateLineCredit(limit, offer, msgId, StocFactory.getProductId(rq), time));
    }

    private Mono<GeneralInformation> validateLineCredit(SuccessResponseLoanAmount limit,
                                                        GeneralInformation offer, String msgId, String
                                                                porductId, Long time) {
        return runRule(LoanAmountFactory.attrLine(porductId), "dsfdsfdfsf", porductId)
                .flatMap(r -> Mono.just(offer));
    }

    private Mono<ValidateCustomer> validateProduct(EnableOfferRQ rq, GeneralInformation data, String msgId) {
        return runRule(LoanAmountFactory.getAttrRuleProduct(data),
                "ddsfds", StocFactory.getProductId(rq))
                .filter(r -> r.getData().isValid())
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(new LibreInversionException(ErrorEnum.MSG_LI011.getName(),
                                ErrorEnum.MSG_LI011.getName(), ErrorEnum.MSG_LI011.getMessage(), "", "", msgId))))
                .flatMap(r -> Mono.just(ValidateCustomer.builder()
                        .rateTypes(StocFactory.getFirstData(r))
                        .stoc(data)
                        .productCode(data.getProductCode())
                        .customer(rq.getCustomer())
                        .msgId(msgId)
                        .interestRateType(StocFactory.getInterestRateType(rq.getProducts()))
                        .gracePeriod(data.getPreapprovedDetail().getMaximumTerm())
                        .build())
                );
    }

    private Mono<EnableOfferData> getCustomerData(ValidateCustomer data) {

        RequestBodyData rqCustomerData = CustomerFactory
                .getRqCustomer(data.getCustomer().getIdentification().getType(),
                        data.getCustomer().getIdentification().getNumber());
        RetrieveInformationRQ rqNotificationData = CustomerFactory
                .getRqAlert(data.getCustomer().getIdentification().getType(),
                        data.getCustomer().getIdentification().getNumber());

        return mapGateways.getTimeOffer().flatMap(rule -> Mono.just(rule.getData()))
                .flatMap(param -> {
                    Long time = 40L;

                    return Mono.zip(
                                    Mono.just(ResponseCustomerDetail.builder().build()),
                                    Mono.just(ResponseCustomerDetail.builder().build()),
                                    Mono.just(ResponseCustomerDetail.builder().build())
                            ).switchIfEmpty(Mono.defer(() ->
                                    Mono.error(new LibreInversionException(ErrorEnum.MSG_LI005.getName(),
                                            ErrorEnum.MSG_LI005.getName(),
                                            ErrorEnum.MSG_LI005.getMessage(), "", "", data.getMsgId()))))
                            .flatMap(tuple -> validateBuildCustomer(
                                    data.toBuilder().detail(tuple.getT1()).contact(null)
                                            .alerts(null).cacheTime(time).build()
                            ))
                            .flatMap(this::getRateRange)
                            .flatMap(vc -> Mono.just(EnableOfferData.builder()
                                    .customer(data.getCustomer()).products(getProducts(vc, param)).build()));
                });
    }

    private Mono<ValidateCustomer> validateBuildCustomer(ValidateCustomer data) {

        return Mono.just(data);
    }

    private Mono<ValidateCustomer> validateAmounts(ValidateCustomer data) {

        return mapGateways.getAmounts()
                .flatMap(amounts -> Flux.fromIterable(amounts)
                        .filter(amount -> StocFactory.between(data.getStoc().getPreapprovedDetail().getCapacity(),
                                amount.getMinAmount(), amount.getMaxAmount()))
                        .switchIfEmpty(Mono.defer(() ->
                                Mono.error(new LibreInversionException(ErrorEnum.MSG_LI014.getName(),
                                        ErrorEnum.MSG_LI014.getName(),
                                        ErrorEnum.MSG_LI014.getMessage(), "", "", data.getMsgId()))))
                        .single()
                        .flatMap(amount -> Mono.just(data.toBuilder()
                                .minAmount(amount.getMinAmount())
                                .maxAmount(amount.getMaxAmount())
                                .factor(amount.getFactor())
                                .build())));
    }

    private Mono<ValidateCustomer> validateOccupation(ValidateCustomer data) {
        List<Attribute> attr = StocFactory.getAttrOccupation(data);
        List<Attribute> attrSV = StocFactory.getAttrOccupationSV(data);
        return Mono.just(data);
    }

    private List<Product> getProducts(ValidateCustomer data, ResponseData responseData) {
        return new ArrayList<>(Arrays.asList(Product.builder()
                .build()));
    }

    private Mono<ValidateCustomer> getRateRange(ValidateCustomer data) {
        return getDateInteresRate(data).flatMap(tasas -> Mono.just(tasas.getData()))
                .flatMap(tasas -> Mono.just(tasas.getRateRange()))
                .flatMap(da -> Mono.just(data));
    }

    private Mono<RuleResponse> runRule(List<Attribute> attr, String ruleName, String productId) {
        return mapGateways.ruleValidate(RuleRequest
                .builder().attributes(attr).productCode(productId).ruleName(ruleName).build());
    }

    private Mono<LoanInteresRateResponse> getDateInteresRate(ValidateCustomer data) {

        LoanInteresRateRQ.LoanInteresRateRQBuilder loanInteresRateRQ = LoanInteresRateRQ.builder();
        LoanInteresRate.LoanInteresRateBuilder loanInteresRate = LoanInteresRate.builder();
        RequestBodyData rqCustomerData = CustomerFactory.getRqCustomer(data.getCustomer().getIdentification().getType(),
                data.getCustomer().getIdentification().getNumber());

       return Mono.just(LoanInteresRateResponse.builder().build());
    }

    private Mono<GeneralInformation> setDataCache(Long time, GeneralInformation general, String number) {
        return redisGateways.setData(Constant.BUSINESOPORTUNITI_ID + number, general, time)
                .flatMap(coun -> Mono.just(general));
    }

    public Mono<String> changeOpportunitiesToFinished(GeneralInformation generalInformation, String msgId) {
        return Mono.just(msgId);
    }

    private Mono<LoanInteresRateResponse> setDataCacheRate(Long time, LoanInteresRateResponse data, String number) {
        return redisGateways.setData(Constant.INTERES_RATES + number, data, time)
                .flatMap(coun -> Mono.just(data));
    }

    private Mono<GeneralInformation> validateGroupRisk(GeneralInformation data, String msgId) {
        return runRule(LoanAmountFactory.groupRisk(data), Constant.GROUP_RISK, data.getProductCode().toString())
                .filter(rule -> rule.getData().isValid())
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(new LibreInversionException(ErrorEnum.MSG_GP005.getName(),
                                ErrorEnum.MSG_GP005.getName(), ErrorEnum.MSG_GP005.getMessage(), "", "", msgId))))
                .flatMap(rule -> Mono.just(data));
    }
}
