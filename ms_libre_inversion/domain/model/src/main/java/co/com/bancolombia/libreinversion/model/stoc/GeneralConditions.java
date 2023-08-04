package co.com.bancolombia.libreinversion.model.stoc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class GeneralConditions {

    private String creditCardMaximumCapacity;
    private String creditCardBenefit;
    private String overdraftMaximunCapacity;
    private int freeInvestmentMaximunCapacity;
    private String repaymentLoanNumber;

}
