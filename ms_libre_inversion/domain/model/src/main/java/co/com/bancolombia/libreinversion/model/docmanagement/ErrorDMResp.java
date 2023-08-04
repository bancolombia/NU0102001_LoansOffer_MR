package co.com.bancolombia.libreinversion.model.docmanagement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDMResp {
    private Meta meta;
    private int status;
    private String title;
    private List<ErrorDMDetail> errors;
}
