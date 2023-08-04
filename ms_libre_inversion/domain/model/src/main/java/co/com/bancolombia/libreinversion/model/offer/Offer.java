package co.com.bancolombia.libreinversion.model.offer;

import co.com.bancolombia.libreinversion.model.request.DisbursementDestination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Offer {

    @Schema(required = true)
    private String id;

    @Schema(required = true)
    private String amount;

    @Schema(required = true)
    private String term;

    @Schema(required = true)
    private String interestRateType;

    @Schema(required = true)
    private Integer paymentDay;

    @Schema(required = true)
    private DisbursementDestination disbursementDestination;
}
