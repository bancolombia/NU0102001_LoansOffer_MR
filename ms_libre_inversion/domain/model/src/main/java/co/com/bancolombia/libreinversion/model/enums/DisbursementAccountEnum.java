package co.com.bancolombia.libreinversion.model.enums;

import lombok.Getter;

@Getter
public enum DisbursementAccountEnum {
    AHO("AHO", "Cuenta de ahorros"),
    CTE("CTE", "Cuenta corriente"),
    ABO("ABO", "Abono a crédito"),
    CHQ("CHQ", "Cheque de gerencia");

    private final String key;
    private final String value;

    DisbursementAccountEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
