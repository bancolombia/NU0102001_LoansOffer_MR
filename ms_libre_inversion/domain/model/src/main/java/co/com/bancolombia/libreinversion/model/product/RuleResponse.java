package co.com.bancolombia.libreinversion.model.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RuleResponse {

    private String code;
    private ResponseData data;
    private int success;
    private String message;
}
