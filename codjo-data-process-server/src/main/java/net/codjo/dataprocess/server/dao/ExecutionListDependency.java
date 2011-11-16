/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 *
 */
public class ExecutionListDependency {
    private boolean isCycle;


    ExecutionListDependency() {
    }


    private void findDep(Connection con, int repositoryId,
                         String executionListPrinc,
                         List<String> executionListDep) throws SQLException {
        List<String> idDepList = new ArrayList<String>();
        PreparedStatement pstmt = con.prepareStatement(
              "select EXECUTION_LIST_ID_DEP from PM_DEPENDENCY where EXECUTION_LIST_ID_PRINC = ? "
              + " and REPOSITORY_ID = ? order by EXECUTION_LIST_ID_DEP");
        try {
            pstmt.setString(1, executionListPrinc);
            pstmt.setInt(2, repositoryId);
            ResultSet rs = pstmt.executeQuery();
            try {
                while (rs.next()) {
                    idDepList.add(rs.getString("EXECUTION_LIST_ID_DEP"));
                }
            }
            finally {
                rs.close();
            }
        }
        finally {
            pstmt.close();
        }
        for (String idDep : idDepList) {
            if (!executionListDep.contains(idDep)) {
                executionListDep.add(idDep);
                findDep(con, repositoryId, idDep, executionListDep);
            }
            else {
                isCycle = true;
            }
        }
    }


    private void findDepReverse(Connection con, int repositoryId,
                                String executionListDep,
                                List<String> executionListPrinc) throws SQLException {
        List<String> idPrincList = new ArrayList<String>();
        PreparedStatement pstmt = con.prepareStatement(
              "select EXECUTION_LIST_ID_PRINC from PM_DEPENDENCY where EXECUTION_LIST_ID_DEP = ? "
              + " and REPOSITORY_ID = ? order by EXECUTION_LIST_ID_PRINC");
        try {
            pstmt.setString(1, executionListDep);
            pstmt.setInt(2, repositoryId);
            ResultSet rs = pstmt.executeQuery();
            try {
                while (rs.next()) {
                    idPrincList.add(rs.getString("EXECUTION_LIST_ID_PRINC"));
                }
            }
            finally {
                rs.close();
            }
        }
        finally {
            pstmt.close();
        }
        for (String idPrinc : idPrincList) {
            if (!executionListPrinc.contains(idPrinc)) {
                executionListPrinc.add(idPrinc);
                findDepReverse(con, repositoryId, idPrinc, executionListPrinc);
            }
            else {
                isCycle = true;
            }
        }
    }


    public DependencyResult findImplication(Connection con, int repositoryId,
                                            String executionListPrinc) throws SQLException {
        isCycle = false;
        List<String> executionListDep = new ArrayList<String>();
        findDep(con, repositoryId, executionListPrinc, executionListDep);
        executionListDep.remove(executionListPrinc);

        return new DependencyResult(isCycle, executionListDep);
    }


    public DependencyResult findDependency(Connection con, int repositoryId, String executionListDep)
          throws SQLException {
        isCycle = false;
        List<String> executionListPrinc = new ArrayList<String>();
        findDepReverse(con, repositoryId, executionListDep, executionListPrinc);
        executionListPrinc.remove(executionListDep);

        return new DependencyResult(isCycle, executionListPrinc);
    }


    public boolean isDependOf(Connection con,
                              int repositoryId,
                              String executionListDepId,
                              String executionListPrincId)
          throws SQLException {
        DependencyResult dependencyResult = findImplication(con, repositoryId, executionListPrincId);
        List<String> executionListDep = dependencyResult.getExecutionList();
        return executionListDep.contains(executionListDepId);
    }


    public static class DependencyResult {
        private boolean isCycle;
        private List<String> executionList;


        DependencyResult(boolean isCycle, List<String> executionList) {
            this.isCycle = isCycle;
            this.executionList = executionList;
        }


        public boolean isCycle() {
            return isCycle;
        }


        public List<String> getExecutionList() {
            return executionList;
        }
    }
}
