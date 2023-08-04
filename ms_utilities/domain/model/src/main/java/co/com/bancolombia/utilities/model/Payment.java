package co.com.bancolombia.utilities.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    private Integer installmentNumber;
    private BigDecimal installment;
    private BigDecimal interestPayment;
    private BigDecimal capitalPayment;
    private BigDecimal annualFngCommission;
    private InsurancesResponse insurances;
}
