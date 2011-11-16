package net.codjo.dataprocess.server.dao;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.codec.ListCodec;
import net.codjo.dataprocess.common.util.CommonUtils;
import net.codjo.dataprocess.server.dao.ExecutionListDependency.DependencyResult;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 *
 */
public class DependencyDao {
    public String findImplication(Connection con, int repositoryId, String executionListPrinc)
          throws SQLException {
        DependencyResult dependencyResult =
              new ExecutionListDependency().findImplication(con, repositoryId, executionListPrinc);
        List<String> executionListDep = dependencyResult.getExecutionList();

        String result = "";
        if (!executionListDep.isEmpty()) {
            result = new ListCodec().encode(executionListDep, "", ",");
            result = result + ':' + dependencyResult.isCycle();
        }
        return result;
    }


    public String findDependency(Connection con, int repositoryId, String executionListDep)
          throws SQLException {
        DependencyResult dependencyResult =
              new ExecutionListDependency().findDependency(con, repositoryId, executionListDep);
        List<String> executionListPrinc = dependencyResult.getExecutionList();

        String result = "";
        if (!executionListPrinc.isEmpty()) {
            result = new ListCodec().encode(executionListPrinc, "", ",");
            result = result + ':' + dependencyResult.isCycle();
        }
        return result;
    }


    public void updateImplication(Connection con, int repositoryId, String executionListIdPrinc, int status)
          throws SQLException {
        DependencyResult dependencyResult =
              new ExecutionListDependency().findImplication(con, repositoryId, executionListIdPrinc);
        List<String> executionListDep = dependencyResult.getExecutionList();

        if (!executionListDep.isEmpty()) {
            List<String> executionListDepTemp = new ArrayList<String>(executionListDep.size());
            for (String dep : executionListDep) {
                executionListDepTemp.add(CommonUtils.doubleQuote(dep));
            }
            String inset = new ListCodec().encode(executionListDepTemp);
            String sql =
                  " update PM_EXECUTION_LIST_STATUS set PM_EXECUTION_LIST_STATUS.STATUS = ? "
                  + " from PM_EXECUTION_LIST inner join PM_EXECUTION_LIST_STATUS "
                  + " on PM_EXECUTION_LIST.EXECUTION_LIST_ID "
                  + " = PM_EXECUTION_LIST_STATUS.EXECUTION_LIST_ID "
                  + " where PM_EXECUTION_LIST.REPOSITORY_ID = ? "
                  + " and PM_EXECUTION_LIST.EXECUTION_LIST_NAME in (";
            sql = sql + inset + ')';
            PreparedStatement pstmt = con.prepareStatement(sql);
            try {
                pstmt.setInt(1, status);
                pstmt.setInt(2, repositoryId);
                pstmt.executeUpdate();
            }
            finally {
                pstmt.close();
            }
        }
    }


    public String isExecutable(Connection con, int repositoryId, String executionListDep)
          throws SQLException {
        String result = "TRUE";
        DependencyResult dependencyResult =
              new ExecutionListDependency().findDependency(con, repositoryId, executionListDep);
        List<String> executionListPrinc = dependencyResult.getExecutionList();

        if (!executionListPrinc.isEmpty()) {
            List<String> executionListPrincTemp = new ArrayList<String>(executionListPrinc.size());
            for (String princ : executionListPrinc) {
                executionListPrincTemp.add(CommonUtils.doubleQuote(princ));
            }
            String inset = new ListCodec().encode(executionListPrincTemp);
            String sql =
                  " select PM_EXECUTION_LIST.EXECUTION_LIST_NAME from PM_EXECUTION_LIST "
                  + " inner join PM_EXECUTION_LIST_STATUS "
                  + " on PM_EXECUTION_LIST.EXECUTION_LIST_ID = PM_EXECUTION_LIST_STATUS.EXECUTION_LIST_ID "
                  + " where (PM_EXECUTION_LIST_STATUS.STATUS = ? or PM_EXECUTION_LIST_STATUS.STATUS = ? or PM_EXECUTION_LIST_STATUS.STATUS = ?) "
                  + " and PM_EXECUTION_LIST.REPOSITORY_ID = ? "
                  + " and PM_EXECUTION_LIST.EXECUTION_LIST_NAME in (";
            sql = sql + inset + ") order by PM_EXECUTION_LIST.EXECUTION_LIST_NAME";
            PreparedStatement pstmt = con.prepareStatement(sql);
            try {
                pstmt.setInt(1, DataProcessConstants.TO_DO);
                pstmt.setInt(2, DataProcessConstants.FAILED);
                pstmt.setInt(3, DataProcessConstants.FAILED_DEPENDENCY);
                pstmt.setInt(4, repositoryId);
                ResultSet rs = pstmt.executeQuery();
                try {
                    if (rs.next()) {
                        return "FALSE";
                    }
                }
                finally {
                    rs.close();
                }
            }
            finally {
                pstmt.close();
            }
        }
        return result;
    }


    public void deleteDependencyPrincOrDep(Connection con, int repositoryId, String executionListName)
          throws SQLException {
        PreparedStatement pstmt = con.prepareStatement(
              "delete PM_DEPENDENCY where (PM_DEPENDENCY.EXECUTION_LIST_ID_PRINC = ? "
              + " or PM_DEPENDENCY.EXECUTION_LIST_ID_DEP = ?) and PM_DEPENDENCY.REPOSITORY_ID = ?");
        try {
            pstmt.setString(1, executionListName);
            pstmt.setString(2, executionListName);
            pstmt.setInt(3, repositoryId);
            pstmt.executeUpdate();
        }
        finally {
            pstmt.close();
        }
    }


    public void deleteDependency(Connection con, int repositoryId,
                                 String executionListIdPrinc,
                                 String executionListIdDep)
          throws SQLException {
        PreparedStatement pstmt = con.prepareStatement(
              "delete PM_DEPENDENCY where PM_DEPENDENCY.EXECUTION_LIST_ID_PRINC = ? "
              + " and PM_DEPENDENCY.EXECUTION_LIST_ID_DEP = ? and PM_DEPENDENCY.REPOSITORY_ID = ?");
        try {
            pstmt.setString(1, executionListIdPrinc);
            pstmt.setString(2, executionListIdDep);
            pstmt.setInt(3, repositoryId);
            pstmt.executeUpdate();
        }
        finally {
            pstmt.close();
        }
    }


    public void insertDependency(Connection con, int repositoryId,
                                 String executionListIdPrinc,
                                 String executionListIdDep)
          throws SQLException {
        PreparedStatement pstmt = con.prepareStatement(
              "insert into PM_DEPENDENCY (EXECUTION_LIST_ID_PRINC, EXECUTION_LIST_ID_DEP, REPOSITORY_ID) "
              + " values (?, ?, ?)");
        try {
            pstmt.setString(1, executionListIdPrinc);
            pstmt.setString(2, executionListIdDep);
            pstmt.setInt(3, repositoryId);
            pstmt.executeUpdate();
        }
        finally {
            pstmt.close();
        }
    }
}
