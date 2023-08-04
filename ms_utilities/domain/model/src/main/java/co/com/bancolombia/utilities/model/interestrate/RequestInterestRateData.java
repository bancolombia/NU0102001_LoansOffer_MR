package co.com.bancolombia.utilities.model.interestrate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RequestInterestRateData {
    private List<FixedRateLoans> fixedRateLoans;
    private List<VariableRateLoans> variableRateLoans;
    private String customerSegment;
    private String customerRelaibility;
    private BigDecimal amount;
    private int loanTerm;
}