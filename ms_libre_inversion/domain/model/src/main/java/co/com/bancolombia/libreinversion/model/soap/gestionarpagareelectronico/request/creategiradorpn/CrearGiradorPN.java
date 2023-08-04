package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.creategiradorpn;

import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.DatosAutenticacion;
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
@XmlType(propOrder = {"datosAutenticacion", "datosPersona"})
@XmlRootElement(name = "crearGiradorPN",
        namespace = "http://grupobancolombia.com/intf/Corporativo/AdministracionDocumentos" +
                "/GestionPagareElectronico/V1.0")
public class CrearGiradorPN {

    private DatosAutenticacion datosAutenticacion;
    private DatosPersona datosPersona;

}
