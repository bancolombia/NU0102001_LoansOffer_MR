package co.com.bancolombia.libreinversion.model.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Range {

    @Schema(required = true)
    private BigDecimal minAmount;

    @Schema(required = true)
    private BigDecimal maxAmount;

    @Schema(required = true)
    private Integer minTerm;

    @Schema(required = true)
    private Integer maxTerm;

}
