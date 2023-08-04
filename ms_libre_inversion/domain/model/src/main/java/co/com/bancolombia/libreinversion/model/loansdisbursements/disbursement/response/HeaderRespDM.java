package co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class HeaderRespDM {
    private String type;
    private String id;
}
