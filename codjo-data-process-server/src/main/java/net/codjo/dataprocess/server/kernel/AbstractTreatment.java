/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.kernel;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.ArgList;
import net.codjo.dataprocess.common.model.ArgModel;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.common.report.TreatmentReport;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
/**
 *
 */
abstract class AbstractTreatment implements Treatment {
    private Map<String, Argument> args;
    private int repositoryId = 0;
    private Connection con;
    private TreatmentModel treatmentModel;
    private ExecutionListModel executionListModel;
    private TreatmentResultSender treatmentResultSender;
    private Exception error;
    private Object result;


    public TreatmentModel getTreatmentModel() {
        return treatmentModel;
    }


    public void setTreatmentModel(TreatmentModel treatmentModel) {
        this.treatmentModel = treatmentModel;
    }


    public void setExecutionListModel(ExecutionListModel executionListModel) {
        this.executionListModel = executionListModel;
    }


    public ExecutionListModel getExecutionListModel() {
        return executionListModel;
    }


    public int getRepositoryId() {
        return repositoryId;
    }


    public void setRepositoryId(int repositoryId) {
        this.repositoryId = repositoryId;
    }


    public Connection getConnection() {
        return con;
    }


    public void setConnection(Connection con) {
        this.con = con;
    }


    public void setArgs(Map<String, Argument> args) {
        this.args = args;
    }


    public Map<String, Argument> getArgs() {
        return args;
    }


    public void configure(DataProcessContext context) throws TreatmentException {
        if (args != null && !args.isEmpty()) {
            for (Entry<String, Argument> entry : args.entrySet()) {
                Argument arg = entry.getValue();
                arg.computeValue(con, context, repositoryId,
                                 executionListModel == null ? null : executionListModel.getName());
            }
        }
    }


    public List<String> getNotResolvableArguments(DataProcessContext context) {
        List<String> parametersList = new ArrayList<String>();

        if (args != null && !args.isEmpty()) {
            for (Entry<String, Argument> entry : args.entrySet()) {
                Argument arg = entry.getValue();
                parametersList.addAll(arg.getNotResolvableValue(con, context,
                                                                repositoryId,
                                                                executionListModel == null ?
                                                                null :
                                                                executionListModel.getName()));
            }
        }
        return parametersList;
    }


    public String getArgument(String key) {
        if (args != null) {
            Argument argument = args.get(key);
            if (argument != null) {
                return argument.getValue();
            }
        }
        return null;
    }


    protected void buildArgument() {
        Log.debug(getClass(), "************* DEFAULT BUILD ARGUMENTS");
        args = new HashMap<String, Argument>();

        ArgList arglist = treatmentModel.getArguments();
        if (arglist.getArgs() != null) {
            for (ArgModel argModel : arglist.getArgs()) {
                args.put(argModel.getName(), new Argument(argModel));
            }
        }
        else {
            args = null;
        }
    }


    public void sendInformationToClient(String message) {
        if (treatmentResultSender != null) {
            treatmentResultSender.sendInformationMessage(treatmentModel, message);
        }
        else {
            if (Log.isInfoEnabled()) {
                Log.info(getClass(),
                         "[SEND INFORMATION(treatment : " + treatmentModel.getId() + ")] : " + message);
            }
        }
    }


    public void setTreatmentResultSender(TreatmentResultSender treatmentResultSender) {
        this.treatmentResultSender = treatmentResultSender;
    }


    public Object executeTreatment(String treatmentId,
                                   DataProcessContext context,
                                   String[] repositoryPath, Object... param)
          throws TreatmentException, SQLException {
        return new TreatmentLauncher().proceedTreatment(con, repositoryPath, treatmentId, context, param);
    }


    public void setError(Exception error) {
        this.error = error;
    }


    public Exception getError() {
        return error;
    }


    public void setResult(Object result) {
        this.result = result;
    }


    public Object getResult() {
        return result;
    }


    public TreatmentReport getTreatmentReport() {
        return new TreatmentReport(treatmentModel.getId(),
                                   result != null ? result.toString() : null,
                                   error != null ? error.getLocalizedMessage() : null);
    }
}
