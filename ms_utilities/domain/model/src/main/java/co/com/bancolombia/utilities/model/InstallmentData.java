package co.com.bancolombia.utilities.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class InstallmentData {
    private BigDecimal installment;
    private Integer paymentDay;
    private BigDecimal interestRate;
    private BigDecimal monthOverdueInterestRate;
    private BigDecimal arreasInterestRate;
    private BigDecimal effectiveAnnualInterestRate;
    private BigDecimal nominalAnnualInterestRate;
    private String interestRateType;
    private BigDecimal variableInterestRateAdditionalPoints;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;
    private InsurancesResponse insurances;
    private BigDecimal availabilityHandlingFee;
    private Fng fng;
    private AmortizationSchedule amortizationSchedule;
}
