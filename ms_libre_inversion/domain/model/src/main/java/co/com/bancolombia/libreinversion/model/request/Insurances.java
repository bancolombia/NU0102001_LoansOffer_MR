package co.com.bancolombia.libreinversion.model.request;


import co.com.bancolombia.libreinversion.model.beneficiary.Beneficiaries;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Insurances {

    @Schema(required = true)
    private String type;
    private Beneficiaries beneficiaries;
}
