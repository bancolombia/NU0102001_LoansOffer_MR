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
public class Offer {
    private String productId;
    private BigDecimal amount;
    private Integer term;
    private String interestRateType;
    private Integer gracePeriod;
    private Integer interestPaymentFrequency;
    private Integer capitalPaymentFrequency;
    private InsurancesRequest insurances;
}
