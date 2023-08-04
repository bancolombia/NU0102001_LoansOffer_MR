package co.com.bancolombia.libreinversion.model.beneficiary;

import co.com.bancolombia.libreinversion.model.account.rest.Identification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Beneficiarys {

    @Schema(required = true)
    private String names;

    @Schema(required = true)
    private String surenames;

    @Schema(required = true)
    private Identification identification;

    @Schema(required = true)
    private BigDecimal rate;
}
