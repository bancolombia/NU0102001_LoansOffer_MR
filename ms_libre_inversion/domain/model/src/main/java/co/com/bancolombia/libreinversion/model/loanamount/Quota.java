package co.com.bancolombia.libreinversion.model.loanamount;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Quota {
    private BigDecimal sequenceNumber;
    private String lineOfCreditCode;
    private BigDecimal branchOffice;
    private String currencyCode;
    @JsonProperty("MLAStatus")
    private String mlAStatus;
    private BigDecimal approvedAmount;
    @JsonProperty("COPApprovedAmount")
    private BigDecimal copApprovedAmount;
    private BigDecimal availableAmount;
    private BigDecimal payments;
    private BigDecimal withdrawals;
    @JsonProperty("MLARecordDate")
    private LocalDate mlARecordDate;
    @JsonProperty("MLAexpirationDate")
    private LocalDate mlAexpirationDate;
    private BigDecimal exchangeRate;
}

