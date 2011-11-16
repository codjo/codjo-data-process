/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.kernel;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.common.util.CommonUtils;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 *
 */
class StoredProcTreatmentWithResult extends AbstractSqlTreatment {
    private static final String DEFAULT_SEPARATOR = ", ";
    private static final String DEFAULT_QUOTE = "\"";


    public Object proceedTreatment(DataProcessContext context, Object... param)
          throws TreatmentException, SQLException {
        return proceedStoredProcTypeWithResult();
    }


    private String proceedStoredProcTypeWithResult() throws TreatmentException, SQLException {
        if (getTreatmentModel().getTarget().trim().length() == 0) {
            return "";
        }
        Boolean bColumn = null;
        String separator = getArgument(DataProcessConstants.SEPARATOR);
        String quote = getArgument(DataProcessConstants.QUOTE);
        String column = getArgument(DataProcessConstants.COLUMN);
        if (column != null) {
            if ("1".equals(column) || "true".equalsIgnoreCase(column)) {
                column = "True";
            }
            else {
                column = "false";
            }
            bColumn = Boolean.valueOf(column);
        }
        CallableStatement callStmt = getConnection().prepareCall(buildStoredProcQuery());
        try {
            configureStatement(callStmt);
            ResultSet rs = callStmt.executeQuery();
            try {
                return CommonUtils.resultSetToStringFormat(rs,
                                                           separator == null ? DEFAULT_SEPARATOR : separator,
                                                           quote == null ? DEFAULT_QUOTE : quote,
                                                           bColumn == null ? false : bColumn);
            }
            finally {
                rs.close();
            }
        }
        finally {
            callStmt.close();
        }
    }


    public static StoredProcTreatmentWithResult create(Connection con,
                                                       TreatmentModel treatmentModel,
                                                       int repositoryId,
                                                       ExecutionListModel executionListModel) {
        StoredProcTreatmentWithResult treatment = new StoredProcTreatmentWithResult();
        treatment.setConnection(con);
        treatment.setTreatmentModel(treatmentModel);
        treatment.setExecutionListModel(executionListModel);
        treatment.setRepositoryId(repositoryId);
        treatment.buildArgument();
        return treatment;
    }
}
