package co.com.bancolombia.libreinversion.model.offer;

import co.com.bancolombia.libreinversion.model.commons.Header;
import co.com.bancolombia.libreinversion.model.document.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class SellOfferData {
    private Header header;
    private LoanSell loans;
    private List<Document> documents;
}
