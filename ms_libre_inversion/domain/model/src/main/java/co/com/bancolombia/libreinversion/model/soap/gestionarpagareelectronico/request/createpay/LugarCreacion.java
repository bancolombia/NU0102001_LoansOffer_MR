package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.createpay;

import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.Lugar;
import lombok.Builder;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "lugarDesembolso", propOrder = {"pais", "departamento", "ciudad"})
public class LugarCreacion extends Lugar {

    @Builder
    public LugarCreacion(String pais, String departamento, String ciudad) {
        super(pais, departamento, ciudad);
    }

    public LugarCreacion() {
    }
}
