package co.com.bancolombia.libreinversion.model.stoc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class PayrollDeductionConditions {

    private String agreementCode;
    private String alternativeMinimumTerm;
    private String alternativeMaximumTerm;

}
