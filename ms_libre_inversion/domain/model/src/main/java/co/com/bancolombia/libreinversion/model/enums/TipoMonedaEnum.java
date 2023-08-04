package co.com.bancolombia.libreinversion.model.enums;

import lombok.Getter;

@Getter
public enum TipoMonedaEnum {
    UVR("1", "En URV"),
    PESOS("2", "En Pesos"),
    DOLARES("3", "En Dólares"),
    OTRO("2", "Otro");

    private final String key;
    private final String value;

    TipoMonedaEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
