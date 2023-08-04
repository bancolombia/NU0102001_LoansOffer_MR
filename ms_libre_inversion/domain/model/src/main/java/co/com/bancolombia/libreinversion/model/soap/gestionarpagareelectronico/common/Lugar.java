package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Lugar {

    protected String pais;
    protected String departamento;
    protected String ciudad;

}
