package co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class InsuranceDM {
    private String type;
    private Double rate;
}
