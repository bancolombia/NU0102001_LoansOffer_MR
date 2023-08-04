package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlType(propOrder = {"direccion", "telefono", "fax"})
public class Ubicacion {

    protected String direccion;
    protected String telefono;
    protected String fax;
}
