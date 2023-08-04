package co.com.bancolombia.utilities.model.utils;

public enum ExceptionEnum {

    LI000("Internal server error"),
    LI001("Customer commercial client error"),
    LI002("Customer commercial server error"),
    LI003("Customer contact client error"),
    LI004("Customer contact server error"),
    LI005("Customer details client error"),
    LI006("Customer details server error"),
    LI007("Installments client error"),
    LI008("Installments server error"),
    LI009("Interest rate client terror"),
    LI010("Interest rate server error"),
    LI011("Map client error"),
    LI012("Map server error"),
    LI013("No se encontraron tasas disponibles con los par\u00E1metros ingresados"),
    LI014("Es obligatorio indicar datos de identificaci\u00f3n del cliente"),
    LI015("El monto es un dato obligatorio"),
    LI016("El plazo es un dato obligatorio"),
    LI017("El tipo de seguro es un dato obligatorio / Se debe indicar la informaci\u00f3n relacionada con seguros."),
    LI018("El monto no se encuentra dentro del rango permitido."),
    LI019("El plazo no se encuentra dentro del rango permitido."),
    LI020("La identificaci\u00f3n del producto no corresponde con la experiencia."),
    LI021("El tipo de tasa no aplica para este producto.");

    private String message;

    ExceptionEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
