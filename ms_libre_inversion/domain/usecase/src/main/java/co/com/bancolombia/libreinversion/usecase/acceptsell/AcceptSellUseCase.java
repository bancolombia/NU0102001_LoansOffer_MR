package co.com.bancolombia.libreinversion.usecase.acceptsell;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.commons.ErrorEnum;
import co.com.bancolombia.libreinversion.model.constants.SellConst;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.libreinversion.model.offer.Loan;
import co.com.bancolombia.libreinversion.model.offer.sellutil.CacheSell;
import co.com.bancolombia.libreinversion.model.offer.sellutil.UtilSell;
import co.com.bancolombia.libreinversion.model.product.Attribute;
import co.com.bancolombia.libreinversion.model.product.ProductFactory;
import co.com.bancolombia.libreinversion.model.product.ResponseData;
import co.com.bancolombia.libreinversion.model.product.RuleRequest;
import co.com.bancolombia.libreinversion.model.product.RuleResponse;
import co.com.bancolombia.libreinversion.model.product.gateways.MAPGateways;
import co.com.bancolombia.libreinversion.model.rate.LoanInteresRateResponse;
import co.com.bancolombia.libreinversion.model.rate.RangeType;
import co.com.bancolombia.libreinversion.model.rate.RateRange;
import co.com.bancolombia.libreinversion.model.request.ConfirmOfferComplete;
import co.com.bancolombia.libreinversion.model.request.HtmlDocumentFactory;
import co.com.bancolombia.libreinversion.model.request.SellOfferRQ;
import co.com.bancolombia.libreinversion.model.request.gateways.RedisGateways;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AcceptSellUseCase {

    private final MAPGateways mapGateways;
    private final RedisGateways redisGateways;
    private static final TechLogger log = LoggerFactory.getLog(AcceptSellUseCase.class.getName());


    public Mono<RuleResponse> validAvailabilityApp(SellOfferRQ sellOfferRQ) {
        return Mono.just(sellOfferRQ).flatMap(sellOffer ->
                Mono.just(RuleResponse.builder().code("").message("").success(1)
                        .data(ResponseData.builder().valid(true).build()).build()));
    }

    public Mono<SellOfferRQ> queryRuleParamPgc(SellOfferRQ sellOffer) {
        RuleRequest rule = RuleRequest.builder().ruleName(SellConst.MAP_PCG_PARAMETER)
                .productCode(SellConst.MAP_PRODUCT_CODE)
                .attributes(Arrays.asList(Attribute.builder().name(SellConst.MAP_PCG_PARAMETER)
                        .value(SellConst.MAP_PCG_PARAMETER).build())).build();

        if (sellOffer.getLoans() != null) {
            return mapGateways.ruleValidate(rule).flatMap(ruleResponse -> Mono.just(sellOffer)
                    .map(sell -> sell.getLoans().stream().map(loan -> {
                        if (loan.getAdvisoryCode() == null || loan.getAdvisoryCode().trim().isEmpty()) {
                            loan.setAdvisoryCode(ruleResponse.getData().getUtilLoad()
                                    .get(SellConst.MAP_CUSTOMER_ADVISORY_CODE).toString());
                        }
                        if (loan.getBranchOffice() == null || loan.getBranchOffice().trim().isEmpty()) {
                            loan.setBranchOffice(ruleResponse.getData().getUtilLoad()
                                    .get(SellConst.MAP_CUSTOMER_BRANCH_CODE).toString());
                        }
                        return loan;
                    }).collect(Collectors.toList()))).flatMap(loans -> {
                sellOffer.setLoans(loans);
                return Mono.just(sellOffer);
            }).onErrorResume(e -> Mono.error(UtilSell.errorProcess("queryRuleParamPgc", "", e)));
        } else {
            return Mono.error(new LibreInversionException(Constant.LOANS_REQ_NOT_FOUND, Constant.ERROR_LI001,
                    Constant.LOANS_REQ_NOT_FOUND, SellConst.SELL_OFFER, SellConst.SELL_OFFER, ""));
        }
    }

    public Mono<RuleResponse> queryRuleParamDocumentManage() {
        RuleRequest rule = RuleRequest.builder().ruleName(SellConst.MAP_DOC_MANAGER_PARAM)
                .productCode(SellConst.MAP_PRODUCT_CODE).attributes(Arrays.asList(Attribute.builder()
                        .name(SellConst.MAP_DOC_MANAGER_PARAM).value(SellConst.MAP_DOC_MANAGER_PARAM)
                        .build())).build();

        return mapGateways.ruleValidate(rule);
    }

    public Mono<Long> getTimeOffer() {
        return mapGateways.getTimeOffer().flatMap(rule -> Mono.just(rule.getData()))
                .flatMap(param -> {
                    Long time = Long.parseLong(ProductFactory.getfirstValue(param)) * Constant.HOUR_TODAY;
                    return Mono.just(time);
                });
    }

    public Mono<RangeType> getInteresRateCache(ConfirmOfferComplete confirmOfferCompl, SellOfferRQ sellOfferPgc) {
        final String id = Constant.INTERES_RATES + sellOfferPgc.getCustomer().getIdentification().getNumber();
        return redisGateways.getData(id).flatMap(obj -> {
            Integer amount = 0;
            RangeType res = null;
            LoanInteresRateResponse loanRate = redisGateways.castObject(obj, LoanInteresRateResponse.class);
            if (loanRate != null) {
                RateRange ranges = loanRate.getData().getRateRange().get(0);
                for (RangeType rate : ranges.getRangeType()) {
                    log.error(rate);
                    amount = new BigDecimal(confirmOfferCompl.getConfirmOfferRQ()
                            .getOffer().getAmount()).intValue();
                    if (rate.getMinimumAmount().intValue() <= amount &&
                            rate.getMaximumAmount().intValue() >= amount) {
                        res = rate;
                        break;
                    }
                }
            } else {
                return Mono.error(UtilSell.errorInteresRate(Constant.RATES_DATA_NOT_FOUND, ""));
            }
            if (res == null) {
                return Mono.error(UtilSell.errorInteresRate(Constant.RATE_RANGE_NOT_FOUND + amount, ""));
            }
            return Mono.just(res);
        }).onErrorResume(e -> Mono.error(UtilSell.errorProcess("getInteresRateCache", "", e)));

    }

    public Mono<SellOfferRQ> setCaheQAndOperation(SellOfferRQ sellOfferRQ, Long time) {
        final String key_status = Constant.APLICATION_STATUS + "" +
                sellOfferRQ.getCustomer().getIdentification().getNumber();
        final String key_req = Constant.REQUEST_SELL + "" + sellOfferRQ.getCustomer().getIdentification().getNumber();
        return redisGateways.setData(key_status, Constant.SELL, time)
                .flatMap(sellRQ -> redisGateways.setData(key_req, sellOfferRQ, time)
                        .flatMap(obj -> Mono.just((SellOfferRQ) obj)));
    }

    public Mono<Boolean> updateCacheSell(CacheSell cache, SellOfferRQ sellOfferRQ, Long time) {
        String keyStatus = SellConst.CACHE_SELL_OFFER + sellOfferRQ.getCustomer().getIdentification().getNumber();
        return redisGateways.setData(keyStatus, cache, time)
                .flatMap(sellRQ -> Mono.just(true));
    }

    public Mono<CacheSell> loadCacheSell(SellOfferRQ sellOfferRQ) {
        String keyStatus = SellConst.CACHE_SELL_OFFER + sellOfferRQ.getCustomer().getIdentification().getNumber();
        return redisGateways.getData(keyStatus)
                .flatMap(obj -> {
                    CacheSell cache = redisGateways.castObject(obj, CacheSell.class);
                    if (cache == null) {
                        return Mono.just(CacheSell.builder().build());
                    } else {
                        return Mono.just(cache);
                    }
                });
    }

    public void clearDataCustomer(String customerIdNumber) {

    }

    public Mono<Long> getTimeUrlPreSigned(String msgId) {

        RuleRequest rule = RuleRequest.builder().ruleName(SellConst.MAP_DOC_MANAGER_PARAM)
                .productCode(SellConst.MAP_PRODUCT_CODE).ruleName(Constant.RULE_FREE_INVESTMENT_DOC)
                .attributes(Arrays.asList(Attribute.builder()
                        .name(Constant.OFFER_TIME_PARAM_NAME).value(Constant.RULE_FREE_INVESTMENT_VALUE)
                        .build())).build();
        return mapGateways.ruleValidate(rule).filter(param -> param.getData().isValid())
                .flatMap(ruleRs -> {
                    Long minute = Long.parseLong(ruleRs.getData().getUtilLoad()
                            .get(Constant.TIME_EXP_DOC_MAP_RULE).toString());
                    return Mono.just(minute);
                }).switchIfEmpty(Mono.defer(() ->
                        Mono.error(new LibreInversionException(ErrorEnum.MSG_VP006.getName(),
                                ErrorEnum.MSG_VP006.getName(), ErrorEnum.MSG_VP006.getMessage(), "", "", msgId))));

    }

    public Mono<String> getloanType(ConfirmOfferComplete confirmOfferCompl, SellOfferRQ sellOfferPgc) {
        final String id = Constant.INTERES_RATES + sellOfferPgc.getCustomer().getIdentification().getNumber();
        return redisGateways.getData(id).flatMap(obj -> {
            String loanType = null;
            LoanInteresRateResponse loanRate = redisGateways.castObject(obj, LoanInteresRateResponse.class);
            if (loanRate != null) {
                RateRange ranges = loanRate.getData().getRateRange().get(0);
                loanType = ranges.getLoanType();
            } else {
                return Mono.error(UtilSell.errorInteresRate(Constant.RATES_DATA_NOT_FOUND, ""));
            }
            if (loanType == null) {
                return Mono.error(UtilSell.errorInteresRate(Constant.LOAN_TYPE_NOT_FOUND, ""));
            }
            return Mono.just(loanType);
        }).onErrorResume(e -> Mono.error(UtilSell.errorProcess("getInteresRateCache", "", e)));

    }

    public Mono<Boolean> validAmmounts(ConfirmOfferComplete confirCompl, SellOfferRQ sellOffer, String msgId) {
        return HtmlDocumentFactory.validateAmmount(confirCompl.getConfirmOfferRQ(), msgId)
                .flatMap(confirmOfferRQ -> {
                    Double ammount = Double.valueOf(confirmOfferRQ.getOffer().getAmount());
                    for (Loan loan : sellOffer.getLoans()) {
                        boolean cumple = UtilSell.validateAmmount(loan.getDisbursementDestination(), ammount);
                        if (!cumple) {
                            return Mono.error(new LibreInversionException(ErrorEnum.MSG_LI023.getName(),
                                    ErrorEnum.MSG_LI023.getName(), ErrorEnum.MSG_LI023.getMessage(), "", "", msgId));
                        }
                    }
                    return Mono.just(Boolean.TRUE);
                });
    }
}