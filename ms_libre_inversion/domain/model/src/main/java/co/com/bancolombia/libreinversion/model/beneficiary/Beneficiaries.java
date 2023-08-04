package co.com.bancolombia.libreinversion.model.beneficiary;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Beneficiaries {

    @Schema(required = true)
    private List<Beneficiarys> beneficiary;
}
