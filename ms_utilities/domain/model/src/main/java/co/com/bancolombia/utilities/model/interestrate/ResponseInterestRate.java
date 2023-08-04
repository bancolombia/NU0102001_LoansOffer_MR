package co.com.bancolombia.utilities.model.interestrate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseInterestRate {
    private Meta meta;
    private ResponseInterestRateData data;
    private Links links;
}