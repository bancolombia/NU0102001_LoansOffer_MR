package co.com.bancolombia.libreinversion.model.request;

import co.com.bancolombia.libreinversion.model.customer.Customer;
import co.com.bancolombia.libreinversion.model.offer.Loan;
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
public class SellOfferRQ {

    @Schema(required = true)
    private String legalTrace;
    @Schema(required = true)
    private Customer customer;
    @Schema(required = true)
    private List<Loan> loans;
    private String alternativeEmail;
    private String alternativeCellPhoneNumber;
}
