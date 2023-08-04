package co.com.bancolombia.libreinversion.model.request;

import co.com.bancolombia.libreinversion.model.customer.Customer;
import co.com.bancolombia.libreinversion.model.offer.Offer;
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
public class ConfirmOfferRQ {
    private String legalTrace;
    @Schema(required = true)
    private Customer customer;

    @Schema(required = true)
    private Offer offer;
    private DirectDebit directDebit;

    @Schema(required = true)
    private List<Insurances> insurances;

    @Schema(required = true)
    private Boolean partial;
}
