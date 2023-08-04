package co.com.bancolombia.libreinversion.model.rate;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RateRange {

    private String loanType;
    private List<RangeType> rangeType;
}
