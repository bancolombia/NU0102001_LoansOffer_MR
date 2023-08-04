package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.creategiradorpn;


import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.Lugar;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@Data
@EqualsAndHashCode(callSuper = false)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "lugarExpedicionDocumento", propOrder = {"pais", "departamento", "ciudad"})
public class LugarExpedicionDocumento extends Lugar {

    @Builder
    public LugarExpedicionDocumento(String pais, String departamento, String ciudad) {
        super(pais, departamento, ciudad);
    }

    public LugarExpedicionDocumento() {
    }
}
