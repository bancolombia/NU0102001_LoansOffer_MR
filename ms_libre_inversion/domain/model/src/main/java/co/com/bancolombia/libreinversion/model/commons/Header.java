package co.com.bancolombia.libreinversion.model.commons;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Header {

    String type;
    String id;
    Map<String, String> additionalProp1;
}
