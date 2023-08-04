package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.createpay;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cartaInstruccionesCuotasExtras", propOrder = {"valorCuotaExtra",
        "numeroCuotasExtras", "pagaderaCada", "aPartirDelMes"})
public class CartaInstruccionesCuotasExtras {

    private BigDecimal valorCuotaExtra;
    private Integer numeroCuotasExtras;
    private Integer pagaderaCada;
    private Date aPartirDelMes;
}
