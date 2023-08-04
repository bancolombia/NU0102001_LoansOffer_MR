package co.com.bancolombia.libreinversion.model.commons;

public enum IdTypeEnum {

    TIPDOC_FS001("CC",1),
    TIPDOC_FS002("CE",2),
    TIPDOC_FS003("NIT",3),
    TIPDOC_FS004("TI",4);


    public final String idType;
    public final int stocType;

    IdTypeEnum(String idType, int stocType) {
        this.idType = idType;
        this.stocType = stocType;
    }

    public static IdTypeEnum valueOfType(String label) {
        for (IdTypeEnum e : values()) {
            if (e.name().equalsIgnoreCase(label)) {
                return e;
            }
        }
        return null;
    }

    public String getIdType() {
        return idType;
    }

    public int getStocType() {
        return stocType;
    }
}
