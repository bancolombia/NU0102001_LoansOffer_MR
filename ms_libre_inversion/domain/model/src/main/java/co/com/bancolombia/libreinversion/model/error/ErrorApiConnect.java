package co.com.bancolombia.libreinversion.model.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ErrorApiConnect {

    private String id;
    private String status;
    private String code;
    private String title;
    private String detail;
}
