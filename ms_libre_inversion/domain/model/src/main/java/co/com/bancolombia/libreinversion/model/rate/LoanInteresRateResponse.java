package co.com.bancolombia.libreinversion.model.rate;


import co.com.bancolombia.libreinversion.model.commons.Links;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LoanInteresRateResponse {

    private MetaData meta;
    private Links links;
}
