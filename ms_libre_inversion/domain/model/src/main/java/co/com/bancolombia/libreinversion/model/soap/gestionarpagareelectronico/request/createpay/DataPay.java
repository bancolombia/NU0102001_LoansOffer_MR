package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.createpay;


import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.ArchivoAdjunto;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.BaseIndentification;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.DateAdapter;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.Lugar;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "datosPagare")
public class DataPay {

    private String nitEmisor;
    private Integer idClaseDefinicionDocumento;
    private Integer tipoPagare;
    private String numPagareEntidad;
    private String numCredito;
    private String numReferencia;
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date fechaDesembolso;
    @XmlElement(name = "identificacionOtorgante", type = BaseIndentification.class)
    private IdentificacionOtorgante identificacionOtorgante;
    private Integer idCuentaOtorgante;
    @XmlElement(name = "identificacionApoderado", type = BaseIndentification.class)
    private IdentificacionApoderado identificacionApoderado;
    private Integer idCuentaApoderado;
    private String creditoReembolsableEn;
    private String empresaOtorgante;
    private Integer numUnidadesUVR;
    private BigDecimal valorPesosDesembolso;
    private BigDecimal valorPesosDiligenciamiento;
    private BigDecimal tasaInteresRenumEA;
    private String baseLiquidacionTasa;
    private String tasaInteres;
    private String modalidad;
    private BigDecimal dtfDesembolso;
    private BigDecimal spreed;
    private BigDecimal tasaRedescuento;
    private String dtfEA;
    private String baseLiquidacionPlazo;
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date plazoCreditoDe;
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date plazoCreditoHasta;
    private Boolean periodoGracia;
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date periodoGraciaDe;
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date periodoGraciaHasta;
    private Boolean periodoMuerto;
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date periodoMuertoDe;
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date periodoMuertoHasta;
    private String modalidadPagosInteres;
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date fechaPagoPrimerCuota;
    private String modalidadPagoCapital;
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date fechaPagoPrimerCuotaCapital;
    private String otraModalidadPago;
    private Integer numCuotas;
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date fechaPagoPrimerCuotaInteres;
    private BigDecimal valorCuotaPesos;
    private BigDecimal valorCuotaUVR;
    private BigDecimal porcentAmortizacionMensual;
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date fechaPagoPrimerCuotaMensual;
    private BigDecimal porcentAmortizacionSemestral;
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date fechaPagoPrimerCuotaSemestral;
    @XmlElement(name = "lugarDesembolso", type = Lugar.class)
    private LugarDesembolso lugarDesembolso;
    private String codOficina;
    private String regional;
    private Boolean autorizaDebitoAutomatico;
    private String cuenta;
    private Boolean adquisicionVivienda;
    private Boolean construccionViviendaIndividual;
    private Boolean mejoramientoViviendaInterSocial;
    private Boolean otroDestino;
    private String destinoOperacion;
    private Boolean vis;
    private Boolean noVis;
    private String listaAmortizaciones;
    @XmlElement(name = "lugarCreacion", type = Lugar.class)
    private LugarCreacion lugarCreacion;
    private String valorPesosDesembolsoLetras;
    private BigDecimal valorPesosActual;
    private BigDecimal valorCapitalNumero;
    private String valorCapitalLetras;
    private BigDecimal margenRedescuento;
    private BigDecimal puntosPorcentualesRedescuento;
    private String valorDesembolsoUVRLetras;
    private String tasaInteresRenumEALetras;
    private BigDecimal tasaInteresMora;
    private String tasaInteresMoraLetras;
    private BigDecimal tasaNominal;
    private String tasaInteresNominalLetras;
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date fechaVencimientoFinanciero;
    private Integer plazoNumero;
    private String plazoLetras;
    private String periodicidadPlazo;
    private Integer periodoGraciaNumero;
    private String periodoGraciaLetras;
    private String periodicidadGracia;
    private Integer periodoMuertoNumero;
    private String periodoMuertoLetras;
    private String periodicidadMuerto;
    private String numCuotasLetras;
    private String valorCuotaPesosLetras;
    private String valorCuotaUVRLetras;
    private BigDecimal valorInteresesNumero;
    private String valorInteresesLetras;
    private BigDecimal porcentajeComision;
    private BigDecimal porcentajeCobertura;
    private String garantiaAdicional;
    private String textoAdicional;
    private Integer diaPago;
    private Boolean cartaInstruccionesConPeriodoGracia;
    private ArchivoAdjunto archivoAdjunto;
    private Integer seguroVida;
    private Integer aval;
}



