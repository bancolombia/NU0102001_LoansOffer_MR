package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.createpay;

import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.Lugar;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.xml.bind.annotation.XmlType;

@Data
@EqualsAndHashCode(callSuper=false)
@XmlType(name = "lugarDesembolso", propOrder = {"pais", "departamento", "ciudad"})
public class LugarDesembolso extends Lugar {

    @Builder
    public LugarDesembolso(String pais, String departamento, String ciudad) {
        super(pais, departamento, ciudad);
    }

    public LugarDesembolso() {
    }
}
