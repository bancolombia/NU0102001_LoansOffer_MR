package co.com.bancolombia.libreinversion.model.enums;

import lombok.Getter;

@Getter
public enum TipoPagareEnum {


    DILIGENCIADO(1, "Diligenciado"), EN_BLANCO(2, "En blanco con carta de instrucciones");

    private final Integer key;
    private final String value;

    TipoPagareEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }
}
