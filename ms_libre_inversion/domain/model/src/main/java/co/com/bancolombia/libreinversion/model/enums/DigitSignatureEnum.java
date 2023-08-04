package co.com.bancolombia.libreinversion.model.enums;

import lombok.Getter;

@Getter
public enum DigitSignatureEnum {
    OTP("OTP", "123456"),
    TOKEN("Token", "234567"),
    PASSWORD("Contraseña", "74234669");

    private final String key;
    private final String value;

    DigitSignatureEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
