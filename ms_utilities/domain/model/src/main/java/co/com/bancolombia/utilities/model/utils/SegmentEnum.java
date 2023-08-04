package co.com.bancolombia.utilities.model.utils;

public enum SegmentEnum {

    SEGMEN_01("S2"),
    SEGMEN_02("S2"),
    SEGMEN_03("S1"),
    SEGMEN_04("S2"),
    SEGMEN_05("S2"),
    SEGMEN_08("S2"),
    SEGMEN_15("S2");

    private String message;

    SegmentEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}


