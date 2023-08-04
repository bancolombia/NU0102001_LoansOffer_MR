package co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DisbursementDestinationDbm {
    private List<DestinationDbm> destination;
}
