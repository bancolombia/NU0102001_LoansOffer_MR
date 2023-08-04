package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.creategiradorpn;

import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.BaseIndentification;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.Identification;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.Lugar;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.Ubicacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "datosPersona",
        propOrder = {"identificacionEmisor",
                "identificacion",
                "lugarExpedicionDocumento",
                "primerApellido",
                "segundoApellido",
                "nombreCompleto", "fechaExpedicion",
                "fechaNacimiento",
                "correoElectronico",
                "direccionDomicilio", "direccionOficina",
                "direccionAlterna",
                "lugarDomicilio",
                "paisNacionalidad",
                "salario",
                "tiempoServicio",
                "pensionado",
                "numeroCelular",
                "estadoCivil"})
public class DatosPersona {

    private String identificacionEmisor;
    @XmlElement(name = "identificacion", type = BaseIndentification.class)
    private Identification identificacion;
    @XmlElement(name = "lugarExpedicionDocumento", type = Lugar.class)
    private LugarExpedicionDocumento lugarExpedicionDocumento;
    private String primerApellido;
    private String segundoApellido;
    private String nombreCompleto;
    private String fechaExpedicion;
    private String fechaNacimiento;
    private String correoElectronico;
    @XmlElement(name = "direccionDomicilio", type = Ubicacion.class)
    private DireccionDomicilio direccionDomicilio;
    @XmlElement(name = "direccionOficina", type = Ubicacion.class)
    private DireccionOficina direccionOficina;
    @XmlElement(name = "direccionAlterna", type = Ubicacion.class)
    private DireccionAlterna direccionAlterna;
    @XmlElement(name = "lugarDomicilio", type = Lugar.class)
    private LugarDomicilio lugarDomicilio;
    private String paisNacionalidad;
    private Double salario;
    private String tiempoServicio;
    private Boolean pensionado;
    private String numeroCelular;
    private String estadoCivil;

}
