package co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DataRespDM {
    private HeaderRespDM header;
    private String title;
    private String loanId;
    private Integer loanAmount;
    private Integer disbursementAmount;
    private Integer installments;
    private String interestRateType;
    private String loanOriginDate;
    private String loanDueDate;
    private String nextPaymentDate;
    private Integer paymentDay;
}
