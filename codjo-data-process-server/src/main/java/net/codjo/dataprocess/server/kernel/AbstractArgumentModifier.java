/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.kernel;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.util.CommonUtils;
import java.util.List;
/**
 *
 */
public abstract class AbstractArgumentModifier implements ArgumentModifier {
    public void showParameters(List<String> argument) {
        String result = CommonUtils.listToString(argument);
        if (Log.isInfoEnabled()) {
            Log.info(getClass(), "Paramètres de '" + getClass().getName() + "' :\n" + result);
        }
    }
}
