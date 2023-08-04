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
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"tipoDocumento", "numeroDocumento"})
@XmlRootElement(name = "identificacionOtorgante")
@EqualsAndHashCode(callSuper=false)
public class IdentificacionOtorgante extends BaseIndentification {

    @Builder
    public IdentificacionOtorgante(String tipoDocumento, String numeroDocumento) {
        super(tipoDocumento, numeroDocumento);
    }

    public IdentificacionOtorgante() {
    }
}
