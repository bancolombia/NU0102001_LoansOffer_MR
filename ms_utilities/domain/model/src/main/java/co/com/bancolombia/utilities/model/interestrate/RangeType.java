package co.com.bancolombia.utilities.model.interestrate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RangeType {
    private String rateType;
    private Double minimumAmount;
    private Double maximumAmount;
    private Integer minimumTerm;
    private Integer maximumTerm;
    private Double monthlyRate;
    private Double annualNominalMonthlyRate;
    private Double effectiveAnnualRate;
    private Double arrearsRate;
    private Double fixedTermDepositRate;
    private Double fixedTermDepositScore;
}