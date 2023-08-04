package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "archivoAdjunto", propOrder = {"nombreArchivo", "contenidoArchivo"})
public class ArchivoAdjunto {

    private String nombreArchivo;
    private String contenidoArchivo;
}
