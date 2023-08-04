package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.header;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "userId", propOrder = {"userName", "userToken"})
@XmlRootElement(name = "userId")
public class UserId {

    private String userName;
    private String userToken;

}
