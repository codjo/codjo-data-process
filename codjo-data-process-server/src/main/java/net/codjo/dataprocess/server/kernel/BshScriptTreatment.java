package net.codjo.dataprocess.server.kernel;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.TargetError;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.TreatmentModel;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
class BshScriptTreatment extends AbstractTreatment {
    private static final String RESULT = "RESULT";
    private static final String PARAM = "PARAM";
    private static final String TREATMENT = "treatment";


    public Object proceedTreatment(DataProcessContext context, Object... object) throws TreatmentException {
        return proceedBshScript(object);
    }


    private Object proceedBshScript(Object[] object) throws TreatmentException {
        Interpreter interpreter = new Interpreter();
        interpreter.setStrictJava(true);
        String code = getTreatmentModel().getTarget();
        try {
            interpreter.set(RESULT, "");
            interpreter.set(PARAM, object);
            interpreter.set(TREATMENT, new TreatmentRestrictedAccess());
            interpreter.eval(code);
            return interpreter.get(RESULT);
        }
        catch (EvalError evalError) {
            if (evalError instanceof TargetError) {
                String errorMsg = "Erreur ligne " + evalError.getErrorLineNumber() + " --> "
                                  + evalError.getErrorText() + "\n" + evalError.getMessage();
                Throwable throwable = ((TargetError)evalError).getTarget();
                if (throwable instanceof TreatmentException) {
                    throw (TreatmentException)throwable;
                }
                throw new TreatmentException(errorMsg, throwable);
            }
            throw new TreatmentException("L'exécution du script Bean Shell a échoué !\n", evalError);
        }
    }


    public static BshScriptTreatment create(Connection con,
                                            TreatmentModel treatmentModel,
                                            int repositoryId,
                                            ExecutionListModel executionListModel) {
        BshScriptTreatment treatment = new BshScriptTreatment();
        treatment.setConnection(con);
        treatment.setTreatmentModel(treatmentModel);
        treatment.setExecutionListModel(executionListModel);
        treatment.setRepositoryId(repositoryId);
        treatment.buildArgument();
        return treatment;
    }


    public class TreatmentRestrictedAccess {
        public String getArgument(String key) {
            return BshScriptTreatment.this.getArgument(key);
        }


        public void sendInformationToClient(String message) {
            BshScriptTreatment.this.sendInformationToClient(message);
        }


        public Connection getConnection() {
            return BshScriptTreatment.this.getConnection();
        }


        public Object executeTreatment(String treatmentId,
                                       DataProcessContext context,
                                       String[] repositoryPath, Object... param)
              throws TreatmentException, SQLException {
            return BshScriptTreatment.this.executeTreatment(treatmentId, context, repositoryPath, param);
        }
    }
}
