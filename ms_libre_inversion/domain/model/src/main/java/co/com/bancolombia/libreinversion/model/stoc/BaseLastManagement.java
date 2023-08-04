package co.com.bancolombia.libreinversion.model.stoc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class BaseLastManagement {

    private Integer stateCode;
    private String stateName;
    private Integer subStateCode;
    private String subStateName;
    private String salesCodeUser;
    private String visitDate;
    private String managementDate;
    private Integer contactTypeCode;
    private String contactTypeName;
    private Integer contactPurposeCode;
    private String contactPurposeName;
    private String businessValue;
}
