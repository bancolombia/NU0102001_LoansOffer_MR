package co.com.bancolombia.libreinversion.model.docmanagement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DataReq {
    private String documentType;
    private String documentNumber;
    private String digitalSignature;
    private DocumentSing document;
    private Traceability traceability;
}
