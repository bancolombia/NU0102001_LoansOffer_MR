package co.com.bancolombia.utilities.usecase.utils;

import co.com.bancolombia.utilities.model.exceptions.AmortizationBusinessException;
import co.com.bancolombia.utilities.model.installments.FeeConcept;
import co.com.bancolombia.utilities.model.installments.RegularFeeConcept;
import co.com.bancolombia.utilities.model.interestrate.RangeType;
import co.com.bancolombia.utilities.model.utils.ExceptionEnum;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class AmortizationUtils {

    public RangeType getRangeType(List<RangeType> rangeTypes, BigDecimal amount, Integer term)
            throws AmortizationBusinessException {
        return rangeTypes.stream()
                .filter(object -> amount.compareTo(BigDecimal.valueOf(object.getMinimumAmount())) >= 0)
                .filter(object -> amount.compareTo(BigDecimal.valueOf(object.getMaximumAmount())) <= 0)
                .filter(object -> term.compareTo(object.getMinimumTerm()) >= 0)
                .filter(object -> term.compareTo(object.getMinimumTerm()) <= 0)
                .findAny()
                .orElseThrow(() -> new AmortizationBusinessException(ExceptionEnum.LI013.name(),
                        ExceptionEnum.LI013.name(), ExceptionEnum.LI013.getMessage(), "", "", ""));
    }

    public FeeConcept getFeeConcept(List<FeeConcept> feeConcepts, String filter) throws AmortizationBusinessException {
        return feeConcepts.stream()
                .filter(fee -> fee.getType().equalsIgnoreCase(filter))
                .findAny()
                .orElseThrow(() -> new AmortizationBusinessException(ExceptionEnum.LI013.name(),
                        ExceptionEnum.LI007.name(), ExceptionEnum.LI007.getMessage(), "", "", ""));
    }

    public RegularFeeConcept getRegularFeeConcept(List<RegularFeeConcept> feeConcepts, String filter)
            throws AmortizationBusinessException {
        return feeConcepts.stream()
                .filter(fee -> fee.getType().equalsIgnoreCase(filter))
                .findAny()
                .orElseThrow(() -> new AmortizationBusinessException(ExceptionEnum.LI013.name(),
                        ExceptionEnum.LI007.name(), ExceptionEnum.LI007.getMessage(), "", "", ""));
    }
}
