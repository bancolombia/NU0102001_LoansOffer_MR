package co.com.bancolombia.libreinversion.model.enums;

import lombok.Getter;

@Getter
public enum FuncPagareEnum {

    CREAR_PAGARE(""), CREAR_GIRADOR(""), FIRMAR_PAGARE("");
    private String value;

    FuncPagareEnum(String valor) {
        this.value = valor;
    }
}
