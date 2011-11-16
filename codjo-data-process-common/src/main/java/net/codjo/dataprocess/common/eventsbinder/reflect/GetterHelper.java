package net.codjo.dataprocess.common.eventsbinder.reflect;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
/**
 *
 */
public class GetterHelper {
    private GetterHelper() {
    }


    public static Object safeGetProperty(String propertyName, Object object) {
        try {
            return getProperty(propertyName, object);
        }
        catch (GetterHelperException e) {
            return null;
        }
    }


    public static Object getProperty(String propertyName, Object object)
          throws GetterHelperException {
        if (!propertyName.contains(".")) {
            return getDirectProperty(propertyName, object);
        }
        List<String> parts = new ArrayList<String>();
        StringTokenizer stringTokenizer = new StringTokenizer(propertyName, ".");
        while (stringTokenizer.hasMoreElements()) {
            parts.add(stringTokenizer.nextElement().toString());
        }
        StringBuilder currentDone = new StringBuilder();
        Object currentObject = object;
        for (String property : parts) {
            try {
                currentObject = getDirectProperty(property, currentObject);
            }
            catch (GetterHelperException ghex) {
                if (!(ghex.getCause() instanceof NoSuchMethodException)) {
                    throw ghex;
                }
                else {
                    String msg = "Invalid property '" + currentDone + "["
                                 + propertyName.substring(currentDone.length()) + "]'";
                    throw new GetterHelperException(msg, ghex.getCause());
                }
            }
            if (currentDone.length() > 0) {
                currentDone.append(".");
            }
            currentDone.append(property);
        }
        return currentObject;
    }


    private static Object getDirectProperty(String propertyName, Object object)
          throws GetterHelperException {

        try {
            // try a get
            String getPropertyName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName
                  .substring(1);
            return object.getClass().getMethod(getPropertyName).invoke(object);
        }
        catch (Exception e) {
            // try simple name
            try {
                return object.getClass().getMethod(propertyName).invoke(object);
            }
            catch (Exception e2) {
                throw new GetterHelperException(e2);
            }
        }
    }
}
