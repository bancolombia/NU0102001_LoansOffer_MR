package co.com.bancolombia.libreinversion.model.rate;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LoanInteresRate {

    private String customerSegment;
    private String customerRelaibility;
    private List<FixedRateLoans> fixedRateLoans;
    private List<VariableRateLoans> variableRateLoans;
    private BigDecimal amount;
    private BigDecimal loanTerm;
}
