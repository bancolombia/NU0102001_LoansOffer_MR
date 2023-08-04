package co.com.bancolombia.libreinversion.model.rate;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class MetaData {

    private String _messageId;
    private String _requestDateTime;
    private String _applicationId;

}
