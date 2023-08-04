package co.com.bancolombia.libreinversion.model.loanamount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class SuccessResponseLoanAmount {
    private Meta meta;
    private SuccessResponseLoanAmountData data;
    private TopLevelLinks links;
}