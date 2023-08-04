package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.header;


import co.com.bancolombia.libreinversion.model.constants.SellConst;
import co.com.bancolombia.libreinversion.model.enums.FuncPagareEnum;
import co.com.bancolombia.libreinversion.model.product.RuleResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"systemId", "messageId", "timestamp",
        "messageContext", "userId", "destination",
        "classifications"})
@XmlRootElement(name = "requestHeader", namespace = "http://grupobancolombia.com/ents/SOI/MessageFormat/V2.1")
public class RequestHeader {

    private String systemId;
    private String messageId;
    private String timestamp;
    private MessageContext messageContext;
    private UserId userId;
    private Destination destination;
    private Classifications classifications;

    public static RequestHeader from(FuncPagareEnum funcPagare) {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String strDate = dateFormat.format(date);

        return RequestHeader.builder()
                .systemId("AW1097001")
                .messageId("CrearPN24")
                .timestamp(strDate)
                .userId(UserId.builder().userName("SDAVID").build())
                .destination(Destination.builder()
                        .name("GestionarPagareElectronico")
                        .namespace("http://grupobancolombia.com/intf/Corporativo" +
                                "/AdministracionDocumentos/GestionarPagareElectronico/V1.0")
                        .operation(funcPagare.getValue())
                        .build()
                ).build();
    }

}
