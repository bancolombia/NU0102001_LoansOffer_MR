package co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common;


import lombok.Builder;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "identificacion", propOrder = {"tipoDocumento", "numeroDocumento"})
public class Identification extends BaseIndentification {

    @Builder
    public Identification(String tipoDocumento, String numeroDocumento) {

        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;

    }

    public Identification() {
    }
}
