package co.com.bancolombia.utilities.model.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Meta {
    @JsonProperty("_messageId")
    private String messageId;
    @JsonProperty("_requestDateTime")
    private Date requestDateTime;
    @JsonProperty("_applicationId")
    private String applicationId;
}
