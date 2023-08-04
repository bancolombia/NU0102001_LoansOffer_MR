package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.response.createpay;

import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.DateAdapter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "datosPagareRespuesta",
        propOrder = {"idClaseDefinicionDocumento", "tipoPagare",
                "numPagareEntidad", "mensajeRespuesta", "idPagareDeceval",
        "fechaGrabacionPagare"})
public class DatosPagareRespuesta {

    private Integer idClaseDefinicionDocumento;
    private Integer tipoPagare;
    private String numPagareEntidad;
    private String mensajeRespuesta;
    private Long idPagareDeceval;
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date fechaGrabacionPagare;
}
