package co.com.bancolombia.libreinversion.model.document;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Document {

    @Schema(required = true)
    private String name;

    @Schema(required = true)
    private String format;

    @Schema(required = true)
    private String url;
}
