/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.kernel;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.TreatmentModel;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
class StoredProcTreatment extends AbstractSqlTreatment {
    public Object proceedTreatment(DataProcessContext context, Object... param)
          throws TreatmentException, SQLException {
        return proceedStoredProcType();
    }


    private int proceedStoredProcType() throws TreatmentException, SQLException {
        if (getTreatmentModel().getTarget().trim().length() == 0) {
            return 0;
        }
        CallableStatement callStmt = getConnection().prepareCall(buildStoredProcQuery());
        try {
            configureStatement(callStmt);
            return callStmt.executeUpdate();
        }
        finally {
            callStmt.close();
        }
    }


    public static StoredProcTreatment create(Connection con,
                                             TreatmentModel treatmentModel,
                                             int repositoryId,
                                             ExecutionListModel executionListModel) {
        StoredProcTreatment treatment = new StoredProcTreatment();
        treatment.setConnection(con);
        treatment.setTreatmentModel(treatmentModel);
        treatment.setExecutionListModel(executionListModel);
        treatment.setRepositoryId(repositoryId);
        treatment.buildArgument();
        return treatment;
    }
}
