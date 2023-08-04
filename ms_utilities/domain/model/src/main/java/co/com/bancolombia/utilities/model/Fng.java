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
public class Fng {
    private BigDecimal commission;
    private BigDecimal annualCommission;
    private BigDecimal vat;
    private BigDecimal coverage;
}
