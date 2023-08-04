package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.signaturepay;

import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.ArchivoAdjunto;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.BaseIndentification;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.DatosAutenticacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"datosAutenticacion", "idPagareDeceval",
        "identificacionFirmante", "clave", "motivo", "idRolFirmante",
        "archivosAdjunto"})
@XmlRootElement(name = "firmarPagare",
        namespace = "http://grupobancolombia.com/intf/Corporativo/AdministracionDocumentos/" +
                "GestionPagareElectronico/V1.0")
public class FirmarPagare {

    private DatosAutenticacion datosAutenticacion;
    private Long idPagareDeceval;
    @XmlElement(name = "identificacionFirmante", type = BaseIndentification.class)
    private IdentificacionFirmante identificacionFirmante;
    private String clave;
    private String motivo;
    private Integer idRolFirmante;
    private List<ArchivoAdjunto> archivosAdjunto;

}
