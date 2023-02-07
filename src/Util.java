import java.sql.Array;
import java.util.*;

public final class Util {
    public static Map<String, Object> convertStringToWrapper(String stringValue, Class javaDataTypeClass) {
        Object wrappedValue;
        Map<String, Object> conversionResult = new HashMap<>();

        if(Boolean.class.isAssignableFrom(javaDataTypeClass)) {
            wrappedValue = Boolean.parseBoolean(stringValue);
            conversionResult.put("conversionSuccess", true);
            conversionResult.put("convertedValue", wrappedValue);
        } else if(Byte.class.isAssignableFrom(javaDataTypeClass)) {
            wrappedValue = Byte.parseByte(stringValue);
            conversionResult.put("conversionSuccess", true);
            conversionResult.put("convertedValue", wrappedValue);
        } else if(Short.class.isAssignableFrom(javaDataTypeClass)) {
            wrappedValue = Short.parseShort(stringValue);
            conversionResult.put("conversionSuccess", true);
            conversionResult.put("convertedValue", wrappedValue);
        } else if(Integer.class.isAssignableFrom(javaDataTypeClass)) {
            wrappedValue = Integer.parseInt(stringValue);
            conversionResult.put("conversionSuccess", true);
            conversionResult.put("convertedValue", wrappedValue);
        } else if(Long.class.isAssignableFrom(javaDataTypeClass)) {
            wrappedValue = Long.parseLong(stringValue);
            conversionResult.put("conversionSuccess", true);
            conversionResult.put("convertedValue", wrappedValue);
        } else if(Float.class.isAssignableFrom(javaDataTypeClass)) {
            wrappedValue = Float.parseFloat(stringValue);
            conversionResult.put("conversionSuccess", true);
            conversionResult.put("convertedValue", wrappedValue);
        } else if(Double.class.isAssignableFrom(javaDataTypeClass)) {
            wrappedValue = Double.parseDouble(stringValue);
            conversionResult.put("conversionSuccess", true);
            conversionResult.put("convertedValue", wrappedValue);
        } else {
            conversionResult.put("conversionSuccess", false);
            conversionResult.put("convertedValue", null);
        }

        return conversionResult;
    }
}
