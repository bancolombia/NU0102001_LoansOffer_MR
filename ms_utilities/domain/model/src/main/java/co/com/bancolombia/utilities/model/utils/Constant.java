package co.com.bancolombia.utilities.model.utils;

import co.com.bancolombia.utilities.model.InsuranceRequest;

import java.math.BigDecimal;

public class Constant {

    public static final Integer NUMBER_FIVE = 5;
    public static final String APP_NAME = "Utilities";
    public static final String COMPONENT_NAME = "MS_UTILITIES";

    public static final String X_IBM_CLIENT_SECRET = "X-IBM-Client-Secret";
    public static final String X_IBM_CLIENT_ID = "X-IBM-Client-Id";
    public static final String MESSAGE_ID = "Message-Id";

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_V1 = "application/vnd.bancolombia.v1+json";
    public static final String CONTENT_TYPE_V4 = "application/vnd.bancolombia.v4+json";

    public static final String X_MAP_CLIENT_ID = "CLIENT-ID";
    public static final String X_MAP_SECRET_ID = "SECRET-ID";

    public static final String HEADER_ID = "id";
    public static final String DEVICE_ID = "deviceId";
    public static final String TOKEN = "token";
    public static final String TIMESTAMP = "timestamp";
    public static final String CONSUMER = "consumer";

    public static final String BLANK = "";
    public static final String ERRORS = "errors";

    public static final String MAP_REGLA_SEGURO_DESEMPLEO = "RuleOccupationSDE";
    public static final String MAP_PROCUCT_CODE = "14";
    public static final String MAP_ATTRIBUTE_AGE = "age";
    public static final String MAP_ATTRIBUTE_OCCUPATION = "occupation";
    public static final String MAP_ATTRIBUTE_PHONE = "cellphoneMandatory";
    public static final String MAP_ATTRIBUTE_MAIL = "emailMandatory";
    public static final String MAP_VALUE_VERDADERO = "verdadero";
    public static final String MAP_VALUE_FALSO = "falso";


    public static final String CURRENCY_COP = "COP";
    public static final String TOTAL_CUOTA = "totalCuota";
    public static final String PLAN_P59 = "P59";
    public static final String PLAN_PA5 = "PA5";
    public static final String TYPE_SV = "05";
    public static final String TYPE_SD = "09";
    public static final String INSURANCE_SV = "SV";
    public static final String INSURANCE_SD = "SD";
    public static final String RATE_TYPE = "F";
    public static final BigDecimal RATE_SD = new BigDecimal("0.002808");
    public static final BigDecimal RATE_SV_1 = new BigDecimal("0.01740");
    public static final BigDecimal RATE_SV_2 = new BigDecimal("0.01788");
    public static final BigDecimal RATE_SV_3 = new BigDecimal("0.01812");
    public static final BigDecimal RATE_SV_4 = new BigDecimal("0.01860");
    public static final BigDecimal RATE_SV_5 = new BigDecimal("0.01872");
    public static final BigDecimal MIN_01 = new BigDecimal("1000000");
    public static final BigDecimal MAX_01 = new BigDecimal("20000000");
    public static final BigDecimal MIN_02 = new BigDecimal("20000001");
    public static final BigDecimal MAX_02 = new BigDecimal("50000000");
    public static final BigDecimal MIN_03 = new BigDecimal("50000001");
    public static final BigDecimal MAX_03 = new BigDecimal("100000000");
    public static final BigDecimal MIN_04 = new BigDecimal("100000001");
    public static final BigDecimal MAX_04 = new BigDecimal("200000000");
    public static final BigDecimal MIN_05 = new BigDecimal("200000001");
    public static final BigDecimal MAX_05 = new BigDecimal("500000000");
    public static final Integer MIN_TERM_PA5 = 36;
    public static final Integer MAX_TERM_PA5 = 60;
    public static final Integer MIN_TERM_P59 = 24;
    public static final Integer MAX_TERM_P59 = 60;

    public static final InsuranceRequest INSURANCE_REQUEST_SV = InsuranceRequest.builder().type("SV").build();

    private Constant() {
        throw new IllegalStateException("Utility class");
    }

}

