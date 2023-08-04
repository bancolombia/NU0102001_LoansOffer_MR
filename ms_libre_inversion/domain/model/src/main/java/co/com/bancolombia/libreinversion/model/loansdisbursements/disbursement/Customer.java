package co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement;

import co.com.bancolombia.libreinversion.model.account.rest.Identification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Customer {
    private Identification identification;
}
