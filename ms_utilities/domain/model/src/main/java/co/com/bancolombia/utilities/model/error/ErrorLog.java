package co.com.bancolombia.utilities.model.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class ErrorLog {
    private String errorCode;
    private String detailError;
    private String serviceName;
    private String actionName;
    private String messageId;
}
