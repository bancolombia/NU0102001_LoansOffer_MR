package co.com.bancolombia.libreinversion.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@XmlRootElement
public class SoapEnvelopeRequest {

    private String headerContent;
    private Object body;


}
