package co.com.bancolombia.utilities.model.product;

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
public class Product {
    private String id;
    private String name;
    private BigDecimal totalAmount;
    private Integer paymentDay;
    private Integer gracePeriod;
    private List<InterestRate> interestRates;
    private List<Range> ranges;
    private List<Insurance> insurances;
    private List<Text> texts;
}
