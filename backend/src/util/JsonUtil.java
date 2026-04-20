package util;

public class JsonUtil {
    private JsonUtil() {
    }

    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    public static String string(String value) {
        return "\"" + escape(value) + "\"";
    }
}
