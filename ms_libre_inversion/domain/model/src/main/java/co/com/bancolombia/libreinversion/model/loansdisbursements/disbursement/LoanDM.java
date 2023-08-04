package co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoanDM {
    private Customer customer;
    private String creditLimitId;
    private String creditPlan;
    private Double amount;
    private Integer term;
    private Double interestRate;
    private Double basicPoints;
    private Integer paymentFrequency;
    private Integer paymentDay;
    private String branchOffice;
    private String backedByCollateral;
    private String collateralId;
    private String approverOfficer;
    private String referredOfficer;
    private String combo;
    private String agreementId;
    private DirectDebit directDebit;
    private List<InsuranceDM> insurances;
}
