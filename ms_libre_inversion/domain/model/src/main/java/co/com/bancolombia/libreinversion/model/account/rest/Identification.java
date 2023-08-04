package co.com.bancolombia.libreinversion.model.account.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Identification {

    @Schema(required = true)
    private String type;

    @Schema(required = true)
    private String number;
}
