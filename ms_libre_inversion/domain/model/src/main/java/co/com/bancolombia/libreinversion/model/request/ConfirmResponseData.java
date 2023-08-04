package co.com.bancolombia.libreinversion.model.request;


import co.com.bancolombia.libreinversion.model.commons.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ConfirmResponseData {

    @Schema(required = true)
    private DocumentResponse documents;
}
