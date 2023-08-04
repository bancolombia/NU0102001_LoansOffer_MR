package co.com.bancolombia.utilities.model.installments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Insurance {
    private String type;
    private BigDecimal rate;
    private BigDecimal additionalRate;
    private String calculationMethod;
    private String calculationBase;
    private BigDecimal fixedAmount;
}