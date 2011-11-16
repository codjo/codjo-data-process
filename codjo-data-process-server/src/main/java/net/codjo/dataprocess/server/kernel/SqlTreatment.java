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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 *
 */
class SqlTreatment extends AbstractSqlTreatment {
    public Object proceedTreatment(DataProcessContext context, Object... param)
          throws TreatmentException, SQLException {
        return proceedSqlQueryType();
    }


    private int proceedSqlQueryType() throws TreatmentException, SQLException {
        String target = getTreatmentModel().getTarget();
        if (target.trim().length() == 0) {
            return 0;
        }
        PreparedStatement pStmt = getConnection().prepareStatement(target);
        try {
            configureStatement(pStmt);
            return pStmt.executeUpdate();
        }
        finally {
            pStmt.close();
        }
    }


    public static SqlTreatment create(Connection con,
                                      TreatmentModel treatmentModel,
                                      int repositoryId,
                                      ExecutionListModel executionListModel) {
        SqlTreatment treatment = new SqlTreatment();
        treatment.setConnection(con);
        treatment.setTreatmentModel(treatmentModel);
        treatment.setExecutionListModel(executionListModel);
        treatment.setRepositoryId(repositoryId);
        treatment.buildArgument();
        return treatment;
    }
}
