package co.com.bancolombia.utilities.usecase;

import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import co.com.bancolombia.utilities.model.Identification;
import co.com.bancolombia.utilities.model.InstallmentData;
import co.com.bancolombia.utilities.model.InstallmentsData;
import co.com.bancolombia.utilities.model.InsuranceRequest;
import co.com.bancolombia.utilities.model.InsuranceResponse;
import co.com.bancolombia.utilities.model.InsurancesResponse;
import co.com.bancolombia.utilities.model.Offer;
import co.com.bancolombia.utilities.model.RequestAmortization;
import co.com.bancolombia.utilities.model.ResponseAmortization;
import co.com.bancolombia.utilities.model.ResponseAmortizationData;
import co.com.bancolombia.utilities.model.customer.Contact;
import co.com.bancolombia.utilities.model.customer.RequestCustomer;
import co.com.bancolombia.utilities.model.customer.RequestCustomerData;
import co.com.bancolombia.utilities.model.customer.ResponseCustomerCommercial;
import co.com.bancolombia.utilities.model.customer.ResponseCustomerContact;
import co.com.bancolombia.utilities.model.customer.ResponseCustomerDetails;
import co.com.bancolombia.utilities.model.customer.ResponseCustomerDetailsData;
import co.com.bancolombia.utilities.model.exceptions.AmortizationBusinessException;
import co.com.bancolombia.utilities.model.gateways.CustomerCommercialGateway;
import co.com.bancolombia.utilities.model.gateways.CustomerContactGateway;
import co.com.bancolombia.utilities.model.gateways.CustomerDetailsGateway;
import co.com.bancolombia.utilities.model.gateways.InstallmentsGateway;
import co.com.bancolombia.utilities.model.gateways.InterestRateGateway;
import co.com.bancolombia.utilities.model.gateways.MapGateway;
import co.com.bancolombia.utilities.model.installments.FeeConcept;
import co.com.bancolombia.utilities.model.installments.Insurance;
import co.com.bancolombia.utilities.model.installments.RegularFeeConcept;
import co.com.bancolombia.utilities.model.installments.RequestInstallments;
import co.com.bancolombia.utilities.model.installments.RequestInstallmentsData;
import co.com.bancolombia.utilities.model.installments.ResponseInstallments;
import co.com.bancolombia.utilities.model.interestrate.FixedRateLoans;
import co.com.bancolombia.utilities.model.interestrate.RangeType;
import co.com.bancolombia.utilities.model.interestrate.RequestInterestRate;
import co.com.bancolombia.utilities.model.interestrate.RequestInterestRateData;
import co.com.bancolombia.utilities.model.interestrate.ResponseInterestRate;
import co.com.bancolombia.utilities.model.interestrate.VariableRateLoans;
import co.com.bancolombia.utilities.model.product.RuleResponse;
import co.com.bancolombia.utilities.model.utils.Constant;
import co.com.bancolombia.utilities.model.utils.ExceptionEnum;
import co.com.bancolombia.utilities.model.utils.SegmentEnum;
import co.com.bancolombia.utilities.usecase.utils.AmortizationUtils;
import co.com.bancolombia.utilities.usecase.utils.MapValidations;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuple4;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class AmortizationUseCase {

    private final CustomerContactGateway customerContactGateway;
    private final CustomerCommercialGateway customerCommercialGateway;
    private final CustomerDetailsGateway customerDetailsGateway;
    private final InstallmentsGateway installmentsGateway;
    private final InterestRateGateway interestRateGateway;
    private final MapGateway mapGateway;

    private static final TechLogger logger = LoggerFactory.getLog("AmortizationUseCase");

    public Mono<ResponseAmortization> retrieveAmortization(
            RequestAmortization requestBody, String msgId) {
        return retrieveCustomerData(requestBody, msgId)
                .flatMap(customerData -> retrieveMapResponse(customerData, requestBody)
                        .flatMap(ruleResponse -> {
                            String segment = customerData.getT3().getData().getSegment();
                            List<InsuranceRequest> insurances = requestBody
                                    .getData().getOffer().getInsurances().getInsurance();
                            InsuranceRequest insuranceSD = InsuranceRequest
                                    .builder().type(Constant.INSURANCE_SD).build();

                            if (insurances.contains(insuranceSD) && ruleResponse.getData().isValid()) {
                                return retrieveResponsesUnemployment(requestBody, segment, msgId)
                                        .flatMap(responses -> buildResponseUnemployment(requestBody, responses));
                            } else {
                                return retrieveResponsesBasic(requestBody, segment, msgId)
                                        .flatMap(responses -> buildResponseBasic(requestBody, responses));
                            }
                        }));
    }

    private Mono<ResponseAmortization> buildResponseBasic(
            RequestAmortization requestBody,
            Tuple2<ResponseInterestRate, ResponseInstallments> objects) {
        Offer offer = requestBody.getData().getOffer();

        ResponseInterestRate responseInterestRateP59 = objects.getT1();
        ResponseInstallments responseInstallmentsP59 = objects.getT2();
        List<RangeType> rangeTypesP59 = responseInterestRateP59.getData().getRateRange().get(0).getRangeType();
        RangeType rangeTypeP59;
        BigDecimal installmentP59;
        List<InsuranceResponse> insurancesP59 = new ArrayList<>();

        AmortizationUtils amortizationUtils = new AmortizationUtils();

        try {
            rangeTypeP59 = amortizationUtils.getRangeType(rangeTypesP59, offer.getAmount(), offer.getTerm());
            FeeConcept feeConceptP59 = amortizationUtils
                    .getFeeConcept(responseInstallmentsP59.getData().getFeeConcepts(), Constant.TOTAL_CUOTA);
            installmentP59 = feeConceptP59.getAmount();

            RegularFeeConcept feeConceptSVP59 = amortizationUtils
                    .getRegularFeeConcept(responseInstallmentsP59.getData().getRegularFeeConcepts(), Constant.TYPE_SV);
            insurancesP59.add(InsuranceResponse.builder()
                    .type(Constant.INSURANCE_SV)
                    .amount(feeConceptSVP59.getAmount())
                    .build());
        } catch (AmortizationBusinessException e) {
            rangeTypeP59 = RangeType.builder().build();
            installmentP59 = BigDecimal.ZERO;
            insurancesP59 = null;
            logger.info(e.getMessage());
        }

        List<InstallmentData> installmentData = new ArrayList<>();
        installmentData.add(InstallmentData.builder()
                .installment(installmentP59)
                .monthOverdueInterestRate(BigDecimal.valueOf(rangeTypeP59.getMonthlyRate()))
                .arreasInterestRate(BigDecimal.valueOf(rangeTypeP59.getArrearsRate()))
                .effectiveAnnualInterestRate(BigDecimal.valueOf(rangeTypeP59.getEffectiveAnnualRate()))
                .nominalAnnualInterestRate(BigDecimal.valueOf(rangeTypeP59.getAnnualNominalMonthlyRate()))
                .interestRateType(rangeTypeP59.getRateType())
                .variableInterestRateAdditionalPoints(BigDecimal
                        .valueOf(rangeTypeP59.getFixedTermDepositRate()))
                .insurances(InsurancesResponse.builder().insurance(insurancesP59).build())
                .build());

        return Mono.just(ResponseAmortization.builder()
                .data(ResponseAmortizationData.builder()
                        .customer(requestBody.getData().getCustomer())
                        .installmentsData(InstallmentsData.builder().installmentData(installmentData).build())
                        .build())
                .build());
    }

    private Mono<ResponseAmortization> buildResponseUnemployment(
            RequestAmortization requestBody,
            Tuple4<ResponseInterestRate, ResponseInterestRate, ResponseInstallments, ResponseInstallments> objects) {
        Offer offer = requestBody.getData().getOffer();

        ResponseInterestRate responseInterestRateP59 = objects.getT1();
        ResponseInstallments responseInstallmentsP59 = objects.getT3();
        List<RangeType> rangeTypesP59 = responseInterestRateP59.getData().getRateRange().get(0).getRangeType();
        RangeType rangeTypeP59;
        BigDecimal installmentP59;
        List<InsuranceResponse> insurancesP59 = new ArrayList<>();

        ResponseInterestRate responseInterestRatePA5 = objects.getT2();
        ResponseInstallments responseInstallmentsPA5 = objects.getT4();
        List<RangeType> rangeTypesPA5 = responseInterestRatePA5.getData().getRateRange().get(0).getRangeType();
        RangeType rangeTypePA5;
        BigDecimal installmentPA5;
        List<InsuranceResponse> insurancesPA5 = new ArrayList<>();

        AmortizationUtils amortizationUtils = new AmortizationUtils();

        try {
            rangeTypeP59 = amortizationUtils.getRangeType(rangeTypesP59, offer.getAmount(), offer.getTerm());
            FeeConcept feeConceptP59 = amortizationUtils
                    .getFeeConcept(responseInstallmentsP59.getData().getFeeConcepts(), Constant.TOTAL_CUOTA);
            installmentP59 = feeConceptP59.getAmount();

            RegularFeeConcept feeConceptSVP59 = amortizationUtils
                    .getRegularFeeConcept(responseInstallmentsP59.getData().getRegularFeeConcepts(), Constant.TYPE_SV);
            insurancesP59.add(InsuranceResponse.builder()
                    .type(Constant.INSURANCE_SV)
                    .amount(feeConceptSVP59.getAmount())
                    .build());

            rangeTypePA5 = amortizationUtils.getRangeType(rangeTypesPA5, offer.getAmount(), offer.getTerm());
            FeeConcept feeConceptPA5 = amortizationUtils
                    .getFeeConcept(responseInstallmentsPA5.getData().getFeeConcepts(), Constant.TOTAL_CUOTA);
            installmentPA5 = feeConceptPA5.getAmount();

            RegularFeeConcept feeConceptSVPA5 = amortizationUtils
                    .getRegularFeeConcept(responseInstallmentsPA5.getData().getRegularFeeConcepts(), Constant.TYPE_SV);
            insurancesPA5.add(InsuranceResponse.builder()
                    .type(Constant.INSURANCE_SV)
                    .amount(feeConceptSVPA5.getAmount())
                    .build());

            RegularFeeConcept feeConceptSDPA5 = amortizationUtils
                    .getRegularFeeConcept(responseInstallmentsPA5.getData().getRegularFeeConcepts(), Constant.TYPE_SD);
            insurancesPA5.add(InsuranceResponse.builder()
                    .type(Constant.INSURANCE_SD)
                    .amount(feeConceptSDPA5.getAmount())
                    .build());

        } catch (AmortizationBusinessException e) {
            rangeTypeP59 = RangeType.builder().build();
            rangeTypePA5 = RangeType.builder().build();
            installmentP59 = BigDecimal.ZERO;
            installmentPA5 = BigDecimal.ZERO;
            insurancesP59 = null;
            insurancesPA5 = null;
            logger.info(e.getMessage());
        }

        List<InstallmentData> installmentData = new ArrayList<>();
        installmentData.add(InstallmentData.builder()
                .installment(installmentP59)
                .monthOverdueInterestRate(BigDecimal.valueOf(rangeTypeP59.getMonthlyRate()))
                .arreasInterestRate(BigDecimal.valueOf(rangeTypeP59.getArrearsRate()))
                .effectiveAnnualInterestRate(BigDecimal.valueOf(rangeTypeP59.getEffectiveAnnualRate()))
                .nominalAnnualInterestRate(BigDecimal.valueOf(rangeTypeP59.getAnnualNominalMonthlyRate()))
                .interestRateType(rangeTypeP59.getRateType())
                .variableInterestRateAdditionalPoints(BigDecimal
                        .valueOf(rangeTypeP59.getFixedTermDepositRate()))
                .insurances(InsurancesResponse.builder().insurance(insurancesP59).build())
                .build());

        installmentData.add(InstallmentData.builder()
                .installment(installmentPA5)
                .monthOverdueInterestRate(BigDecimal.valueOf(rangeTypePA5.getMonthlyRate()))
                .arreasInterestRate(BigDecimal.valueOf(rangeTypePA5.getArrearsRate()))
                .effectiveAnnualInterestRate(BigDecimal.valueOf(rangeTypePA5.getEffectiveAnnualRate()))
                .nominalAnnualInterestRate(BigDecimal.valueOf(rangeTypePA5.getAnnualNominalMonthlyRate()))
                .interestRateType(rangeTypePA5.getRateType())
                .variableInterestRateAdditionalPoints(BigDecimal
                        .valueOf(rangeTypePA5.getFixedTermDepositRate()))
                .insurances(InsurancesResponse.builder().insurance(insurancesPA5).build())
                .build());

        return Mono.just(ResponseAmortization.builder()
                .data(ResponseAmortizationData.builder()
                        .customer(requestBody.getData().getCustomer())
                        .installmentsData(InstallmentsData.builder().installmentData(installmentData).build())
                        .build())
                .build());
    }

    private Mono<Tuple2<ResponseInterestRate, ResponseInstallments>> retrieveResponsesBasic(
            RequestAmortization requestAmortization, String segment, String msgId) {
        return Mono.zip(retrieveInterestRate(requestAmortization, segment, Constant.PLAN_P59, msgId),
                retrieveInstallments(requestAmortization, Constant.PLAN_P59, msgId));
    }

    private Mono<Tuple4<ResponseInterestRate, ResponseInterestRate, ResponseInstallments, ResponseInstallments>>
    retrieveResponsesUnemployment(RequestAmortization requestAmortization, String segment, String msgId) {
        return Mono.zip(retrieveInterestRate(requestAmortization, segment, Constant.PLAN_P59, msgId),
                retrieveInterestRate(requestAmortization, segment, Constant.PLAN_PA5, msgId),
                retrieveInstallments(requestAmortization, Constant.PLAN_P59, msgId),
                retrieveInstallments(requestAmortization, Constant.PLAN_PA5, msgId));
    }

    private Mono<ResponseInterestRate> retrieveInterestRate(
            RequestAmortization requestAmortization, String segment, String plan, String msgId) {
        List<FixedRateLoans> fixedRateLoans = new ArrayList<>();
        fixedRateLoans.add(FixedRateLoans.builder().fixedRateLoanId(plan).build());

        List<VariableRateLoans> variableRateLoans = new ArrayList<>();
        variableRateLoans.add(VariableRateLoans.builder().variableRateLoanType(plan).build());

        RequestInterestRate requestInterestRate = RequestInterestRate.builder()
                .data(RequestInterestRateData.builder()
                        .customerSegment(SegmentEnum.valueOf(segment).getMessage())
                        .customerRelaibility(requestAmortization.getData().getCustomer().getCustomerReliability())
                        .fixedRateLoans(fixedRateLoans)
                        .variableRateLoans(variableRateLoans)
                        .amount(requestAmortization.getData().getOffer().getAmount())
                        .loanTerm(requestAmortization.getData().getOffer().getTerm())
                        .build())
                .build();

        return interestRateGateway.retrieveInterestRate(requestInterestRate, msgId);
    }

    private Mono<ResponseInstallments> retrieveInstallments(
            RequestAmortization requestAmortization, String plan, String msgId) {

        BigDecimal amount = requestAmortization.getData().getOffer().getAmount();

        List<Insurance> insurances = new ArrayList<>();
        insurances.add(Insurance.builder()
                .type(Constant.INSURANCE_SV)
                .rate(getRate(amount))
                .build());

        final int NEW_SCALE = 2;
        if (plan.equalsIgnoreCase(Constant.PLAN_PA5)) {
            BigDecimal fixedAmount = amount.multiply(Constant.RATE_SD).setScale(NEW_SCALE, RoundingMode.HALF_EVEN);

            insurances.add(Insurance.builder()
                    .type(Constant.INSURANCE_SD)
                    .fixedAmount(fixedAmount)
                    .build());
        }

        RequestInstallments requestInstallments = RequestInstallments.builder()
                .data(RequestInstallmentsData.builder()
                        .creditPlan(plan)
                        .amount(requestAmortization.getData().getOffer().getAmount())
                        .term(requestAmortization.getData().getOffer().getTerm())
                        .currency(Constant.CURRENCY_COP)
                        .insurances(insurances)
                        .build())
                .build();

        return installmentsGateway.retrieveInstallments(requestInstallments, msgId);
    }

    private Mono<RuleResponse> retrieveMapResponse(
            Tuple3<ResponseCustomerDetails, ResponseCustomerContact, ResponseCustomerCommercial> customerData,
            RequestAmortization requestBody) {
        ResponseCustomerDetailsData detailsData = customerData.getT1().getData();
        Contact contactData = customerData.getT2().getData().getContact().get(0);

        String age = String.valueOf(calculateAge(detailsData.getBirthDate(), LocalDate.now()));
        String occupation = detailsData.getOccupation();
        boolean phone = !Constant.BLANK.equalsIgnoreCase(contactData.getMobilPhone());
        boolean email = !Constant.BLANK.equalsIgnoreCase(contactData.getEmail());

        MapValidations validations = new MapValidations(mapGateway);
        return validations.validateRuleRuleOccupationSDE(age, occupation, phone, email)
                .filter(ruleResponse -> {
                    String plan = ruleResponse.getData().isValid() ? Constant.PLAN_PA5 : Constant.PLAN_P59;
                    Integer term = requestBody.getData().getOffer().getTerm();

                    boolean res;

                    if (plan.equals(Constant.PLAN_PA5) && (term.compareTo(Constant.MIN_TERM_PA5) < 0
                            || term.compareTo(Constant.MAX_TERM_PA5) > 0)) {
                        res = true;
                    } else res = plan.equals(Constant.PLAN_P59) && (term.compareTo(Constant.MIN_TERM_P59) < 0
                            || term.compareTo(Constant.MAX_TERM_P59) > 0);

                    return validations.validateRuleResponse(res, ExceptionEnum.LI019.name(),
                            ExceptionEnum.LI019.getMessage());
                });
    }

    private Mono<Tuple3<ResponseCustomerDetails, ResponseCustomerContact, ResponseCustomerCommercial>>
    retrieveCustomerData(RequestAmortization request, String msgId) {
        Identification identification = request.getData().getCustomer().getIdentification();
        RequestCustomer requestCustomer = RequestCustomer.builder()
                .data(RequestCustomerData.builder()
                        .customerDocumentId(identification.getNumber())
                        .customerDocumentType(identification.getType())
                        .queryType("")
                        .build())
                .build();
        return Mono.zip(customerDetailsGateway.retrieveCustomerDetails(requestCustomer, msgId),
                customerContactGateway.retrieveCustomerContact(requestCustomer, msgId),
                customerCommercialGateway.retrieveCustomerCommercial(requestCustomer, msgId));
    }

    private BigDecimal getRate(BigDecimal amount) {

        BigDecimal rate = BigDecimal.ZERO;

        if (amount.compareTo(Constant.MIN_01) >= 0 && amount.compareTo(Constant.MAX_01) <= 0) {
            rate = Constant.RATE_SV_1;
        } else if (amount.compareTo(Constant.MIN_02) >= 0 && amount.compareTo(Constant.MAX_02) <= 0) {
            rate = Constant.RATE_SV_2;
        } else if (amount.compareTo(Constant.MIN_03) >= 0 && amount.compareTo(Constant.MAX_03) <= 0) {
            rate = Constant.RATE_SV_3;
        } else if (amount.compareTo(Constant.MIN_04) >= 0 && amount.compareTo(Constant.MAX_04) <= 0) {
            rate = Constant.RATE_SV_4;
        } else if (amount.compareTo(Constant.MIN_05) >= 0 && amount.compareTo(Constant.MAX_05) <= 0) {
            rate = Constant.RATE_SV_5;
        }

        return rate;
    }

    private int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthDate, currentDate).getYears();
        } else {
            return 0;
        }
    }
}