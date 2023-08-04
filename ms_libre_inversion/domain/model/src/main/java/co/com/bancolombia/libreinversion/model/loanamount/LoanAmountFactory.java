package co.com.bancolombia.libreinversion.model.loanamount;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.commons.ErrorEnum;
import co.com.bancolombia.libreinversion.model.commons.IdTypeEnum;
import co.com.bancolombia.libreinversion.model.constants.SellConst;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.libreinversion.model.product.Attribute;
import co.com.bancolombia.libreinversion.model.request.EnableOfferRQ;
import co.com.bancolombia.libreinversion.model.stoc.GeneralInformation;
import co.com.bancolombia.libreinversion.model.stoc.OpportunitiesRQDataArgs;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class LoanAmountFactory {

    public static RequestLoanAmount getCustomer(EnableOfferRQ rq) {

        return RequestLoanAmount.builder()
                .data(RequestLoanAmountData.builder()
                        .customer(Customer.builder()
                                .customerIdNumber(rq.getCustomer()
                                        .getIdentification()
                                        .getNumber())
                                .customerId(rq.getCustomer()
                                        .getIdentification().getType().split("_")[1])
                                .build())
                        .build())
                .build();
    }

    public static OpportunitiesRQDataArgs buildRequest(EnableOfferRQ rq, Integer channelCode) {
        return OpportunitiesRQDataArgs.builder()
                .contactChannelCode(channelCode)
                .documentType(IdTypeEnum.valueOfType(rq.getCustomer().getIdentification().getType()).getStocType())
                .documentNumber(Integer.valueOf(rq.getCustomer().getIdentification().getNumber()))
                .build();
    }

    public static List<Attribute> getAttrRuleProduct(GeneralInformation data) {
        return Arrays.asList(Attribute.builder().name("productCode")
                .value(data.getProductCode().toString()).build());
    }

    public static List<Attribute> groupRisk(GeneralInformation data) {
        return Arrays.asList(Attribute.builder().name(Constant.GROUP_RISK)
                .value(data.getRiskGroupName()).build());
    }

    public static List<Attribute> oppFinish() {
        return Arrays.asList(Attribute.builder().name(SellConst.MAP_OPPORTUNITI_PERSONMANAGEPARAM)
                .value(SellConst.MAP_OPPORTUNITI_PERSONMANAGEPARAM).build());
    }

    public static List<Attribute> attrLine(String producId) {
        return Collections.singletonList(Attribute.builder().name("LineaCredito")
                .value(producId).build());
    }

    public static Mono<Quota> getLimit(SuccessResponseLoanAmount limit,
                                       GeneralInformation offer, Map<String, Object> utilLoad, String msgId) {
        List<Quota> collect = limit.getData().getQuota().stream().filter(quo ->
                quo.getCopApprovedAmount().equals(BigDecimal.valueOf(offer.getPreapprovedDetail().getCapacity()))
        && mapValueToList(utilLoad).contains(quo.getLineOfCreditCode()))
                .collect(Collectors.toList());

        if (collect.isEmpty()) {
            return Mono.error(new LibreInversionException(ErrorEnum.MSG_LI027.getName(),
                    ErrorEnum.MSG_LI027.getName(), ErrorEnum.MSG_LI027.getMessage(), "", "", msgId));
        }
        return Mono.just(collect.get(0));
    }

    private static List<String> mapValueToList(Map<String, Object> utilLoad) {
        return utilLoad.values().stream()
                .map(object -> Objects.toString(object, null))
                .collect(Collectors.toList());
    }
}
