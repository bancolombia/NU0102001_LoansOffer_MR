package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class BaseIndentification implements Serializable {

    protected String tipoDocumento;
    protected String numeroDocumento;
}
