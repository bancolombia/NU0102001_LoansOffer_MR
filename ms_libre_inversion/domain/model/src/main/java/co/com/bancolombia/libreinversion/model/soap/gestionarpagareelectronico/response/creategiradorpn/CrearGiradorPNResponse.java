package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.response.creategiradorpn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"datosPersonaRespuesta"})
@XmlRootElement(name = "crearGiradorPNResponse")
public class CrearGiradorPNResponse {

    private DatosPersonaRespuesta datosPersonaRespuesta;

}
