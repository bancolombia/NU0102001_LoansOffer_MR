package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.querypay;


import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.Identification;
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
@XmlType(propOrder = {"datosAutenticacion", "identificacionFirmante",
        "numPagareEntidad", "idEstadoPagare", "idPagareDeceval"})
@XmlRootElement(name = "consultarPagare")
public class ConsultarPagare {

    private DatosAutenticacion datosAutenticacion;
    private Identification identificacionFirmante;
    private String numPagareEntidad;
    private String idEstadoPagare;
    private String idPagareDeceval;
}
