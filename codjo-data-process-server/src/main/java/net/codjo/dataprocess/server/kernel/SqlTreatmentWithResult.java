/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.kernel;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.common.util.CommonUtils;
import net.codjo.dataprocess.server.dao.UtilDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 *
 */
class SqlTreatmentWithResult extends AbstractSqlTreatment {
    private static final String DEFAULT_SEPARATOR = ", ";
    private static final String DEFAULT_QUOTE = "\"";


    public Object proceedTreatment(DataProcessContext context, Object... param)
          throws TreatmentException, SQLException {
        return proceedSqlQueryTypeWithResult();
    }


    private String proceedSqlQueryTypeWithResult() throws TreatmentException, SQLException {
        String target = getTreatmentModel().getTarget();
        if (target.trim().length() == 0) {
            return "";
        }
        Boolean bColumn = null;
        String separator = getArgument(DataProcessConstants.SEPARATOR);
        String quote = getArgument(DataProcessConstants.QUOTE);
        String column = getArgument(DataProcessConstants.COLUMN);
        if (column != null) {
            if ("1".equals(column) || "true".equalsIgnoreCase(column)) {
                column = "true";
            }
            else {
                column = "false";
            }
            bColumn = Boolean.valueOf(column);
        }
        PreparedStatement pStmt = getConnection().prepareStatement(target);
        try {
            configureStatement(pStmt);
            ResultSet rs = pStmt.executeQuery();
            try {
                String result = CommonUtils.resultSetToStringFormat(rs,
                                                                    separator == null ?
                                                                    DEFAULT_SEPARATOR :
                                                                    separator,
                                                                    quote == null ? DEFAULT_QUOTE : quote,
                                                                    bColumn == null ? false : bColumn);
                try {
                    UtilDao utilDao = new UtilDao();
                    utilDao.dropTables(getConnection(), getTemporaryTables(target));
                }
                catch (SQLException ex) {
                    Log.error(getClass(), ex);
                }
                return result;
            }
            finally {
                rs.close();
            }
        }
        finally {
            pStmt.close();
        }
    }


    List<String> getTemporaryTables(String query) {
        int idx = 0;
        List<String> tempTables = new ArrayList<String>();
        while (idx < query.length()) {
            if (query.charAt(idx) == '\'') {
                idx++;
                while (idx < query.length() && query.charAt(idx) != '\'') {
                    idx++;
                }
            }
            if (query.charAt(idx) == '#') {
                StringBuilder tableTemp = new StringBuilder();
                while (idx < query.length()
                       && query.charAt(idx) != ' '
                       && query.charAt(idx) != '.'
                       && query.charAt(idx) != ','
                       && query.charAt(idx) != ')') {
                    tableTemp.append(query.charAt(idx));
                    idx++;
                }

                if (!tempTables.contains(tableTemp.toString())) {
                    tempTables.add(tableTemp.toString());
                }
            }
            idx++;
        }
        return tempTables;
    }


    public static SqlTreatmentWithResult create(Connection con,
                                                TreatmentModel treatmentModel,
                                                int repositoryId,
                                                ExecutionListModel executionListModel) {
        SqlTreatmentWithResult treatment = new SqlTreatmentWithResult();
        treatment.setConnection(con);
        treatment.setTreatmentModel(treatmentModel);
        treatment.setExecutionListModel(executionListModel);
        treatment.setRepositoryId(repositoryId);
        treatment.buildArgument();
        return treatment;
    }
}
