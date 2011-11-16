package net.codjo.dataprocess.common.model;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
/**
 *
 */
public class ArgModelHelper {
    private ArgModelHelper() {
    }


    public static boolean isGlobalValue(String val) {
        return val.trim().startsWith("$") && val.endsWith("$");
    }


    public static boolean isLocalValue(String val) {
        return val.trim().startsWith("#") && val.endsWith("#");
    }


    public static boolean isFunctionValue(String val) {
        return val.contains("(") && val.contains(")");
    }


    public static String getGlobalValue(String val) {
        if (isGlobalValue(val)) {
            return val.trim().substring(1, val.length() - 1);
        }
        else {
            return null;
        }
    }


    public static String getLocalValue(String val) {
        if (isLocalValue(val)) {
            return val.trim().substring(1, val.length() - 1);
        }
        else {
            return null;
        }
    }


    public static List<String> getFunctionParams(String val) {
        if (isFunctionValue(val)) {
            List<String> params = new ArrayList<String>();
            String paramStr = val.substring(val.indexOf('(') + 1, val.length() - 1);
            List<String> rawParameters = getParameters(paramStr);
            for (String parameter : rawParameters) {
                params.add(parameter.trim());
            }
            return params;
        }
        else {
            return null;
        }
    }


    public static List<String> getParameters(String str) {
        List<String> parameters = new ArrayList<String>();
        if (str == null || str.length() == 0) {
            return parameters;
        }
        StringBuilder partToken = new StringBuilder();
        StringTokenizer listTokenizer = new StringTokenizer(str, ",");
        while (listTokenizer.hasMoreTokens()) {
            String token = listTokenizer.nextToken();
            if (proceedPartTok(parameters, partToken.toString())) {
                partToken = new StringBuilder();
            }
            if (token.trim().startsWith("'") && token.trim().endsWith("'")) {
                token = removeQuote(token.trim());
                parameters.add(token);
            }
            else {
                partToken.append(token);
                if (!partToken.toString().trim().endsWith("'")) {
                    partToken.append(",");
                }
            }
        }
        proceedPartTok(parameters, partToken.toString());
        return parameters;
    }


    private static boolean proceedPartTok(List<String> parameters, String partToken) {
        if (partToken.length() != 0) {
            if (partToken.trim().startsWith("'") && partToken.trim().endsWith("'")) {
                partToken = removeQuote(partToken.trim());
                parameters.add(partToken);
                return true;
            }
        }
        return false;
    }


    private static String removeQuote(String str) {
        return str.substring(1, str.length() - 1);
    }
}
