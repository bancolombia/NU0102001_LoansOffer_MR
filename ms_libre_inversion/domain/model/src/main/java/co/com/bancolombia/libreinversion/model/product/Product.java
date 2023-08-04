package co.com.bancolombia.libreinversion.model.product;

import co.com.bancolombia.libreinversion.model.commons.TextLegalInformation;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(required = true)
    private String id;

    @Schema(required = true)
    private String name;

    @Schema(required = true)
    private BigDecimal totalAmount;

    @Schema(required = true)
    private String minAmount;

    @Schema(required = true)
    private String expirationDate;

    @Schema(required = true)
    private String customerReliability;

    @Schema(required = true)
    private Integer paymentDay;

    private Integer gracePeriod;

    @Schema(required = true)
    private List<InterestRate> interestRates;

    @Schema(required = true)
    private List<Range> ranges;

    @Schema(required = true)
    private List<Insurance> insurances;
    private List<TextLegalInformation> textLegalInformation;
}
