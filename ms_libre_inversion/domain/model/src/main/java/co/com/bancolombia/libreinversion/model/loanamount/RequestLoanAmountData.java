package co.com.bancolombia.libreinversion.model.loanamount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RequestLoanAmountData {
    private Customer customer;
}

