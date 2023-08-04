package co.com.bancolombia.libreinversion.model.customer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class CustomerValidate {

    private boolean ageRange;
    private boolean independent;
    private String insuranceType;
    private boolean cityHas;
    private String segmentType;
}
