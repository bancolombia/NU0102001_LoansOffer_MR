package co.com.bancolombia.libreinversion.model.product;

public class ProductFactory {

    private ProductFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static String getfirstValue(ResponseData data) {
        Object firstKey = data.getUtilLoad().keySet().toArray()[0];
        Object valueForFirstKey = data.getUtilLoad().get(firstKey);
        return valueForFirstKey.toString();
    }

    public static String getLastValue(ResponseData data) {
        int couunt = data.getUtilLoad().keySet().toArray().length;
        Object lasttKey = data.getUtilLoad().keySet().toArray()[couunt - 1];
        Object valueForFirstKey = data.getUtilLoad().get(lasttKey);
        return valueForFirstKey.toString();
    }
}
