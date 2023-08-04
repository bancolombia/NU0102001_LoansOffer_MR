package co.com.bancolombia.libreinversion.model.commons;


public enum ErrorEnum {

    MSG_LI005("LI005", "Lo sentimos, No hay datos de contacto"),
    MSG_LI006("LI006","Lo sentimos, El tipo de documento no es valido"),
    MSG_LI007("LI007","Lo sentimos, La edad del cliente no es valida"),
    MSG_LI008("LI008","Lo sentimos, El segmento del cliente no es valido"),
    MSG_LI009("LI009","Lo sentimos, La ciudad del cliente no es valida"),
    MSG_LI010("LI010","Lo sentimos, No hay cuentas validas"),
    MSG_LI011("LI011","Lo sentimos, El producto solicitado no corresponde con el preaprobado"),
    MSG_LI012("LI012","Lo sentimos, No se ha encontrado factor de seguro"),
    MSG_LI013("LI013","Lo sentimos, No se ha encontrado tipo de tasa"),
    MSG_LI014("LI014","Lo sentimos, el cupo no cumple con los montos"),
    MSG_LI015("LI015","Lo sentimos, no se pudo crear los documentos del Pagaré"),
    MSG_LI016("LI016","Lo sentimos, algunas experiencias de usario no estan disponible"),
    MSG_LI017("LI017","Lo sentimos, no se pudo realizar la operación, intente mas tarde"),
    MSG_LI022("LI022", "Lo sentimos, no se pudo realizar el envío de la notificación"),
    MSG_CD001("CD001","Número de identificación no existe"),
    MSG_CD003("CD003","Tipo de identificación no corresponde a número de identificación"),
    MSG_GP001("GP001","Error en el proceso de gestión documental."),
    MSG_VP006("VP006","Documentos no definidos para el producto"),
    MSG_GS001("GS001","Error en las verificaciones de seguros."),
    MSG_LI018("LI018","En este momento no es posible continuar con la experiencia."),
    MSG_LI019("LI019", "En este momento no es posible continuar con la experiencia, los datos no estan actualizados."),
    MSG_VP005("VP005", "Edad del cliente fuera de los rangos permitidos para el producto."),
    MSG_LI020("LI020","Lo sentimos, Error en el proceso de Archivos, intente mas tarde"),
    MSG_LI021("LI021","Lo sentimos, no se pudo obtener los datos del cliente"),
    MSG_GP005("GP005", "El cliente tiene pertenece a un grupo de riesgo no permitido."),
    MSG_OR001("OR001", "Mecánismo de autenticación es requerido."),
    MSG_BI003("BI003", "No existe información del preaprobado"),
    MSG_LI023("LI023","Lo sentimos, el monto a desembolsar es mayor seleccionado"),
    MSG_LI024("LI023", "Gateway server timeout"),
    MSG_LI025("LI025", "Loan amount client error"),
    MSG_LI026("LI026", "Loan amount server error"),
    MSG_LI027("LI027", "Lo sentimos el cliente no tiene limite para este producto");


    private String message;
    private String name;

    ErrorEnum(String name, String message) {
        this.message = message;
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }
}
