/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.util;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.codec.ListCodec;
import java.util.List;
/**
 *
 */
public class TreatmentGuiLauncher {
    public Object launchTreatmentGui(String targetGuiClassName, String targetGuiClassParam)
          throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Object result = null;
        if (!"".equals(targetGuiClassName)) {
            List<String> parameters = new ListCodec().decode(targetGuiClassParam);
            if (Log.isInfoEnabled()) {
                Log.info(getClass(), "-> Début d'exécution de " + targetGuiClassName + "("
                                     + CommonUtils.listToString(parameters) + ")");
            }
            TreatmentGui treatmentGui = (TreatmentGui)Class.forName(targetGuiClassName).newInstance();
            result = treatmentGui.proceedGuiTreatment(parameters);
            if (Log.isInfoEnabled()) {
                Log.info(getClass(), "-> Fin d'exécution de " + targetGuiClassName + "("
                                     + CommonUtils.listToString(parameters) + ")");
            }
        }
        return result;
    }
}
