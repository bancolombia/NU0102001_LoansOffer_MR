package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.createpay;


import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.Identification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Girador {

    private Identification giradorIdentificacion;
    private Integer idCuenta;
    private Integer idRol;
    private Integer idPersonaRepresentada;
}
