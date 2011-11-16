package net.codjo.dataprocess.server.handlerfactory;
import java.util.List;
import java.util.Map;
/**
 *
 */
public class HandlerFactoryHelper {
    private HandlerFactoryHelper() {
    }


    public static String getAndExtract(Map<String, String> args, String key) {
        String value = args.get(key);
        args.remove(key);
        return value;
    }


    public static String getAndExtractIf(Map<String, String> args, String key, List<String> listValue) {
        String localValue = args.get(key);
        if (listValue.contains(localValue)) {
            args.remove(key);
        }
        return localValue;
    }


    public static Object get(Map args, String key) {
        return args.get(key);
    }
}
