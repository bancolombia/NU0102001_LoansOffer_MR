package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.createpay;

import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.BaseIndentification;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Data
@EqualsAndHashCode(callSuper=false)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"tipoDocumento", "numeroDocumento"})
@XmlRootElement(name = "identificacionApoderado")
public class IdentificacionApoderado extends BaseIndentification {
    @Builder
    public IdentificacionApoderado(String tipoDocumento, String numeroDocumento) {
        super(tipoDocumento, numeroDocumento);
    }

    public IdentificacionApoderado() {
    }
}
