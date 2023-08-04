package co.com.bancolombia.libreinversion.model.enums;

import lombok.Getter;

@Getter
public enum InsuranceTypeEnum {
    SV("SV", "Seguro vida"),
    VE("VE", "Seguro de vehículo"),
    VM("VM", "Seguro vida más"),
    SD("SD", "Seguro desempleo");

    private final String key;
    private final String value;

    InsuranceTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
