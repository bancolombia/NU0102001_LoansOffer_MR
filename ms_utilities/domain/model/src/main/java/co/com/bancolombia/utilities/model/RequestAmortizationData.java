package co.com.bancolombia.utilities.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RequestAmortizationData {
    private Customer customer;
    private Offer offer;
    private boolean amortizationSchedule;
}
