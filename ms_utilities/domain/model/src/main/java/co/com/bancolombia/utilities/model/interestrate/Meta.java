package co.com.bancolombia.utilities.model.interestrate;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("_messageId")
    private String messageId;
    @JsonProperty("_requestDateTime")
    private Date requestDateTime;
    @JsonProperty("_applicationId")
    private String applicationId;
}