package co.com.bancolombia.libreinversion.model.constants;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.commons.ErrorEnum;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;

public class SellConst {

    private SellConst() {
        throw new IllegalStateException("Utility class");
    }

    public static final String MAP_PRODUCT_CODE = "pruebas";
    public static final String MAP_PCG_PARAMETER = "PCGParameter";
    public static final String MAP_CUSTOMER_ADVISORY_CODE = "pruebas";
    public static final String MAP_CUSTOMER_BRANCH_CODE = "pruebas";
    public static final String MAP_DECEVAL_SYSTEM_ID = "pruebas";
    public static final String MAP_DECEVAL_COD_DEPOSITANTE = "decevalCodDepositante";
    public static final String MAP_DECEVAL_USUARIO = "pruebas";
    public static final String MAP_DES_NAME_SPACE = "pruebas";
    public static final String MAP_DECEVAL_USER_NAME = "pruebas";
    public static final String MAP_DECEVAL_DES_NAME = "pruebas";
    public static final String MAP_DECEVAL_PARAM = "pruebas";
    public static final String MAP_DOC_MANAGER_PARAM = "pruebas";
    public static final String MAP_DOC_MANAGER_HEAD_USER_NAME = "pruebas";
    public static final String MAP_DOC_MANAGER_HEAD_USER_TOKEN = "pruebas";
    public static final String USER_TEST_DECEVAL = "pruebas";
    public static final String ERR_DECEVAL_PARAM_NOT_FOUND = "Regla 'decevalParameter' no parametrizada en el MAP";
    public static final String KEY_STEP_FUNCT_DOC = "pruebas";
    public static final String KEY_STEP_QUERY_DATA = "pruebas";
    public static final String DOC_MANAGE_X_TYPE_CLIENT = "pruebas";
    public static final String PERSON_MANAGE_OBSERVATIONS = "Esta es una gestion con el cliente";
    public static final String MAP_OPPORTUNITI_PERSONMANAGEPARAM = "pruebas";
    public static final String MAP_OPPORT_PER_STATUSCODE = "pruebas";
    public static final String MAP_OPPORT_PER_SUBSTATUSCODE = "pruebas";
    public static final String MAP_SELL_OFFER_PARAM = "pruebas";
    public static final String MAP_CARDIF_COD_EMPLOYMENT = "pruebas";
    public static final String NIT_ISSUER = "pruebas";
    public static final String COMPANY_GRANTING = "pruebas";
    public static final String TYPE_TXT = "text";
    public static final String MONTHLY = "pruebas";
    public static final String SUBJECT_EMAIL = "pruebas";
    public static final String ID_TEMPLATE_EMAIL = "123456";
    public static final String MSG_TEMPLATE_EMAIL = "masiv-template/html";
    public static final String DOMAIN_EMAIL = "Notificación <pruebas>";
    public static final String NOT = "N";
    public static final String YES = "Y";
    public static final String CACHE_SELL_OFFER = "sell_offer_process_";
    public static final String SELL_OFFER = "pruebas";
    public static final int PRIORITY_TWO = 2;
    public static final int CLASS_DEFINITION_DOC = 150;
    public static final int ID_ROL_SIGNER = 5;
    public static final String NAME_DOCUMENT_SELL = "NombreDocumentoSell";
    public static final String ERR_DOCS_S3 = Constant.AWS_S3_CLIENT_ERROR + " process Document: ";
    public static final String ERR_DOCS_S3_NOT_FOUND = "Document loan not found: ";
    public static final String ERR_KEY_S3_NOT_FOUND = "The specified key does not exist";
    public static final String ERR_BUCKET_S3_NOT_FOUND = "The specified bucket does not exist";
    public static final String ERR_REPO_S3_NOT_FOUND = " repository file not found";

    public static LibreInversionException dataClientErrorProcess(String operation, String msgId) {
        return new LibreInversionException(ErrorEnum.MSG_LI021.getMessage(), ErrorEnum.MSG_LI021.getName(),
                ErrorEnum.MSG_LI021.getMessage(), SellConst.SELL_OFFER, operation, msgId);
    }
}
