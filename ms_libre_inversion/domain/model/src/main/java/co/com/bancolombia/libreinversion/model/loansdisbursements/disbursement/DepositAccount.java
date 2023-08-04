package co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DepositAccount {

    @Schema(required = true)
    private String type;

    @Schema(required = true)
    private String number;
}
