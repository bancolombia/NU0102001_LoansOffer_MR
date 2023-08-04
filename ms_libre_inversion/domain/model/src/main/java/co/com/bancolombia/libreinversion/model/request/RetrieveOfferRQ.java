package co.com.bancolombia.libreinversion.model.request;

import co.com.bancolombia.libreinversion.model.customer.Customer;
import co.com.bancolombia.libreinversion.model.product.Product;
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
public class RetrieveOfferRQ {

    @Schema(required = false)
    private String legalTrace;
    private List<Product> products;
    private Customer customer;
}
