package net.codjo.dataprocess.server.control;
import net.codjo.control.common.ControlException;
import net.codjo.control.common.Dictionary;
import net.codjo.control.common.Variable;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.server.dao.RepositoryDao;
import net.codjo.dataprocess.server.handlerhelper.TreatmentHandlerHelper;
import java.sql.Connection;
import java.util.Iterator;
/**
 *
 */
public class DataProcessControl {
    public static final int TECHNICAL_ERROR_TYPE = 499;
    public static final String TECHNICAL_ERROR_PREFIX = "ERREUR TECHNIQUE : ";
    public static final String FINAL_TABLE = "final.table";
    public static final String ERROR_CODE = "error_code";
    private String repositoryName;
    private String treatmentId;


    public DataProcessControl(String repositoryName, String treatmentId) {
        this.repositoryName = repositoryName;
        this.treatmentId = treatmentId;
    }


    public void control(Connection con, Dictionary dico, Object[] treatmentParam) throws ControlException {
        try {
            int repositoryId = new RepositoryDao().getRepositoryIdFromName(con, repositoryName);
            TreatmentHandlerHelper.proceedTreatment(con, repositoryId, treatmentId,
                                                    new DataProcessContext().encode(), treatmentParam);
        }
        catch (Exception ex) {
            if (ex.getCause() != null && ex.getCause() instanceof ControlException) {
                throw (ControlException)ex.getCause();
            }
            else {
                Log.error(getClass(),
                          "Erreur technique lors de l'exécution du traitement '" + treatmentId
                          + "' du repository '" + repositoryName + "'", ex);
                throw new ControlException(TECHNICAL_ERROR_TYPE, getTechnicalErrorMessage(ex));
            }
        }
    }


    public String getTechnicalErrorMessage(Throwable ex) {
        if (ex.getLocalizedMessage() == null) {
            return DataProcessControl.TECHNICAL_ERROR_PREFIX + ex.getClass().getName();
        }
        return DataProcessControl.TECHNICAL_ERROR_PREFIX
               + ex.getLocalizedMessage()
              .substring(0, Math.min(ex.getLocalizedMessage().length(),
                                     254 - DataProcessControl.TECHNICAL_ERROR_PREFIX.length()));
    }


    public String getValueFromDico(Dictionary dico, String key) {
        Iterator iterator = dico.getVariables().iterator();
        String value = getVariable(iterator, key);
        if ((value == null)) {
            if (dico.getParent() != null) {
                iterator = dico.getParent().getVariables().iterator();
                value = getVariable(iterator, key);
            }
        }
        return value;
    }


    private static String getVariable(Iterator iterator, String variableName) {
        while (iterator.hasNext()) {
            Variable var = (Variable)iterator.next();
            if (var.getName().equals(variableName)) {
                return var.getValue();
            }
        }
        return null;
    }
}
