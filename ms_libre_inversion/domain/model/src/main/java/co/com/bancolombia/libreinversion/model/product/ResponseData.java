package co.com.bancolombia.libreinversion.model.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseData {

    private Map<String, Object> utilLoad;
    private boolean valid;
}
