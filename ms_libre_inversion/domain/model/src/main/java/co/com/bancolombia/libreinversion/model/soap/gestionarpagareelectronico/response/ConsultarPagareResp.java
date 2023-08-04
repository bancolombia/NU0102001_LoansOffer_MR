package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.response;

import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.ArchivoAdjunto;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.Identification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"idPagareDeceval", "numPagareEntidad",
        "identificacionOtorgante", "nombreOtorgante", "fechaGrabacionPagare",
        "fechaFirmaPagare", "archivoAdjunto"})
@XmlRootElement(name = "datosPagareRespuesta")
public class ConsultarPagareResp {

    private Long idPagareDeceval;
    private String numPagareEntidad;
    private Identification identificacionOtorgante;
    private String nombreOtorgante;
    private Date fechaGrabacionPagare;
    private Date fechaFirmaPagare;
    private ArchivoAdjunto archivoAdjunto;
}
