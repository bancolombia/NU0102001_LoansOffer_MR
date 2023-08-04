package co.com.bancolombia.utilities.model.product;

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
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal factor;
}
