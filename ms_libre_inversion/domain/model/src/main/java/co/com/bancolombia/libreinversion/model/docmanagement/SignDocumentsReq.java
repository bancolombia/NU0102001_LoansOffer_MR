package co.com.bancolombia.libreinversion.model.docmanagement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class SignDocumentsReq {

    private DataReq data;
}
