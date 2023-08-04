package co.com.bancolombia.libreinversion.model.constants;

public class ConstNameSpace {

    public static final String VR_ONE = "v1";
    public static final String VR_TWO = "v2";
    public static final String URI_MSG_FORMAT_DECEVAL = "http://grupobancolombia.com/ents/SOI/MessageFormat/V2.1";
    public static final String URI_ADMIN_DOC_DECEVAL = "http://grupobancolombia.com/intf/Corporativo/" +
            "AdministracionDocumentos/GestionPagareElectronico/V1.0";

    private ConstNameSpace() {
        throw new IllegalStateException("Utility class");
    }
}
