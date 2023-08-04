package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.creategiradorpn;

import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.Ubicacion;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@Data
@EqualsAndHashCode(callSuper=false)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "direccionOficina", propOrder = {"direccion", "telefono", "fax"})
public class DireccionOficina extends Ubicacion {

    @Builder
    public DireccionOficina(String direccion, String telefono, String fax) {
        super(direccion, telefono, fax);
    }

    public DireccionOficina() {
    }
}
