package co.com.bancolombia.utilities.model.interestrate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseInterestRateData {
    private Header header;
    private List<RateRange> rateRange;
}