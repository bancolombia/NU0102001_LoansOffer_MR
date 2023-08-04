package co.com.bancolombia.libreinversion.model.rate;


import co.com.bancolombia.libreinversion.model.commons.Header;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LoanRate {

    private Header header;
    private List<RateRange> rateRange;
}
