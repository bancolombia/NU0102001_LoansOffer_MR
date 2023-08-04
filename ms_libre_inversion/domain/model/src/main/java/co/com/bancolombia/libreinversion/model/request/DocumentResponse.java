package co.com.bancolombia.libreinversion.model.request;


import co.com.bancolombia.libreinversion.model.document.Document;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DocumentResponse {

    @Schema(required = true)
    private List<Document> document;
}
