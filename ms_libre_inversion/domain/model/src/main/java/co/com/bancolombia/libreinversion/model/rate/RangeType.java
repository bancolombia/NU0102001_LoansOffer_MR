package co.com.bancolombia.libreinversion.model.rate;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder(toBuilder = true)
public class RangeType {

    private String rateType;
    private BigDecimal minimumAmount;
    private BigDecimal maximumAmount;
    private Integer minimumTerm;
    private Integer maximumTerm;
    private BigDecimal monthlyRate;
    private BigDecimal annualNominalMonthlyRate;
    private BigDecimal effectiveAnnualRate;
    private BigDecimal arrearsRate;
    private BigDecimal fixedTermDepositRate;
    private BigDecimal fixedTermDepositScore;

}
