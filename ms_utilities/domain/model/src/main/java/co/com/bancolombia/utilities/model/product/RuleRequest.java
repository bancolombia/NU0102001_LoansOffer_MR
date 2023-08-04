package co.com.bancolombia.utilities.model.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RuleRequest {
    private List<Attribute> attributes;
    private String productCode;
    private String ruleName;
}
