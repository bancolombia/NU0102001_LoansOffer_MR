package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.signaturepay;

import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.BaseIndentification;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.xml.bind.annotation.XmlType;

@Data
@EqualsAndHashCode(callSuper=false)
@XmlType(name = "identificacionFirmante", propOrder = {"tipoDocumento", "numeroDocumento"})
public class IdentificacionFirmante extends BaseIndentification {

    @Builder
    public IdentificacionFirmante(String tipoDocumento, String numeroDocumento) {
        super(tipoDocumento, numeroDocumento);
    }

    public IdentificacionFirmante() {
    }
}
