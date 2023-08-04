package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.response.creategiradorpn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "datosPersonaRespuesta",
        propOrder = {"identificacion",
                "primerApellido",
                "segundoApellido",
                "nombreCompleto",
                "mensajeRespuesta",
        "idCuenta"})
@XmlRootElement(name = "datosPersonaRespuesta")
public class DatosPersonaRespuesta {

    @XmlElement(name = "identificacion")
    public IdentificacionPersona identificacion;
    private String primerApellido;
    private String segundoApellido;
    private String nombreCompleto;
    private String mensajeRespuesta;
    private Integer idCuenta;
}
