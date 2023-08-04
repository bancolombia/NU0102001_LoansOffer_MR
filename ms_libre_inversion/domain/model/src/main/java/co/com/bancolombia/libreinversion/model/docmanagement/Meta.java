package co.com.bancolombia.libreinversion.model.docmanagement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Meta {
    private String _messageId;
    private Date _requestDateTime;
    private String _applicationId;
    private DataResp data;
}
