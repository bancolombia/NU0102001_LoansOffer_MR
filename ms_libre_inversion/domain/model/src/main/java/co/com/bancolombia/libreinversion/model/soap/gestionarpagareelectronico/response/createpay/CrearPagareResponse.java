package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.response.createpay;

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
@XmlType(propOrder = {"datosPagareRespuesta"})
@XmlRootElement(name = "datosPagareRespuesta")
public class CrearPagareResponse {
    private DatosPagareRespuesta datosPagareRespuesta;
}
