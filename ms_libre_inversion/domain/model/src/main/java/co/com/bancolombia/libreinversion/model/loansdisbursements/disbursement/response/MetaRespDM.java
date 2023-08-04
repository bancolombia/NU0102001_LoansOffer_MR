package co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class MetaRespDM {
    private String _messageId;
    private String _requestDateTime;
    private String _applicationId;
}
