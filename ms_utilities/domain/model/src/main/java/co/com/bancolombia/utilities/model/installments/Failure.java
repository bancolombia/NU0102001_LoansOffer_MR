package co.com.bancolombia.utilities.model.installments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Failure {
    private Meta meta;
    private BigDecimal status;
    private String title;
    private List<Error> errors;
}