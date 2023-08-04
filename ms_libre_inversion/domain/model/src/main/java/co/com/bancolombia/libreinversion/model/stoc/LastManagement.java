package co.com.bancolombia.libreinversion.model.stoc;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class LastManagement extends BaseLastManagement {

    private Integer currencyCode;
    private String currencyName;
    private String nextContactDate;
    private String preapprovedChangeFlag;
    private String globalQuoteFlag;
    private String salesCodeDelegatedUser;
    private Integer contactChannelCode;
    private String contactChannelName;
    private String managementUserCode;
    private String delegatedManagementUserCode;
    private String observations;

    private List<PreaprovedDistribution> preaprovedDistribution;
}
