package co.com.bancolombia.libreinversion.model.enums;

import lombok.Getter;

@Getter
public enum ConsumerChannel {
    CHANNEL_BIZ("BIZ", "Bizagi"),
    CHANNEL_BMO("BMO", "ELIBOM (Crédito a la mano)");

    private final String key;
    private final String value;

    ConsumerChannel(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
