package co.com.bancolombia.libreinversion.model.stoc;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.commons.ErrorEnum;
import co.com.bancolombia.libreinversion.model.commons.TextLegalInformation;
import co.com.bancolombia.libreinversion.model.customer.CustomerFactory;
import co.com.bancolombia.libreinversion.model.customer.ValidateCustomer;

import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.libreinversion.model.product.ResponseData;
import co.com.bancolombia.libreinversion.model.product.RuleResponse;
import co.com.bancolombia.libreinversion.model.product.InterestRate;
import co.com.bancolombia.libreinversion.model.product.Insurance;
import co.com.bancolombia.libreinversion.model.product.Range;
import co.com.bancolombia.libreinversion.model.product.Attribute;
import co.com.bancolombia.libreinversion.model.product.Product;

import co.com.bancolombia.libreinversion.model.rate.FixedRateLoans;
import co.com.bancolombia.libreinversion.model.rate.RangeType;
import co.com.bancolombia.libreinversion.model.rate.RateRange;
import co.com.bancolombia.libreinversion.model.rate.VariableRateLoans;
import co.com.bancolombia.libreinversion.model.request.EnableOfferRQ;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class StocFactory {

    public static List<FixedRateLoans> fixedRateLoans(ResponseData responseData, ValidateCustomer data) {
        List<FixedRateLoans> fixedRateLoansList = new ArrayList<>();

        if (data.getInsurance() != null) {
            fixedRateLoansList.add(FixedRateLoans.builder()
                    .fixedRateLoanId(responseData.getUtilLoad().get(Constant.PLANSD).toString()).build());
        }

        fixedRateLoansList.add(FixedRateLoans.builder()
                .fixedRateLoanId(responseData.getUtilLoad().get(Constant.PLANSV).toString()).build());

        return fixedRateLoansList;
    }

    public static List<VariableRateLoans> variableRateLoans(String plan) {
        List<VariableRateLoans> variableRateLoansList = new ArrayList<>();
        variableRateLoansList.add(VariableRateLoans.builder().variableRateLoanType(plan).build());
        return variableRateLoansList;
    }

    public static String getFirstValue(RuleResponse ruleResponse) {
        Object firstKey = ruleResponse.getData().getUtilLoad().keySet().toArray()[0];
        Object valueForFirstKey = ruleResponse.getData().getUtilLoad().get(firstKey);
        return valueForFirstKey.toString();
    }

    public static String getSeconValue(RuleResponse ruleResponse) {
        Object secondKey = ruleResponse.getData().getUtilLoad().keySet().toArray()[1];
        Object valueForFirstKey = ruleResponse.getData().getUtilLoad().get(secondKey);
        return valueForFirstKey.toString();
    }

    public static List<InterestRate> getInterestRates(ValidateCustomer data) {
        return new ArrayList<>(Arrays.asList(
                InterestRate.builder().type(data.getInterestRateType()).build()
        ));
    }

    public static List<Insurance> getInsurances(ValidateCustomer data) {

        List<Insurance> insurances = new ArrayList<>();

        insurances.add(Insurance.builder()
                .minAmount(new BigDecimal(data.getMinAmount()))
                .maxAmount(new BigDecimal(data.getMaxAmount()))
                .type(data.getInsuranceType())
                .factor(new BigDecimal(data.getFactor()))
                .build());

        if (data.getInsurance() != null) {
            insurances.add(Insurance.builder()
                    .minAmount(new BigDecimal("0.0"))
                    .maxAmount(new BigDecimal("0.0"))
                    .type(data.getInsurance().getType())
                    .factor(data.getInsurance().getFactor())
                    .build());
        }

        return insurances;
    }


    public static Mono<List<Range>> getRateList(List<RateRange> list, ValidateCustomer data) {
        return Mono.just(list.get(0))
                .flatMap(rates -> Mono.just(rates.getRangeType()))
                .flatMap(rates -> getRangeType(rates, data));
    }

    private static Mono<List<Range>> getRangeType(List<RangeType> ranges, ValidateCustomer data) {

        List<Range> rangesList = new ArrayList<>();

        return Mono.just(ranges).flatMap(ra -> {
            ranges.forEach(rate -> {
                Range ran = Range.builder()
                        .minAmount(rate.getMinimumAmount())
                        .maxAmount(rate.getMaximumAmount())
                        .minTerm(rate.getMaximumTerm())
                        .maxTerm(rate.getMinimumTerm())
                        .build();
                if (data.getStoc().getPreapprovedDetail().getCapacity() >= rate.getMinimumAmount().intValue()
                        && data.getStoc().getPreapprovedDetail().getCapacity() <= rate.getMaximumAmount().intValue()) {
                    rangesList.add(ran);
                }
            });
            return Mono.just(rangesList);
        });
    }

    public static boolean between(int i, int minValueInclusive, int maxValueInclusive) {
        return (i >= minValueInclusive && i <= maxValueInclusive);
    }

    public static String getFirstData(RuleResponse data) {
        return data.getData().getUtilLoad().entrySet().iterator().next().getValue().toString();
    }

    public static String getSecondData(RuleResponse data) {
        int couunt = data.getData().getUtilLoad().keySet().toArray().length;
        Object lasttKey = data.getData().getUtilLoad().keySet().toArray()[couunt - 1];
        Object valueForFirstKey = data.getData().getUtilLoad().get(lasttKey);
        return valueForFirstKey.toString();
    }

    public static Attribute getAttr(String name, String value) {
        return Attribute.builder().name(name).value(value).build();
    }

    public static Mono<ValidateCustomer> getInsuranceType(Tuple2<RuleResponse,
            RuleResponse> tuple, ValidateCustomer data) {

        if (tuple.getT1().getData().isValid()) {
            data.setInsuranceType(tuple.getT1().getData()
                    .getUtilLoad().get(Constant.TIPO_SEGURO_SV).toString());
        }
        if (tuple.getT2().getData().isValid()) {
            BigDecimal factor = new BigDecimal(tuple.getT2().getData()
                    .getUtilLoad().get(Constant.FACTOR_SEGURO_DESEMPLEO).toString());
            data.setInsurance(Insurance.builder()
                    .type(tuple.getT2().getData().getUtilLoad().get(Constant.SEGURO_EMPLEADO).toString())
                    .factor(factor).build());
        }
        return Mono.just(data);
    }

    public static String getInterestRateType(List<Product> products) {
        Optional<Product> op = products.stream().findFirst();

        if (op.isPresent()) {
            Optional<InterestRate> op2 = op.get().getInterestRates().stream().findFirst();
            if (op2.isPresent()) {
                return op2.get().getType();
            }
        }

        return "edfsdfdf";
    }

    public static String getProductId(EnableOfferRQ rq) {
        Optional<Product> op2 = rq.getProducts().stream().findFirst();
        String productId = "";
        if (op2.isPresent()) {
            productId = op2.get().getId();
        }
        return productId;
    }

    public static List<Attribute> getAttrOccupation(ValidateCustomer data) {
        String cellPhoneExists = CustomerFactory.existsCellphone(data.getAlerts(), data.getContact());
        String emailExists = CustomerFactory.existsEmail(data.getAlerts(), data.getContact());
        return new ArrayList<>(Arrays.asList(
        ));
    }

    public static List<Attribute> getAttrOccupationSV(ValidateCustomer data) {
        String cellPhoneExists = CustomerFactory.existsCellphone(data.getAlerts(), data.getContact());
        String emailExists = CustomerFactory.existsEmail(data.getAlerts(), data.getContact());
        return new ArrayList<>(Arrays.asList(
        ));
    }

    public static List<TextLegalInformation> getText(Object copies) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(copies, new TypeReference<List<TextLegalInformation>>() {});
    }

    public static Mono<GeneralInformation> existOffer(GeneralInformation offer, String msgId) {

        if(offer.getOpportunityId() == null) {
            return Mono.error(new LibreInversionException(ErrorEnum.MSG_BI003.getName(),
                    ErrorEnum.MSG_BI003.getName(), ErrorEnum.MSG_BI003.getMessage(), "", "", msgId));
        }
        return Mono.just(offer);
    }
}
