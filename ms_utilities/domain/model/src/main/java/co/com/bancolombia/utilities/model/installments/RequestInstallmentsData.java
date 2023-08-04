package co.com.bancolombia.utilities.model.installments;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RequestInstallmentsData {
    private String rediscountType;
    private String loanId;
    private String creditPlan;
    private BigDecimal amount;
    private String currency;
    private Integer term;
    private Integer paymentFrequency;
    private BigDecimal basisPoints;
    private BigDecimal nominalInterestRate;
    private List<Insurance> insurances;
}
