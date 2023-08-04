package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.header;


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
@XmlType(name = "destination", propOrder = {"name", "namespace", "operation"})
public class Destination {

    private String name;
    private String namespace;
    private String operation;
}
