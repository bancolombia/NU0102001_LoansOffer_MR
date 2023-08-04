package co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DisbursementRQ {

    private DisburmentsData data;

}
