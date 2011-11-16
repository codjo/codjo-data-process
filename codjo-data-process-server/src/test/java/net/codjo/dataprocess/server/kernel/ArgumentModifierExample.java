/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.kernel;
import java.sql.Connection;
import java.util.List;
/**
 *
 */
public class ArgumentModifierExample extends AbstractArgumentModifier {
    public String proceed(Connection con, List<String> parameters) {
        //showParameters(parameters);
        StringBuilder result = new StringBuilder();
        for (String parameter : parameters) {
            result.append(parameter).append('-');
        }
        if (!parameters.isEmpty()) {
            result = result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }
}
