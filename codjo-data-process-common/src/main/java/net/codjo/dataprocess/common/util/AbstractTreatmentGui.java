/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.util;
import net.codjo.dataprocess.common.Log;
import java.util.List;
/**
 *
 */
public abstract class AbstractTreatmentGui implements TreatmentGui {
    public void showParameters(List<String> argument) {
        String result = CommonUtils.listToString(argument);
        if (Log.isInfoEnabled()) {
            Log.info(getClass(), "Paramètres de '" + getClass().getName() + "' :\n" + result);
        }
    }
}
