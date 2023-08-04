package co.com.bancolombia.notification.model.commons;

public class Constant {

    //Names Headers
    public static final String TRANSACTION_TRACKER= "Transaction-Tracker";
    public static final String SESSION_TRACKER= "Session-Tracker";
    public static final String CHANNEL= "Channel";
    public static final String DATE= "Date";
    public static final String DATETIME= "DateTime";
    public static final String IP= "Ip";
    public static final String CLIENT_ID= "ClientID";
    public static final String CLIENT_TYPE= "ClientType";
    public static final String INITIAL_DT = "initialDt";
    public static final String PAGE_AFTER = "page";
    public static final String PAGE_SIZE = "pageSize";
    public static final String TYPEDOCUMET = "typeDocument";

    //Mensajes para la cabecera Transaction-Tracker
    public static final String MESSAGE_TRANSACTION_TRACKER_NOT_BLANK
            = "ORQ001;La cabecera 'Transaction-Tracker' Es requerida.";
    public static final String MESSAGE_TRANSACTION_TRACKER_FORMAT_UUID_REQUIRED
            = "ORQ002;La cabecera 'Transaction-Tracker' Debe cumplir el formato UUID";

    //Mensajes para la cabecera Session-Tracker
    public static final String MESSAGE_SESSION_TRACKER_NOT_BLANK
            = "ORQ003;La cabecera 'Session-Tracker' Es requerida.";
    public static final String MESSAGE_SESSION_TRACKER_FORMAT_UUID_REQUIRED
            = "ORQ004;La cabecera 'Session-Tracker' debe cumplir con el formado UUID";

    //Mensajes para la cabecera Channel
    public static final String MESSAGE_CHANNEL_NOT_BLANK = "ORQ005;La cabecera 'Channel' Es requerida.";
    public static final String MESSAGE_CHANNEL_ALPHABETICAL_REQUIRED
            = "ORQ005;La cabecera 'Channel' Solo acepta letras mayusculas.";
    public static final String MESSAGE_CHANNEL_LENGTH_REQUIRED
            = "ORQ006;La cabecera 'Channel' Debe cumplir con la longitud requerida entre 1 y 3 caracteres.";

    //Mensajes para la cabecera Date
    public static final String MESSAGE_DATE_NOT_BLANK= "ORQ007;La cabecera 'Date' Es requerida.";
    public static final String MESSAGE_DATE_FORMAT_REQUIRED
            = "ORQ008;La cabecera 'Date' Debe cumplir con el formato 'yyyy/MM/dd'";
    public static final String MESSAGE_DATE_RANGE_REQUIRED
            = "ORQ009;La cabecera 'Date' Debe cumplir con un rango valido para las fechas.";

    //Mensajes para la cabecera DateTime
    public static final String MESSAGE_DATETIME_NOT_BLANK= "ORQ010;La cabecera 'DateTime' Es requerida.";
    public static final String MESSAGE_DATETIME_FORMAT_REQUIRED
            = "ORQ011;La cabecera 'DateTime' Debe cumplir con el formato HH:mm:ss";
    public static final String MESSAGE_DATETIME_RANGE_REQUIRED
            = "ORQ012;La cabecera 'DateTime' Debe cumplir con un rango valido para las horas, minutos o segundos.";

    //Mensajes para la cabecera IP
    public static final String MESSAGE_IP_NOT_BLANK= "ORQ013;La cabecera 'Ip' Es requerida.";
    public static final String MESSAGE_IP_FORMAT_REQUIRED
            = "ORQ014;La cabecera 'Ip' Debe cumplor co un valor en el siguiente rango 1.1.1.1 - 255.255.255.255";

    //Mensajes para la cabecera ClientId
    public static final String MESSAGE_CLIENT_ID_NOT_BLANK = "ORQ015;La cabecera 'ClientID' Es requerida.";
    public static final String MESSAGE_CLIENT_ID_FORMAT_REQUIRED
            = "ORQ016;La cabecera 'ClientID' solo acepta numeros positivos.";
    public static final String MESSAGE_CLIENT_ID_LENGTH_REQUIRED
            = "ORQ017;La cabecera 'ClientID' debe cumplir con la longitud requerida de 1 a 20 digitos.";
    public static final String MESSAGE_CLIENT_ID_NOT_ZERO
            = "ORQ018;La cabecera 'ClientID' debe empezar por un valor entre 1 y 9 (1-9)";

    //Mensajes para la cabecera ClientType
    public static final String MESSAGE_CLIENT_TYPE_NOT_BLANK = "ORQ019;La cabecera 'ClientType' Es requerida.";
    public static final String MESSAGE_CLIENT_TYPE_FORMAT_REQUIRED
            = "ORQ020;La cabecera 'ClientType' Solo acepta letras mayusculas.";
    public static final String MESSAGE_CLIENT_TYPE_LENGTH_REQUIRED
            = "ORQ021;La cabecera 'ClientType' Debe cumplir con una longitud entre 1 y 3 .";


    //Mensajes para la cabecera initialDt
    public static final String MESSAGE_INITIAL_DT_NOT_BLANK = "ORQ022;La cabecera 'initialDt' Es requerida.";
    public static final String MESSAGE_INITIAL_DT_FORMAT_REQUIRED
            = "ORQ023;La cabecera 'initialDt' Debe cumplir con el Formato requerido 'yyyy-MM-dd'";
    public static final String MESSAGE_INITIAL_DT_RANGE_REQUIRED
            = "ORQ024;La cabecera 'initialDt' invalido, Rango de fechas invalido.";


    //Mensajes para la cabecera finalDt
    public static final String FINAL_DT= "finalDt";
    public static final String MESSAGE_FINAL_DT_NOT_BLANK = "ORQ025;La cabecera 'finalDt' es requerida.";
    public static final String MESSAGE_FINAL_DT_FORMAT_REQUIRED
            = "ORQ026;La cabecera 'finalDt' invalido, formato requerido 'yyyy-MM-dd'";
    public static final String MESSAGE_FINAL_DT_RANGE_REQUIRED
            = "ORQ027;La cabecera 'finalDt' Debe cumplir con un rango valido para las fechas.";

    public static final String PRIORITY = "priority";
    public static final String X_IBM_CLIENT_SECRET = "X-IBM-Client-Secret";
    public static final String X_IBM_CLIENT_ID = "X-IBM-Client-Id";
    public static final String MESSAGE_ID = "Message-Id";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_BANCO_NAME = "content-type";
    public static final String CONTENT_TYPE_BANCO_VALUE = "application/vnd.bancolombia.v4+json";
    public static final String CONTENT_TYPE_BANCO_VALUE_V1 = "application/vnd.bancolombia.v1+json";
    public static final int FIRST_ARRAY_POSITION = 0;


    private Constant() {
        throw new IllegalStateException("Utility class");
    }

}
