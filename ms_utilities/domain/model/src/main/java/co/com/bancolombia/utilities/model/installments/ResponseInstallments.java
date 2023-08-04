package co.com.bancolombia.utilities.model.installments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseInstallments {
    private Meta meta;
    private ResponseInstallmentsData data;
    private TopLevelLinks links;
}