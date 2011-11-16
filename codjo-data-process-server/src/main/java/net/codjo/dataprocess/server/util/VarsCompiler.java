/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.util;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 */
public class VarsCompiler {
    // pattern de la forme $[name]
    private static final String SELECT_PATTERN_REGXEXP = "(\\$\\[[\\w|\\.]+\\])";


    private VarsCompiler() {
    }


    public static String compile(String source, String... vars) {
        if (vars.length % 2 > 0) {
            throw new IllegalArgumentException("Length of Vars array must be odd");
        }
        Map<String, String> variables = new HashMap<String, String>();
        for (int index = 0; index < (vars.length / 2); index++) {
            String varName = vars[index * 2];
            String varValue = vars[index * 2 + 1];
            variables.put(varName, varValue);
        }
        return compile(source, variables);
    }


    public static String compile(String source, String[] varNames, String[] varValues) {
        if (varNames.length != varValues.length) {
            throw new IllegalArgumentException("Different size between names and values arrays");
        }
        Map<String, String> variables = new HashMap<String, String>();
        for (int index = 0; index < varNames.length; index++) {
            variables.put(varNames[index], varValues[index]);
        }
        return compile(source, variables);
    }


    public static String compile(String source, Map<String, String> variables) {
        StringBuilder result = new StringBuilder();
        int startPosition = 0;
        Pattern pattern = Pattern.compile(SELECT_PATTERN_REGXEXP);
        Matcher matcher = pattern.matcher(source);

        while (matcher.find()) {
            int matchedStart = matcher.start();
            String matchedGroup = matcher.group();

            String varName = matchedGroup.substring(2);
            varName = varName.substring(0, varName.length() - 1);

            String varValue = variables.get(varName);
            if (varValue == null) {
                throw new IllegalArgumentException(generateUnknowVarMessage(varName));
            }

            result.append(source.substring(startPosition, matchedStart));
            result.append(varValue);

            startPosition = matchedStart + matchedGroup.length();
        }
        result.append(source.substring(startPosition));
        return result.toString();
    }


    static String generateUnknowVarMessage(String varName) {
        return "Unkown variable name: " + varName;
    }
}
