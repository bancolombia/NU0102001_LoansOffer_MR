package co.com.bancolombia.libreinversion.model.stoc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class MortgageConditions {

    private BigDecimal expiredMonthFee;
    private BigDecimal annualEffectiveFee;
    private BigDecimal leasingFee;
    private BigDecimal annualEffectiveUvr;
    private BigDecimal expiredMonthUvr;
    private BigDecimal depositAfterAdjustedSpending;
}
