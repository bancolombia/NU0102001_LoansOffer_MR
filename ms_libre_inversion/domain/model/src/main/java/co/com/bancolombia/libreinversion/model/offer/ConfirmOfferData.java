package co.com.bancolombia.libreinversion.model.offer;

import co.com.bancolombia.libreinversion.model.commons.Header;
import co.com.bancolombia.libreinversion.model.document.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmOfferData {

    private Header header;
    private List<Document> documents;

}
