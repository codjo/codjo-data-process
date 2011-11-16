package net.codjo.dataprocess.server.handlerhelper;
import net.codjo.dataprocess.server.dao.DependencyDao;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
public class DependencyHandlerHelper {
    private DependencyHandlerHelper() {
    }


    public static String findImplication(Connection con, int repositoryId, String executionListPrinc)
          throws SQLException {
        DependencyDao dependencyDao = new DependencyDao();
        return dependencyDao.findImplication(con, repositoryId, executionListPrinc);
    }


    public static String findDependency(Connection con, int repositoryId, String executionListDep)
          throws SQLException {
        DependencyDao dependencyDao = new DependencyDao();
        return dependencyDao.findDependency(con, repositoryId, executionListDep);
    }


    public static void updateImplication(Connection con, int repositoryId,
                                         String executionListIdPrinc, int status) throws SQLException {
        DependencyDao dependencyDao = new DependencyDao();
        dependencyDao.updateImplication(con, repositoryId, executionListIdPrinc, status);
    }


    public static String isExecutable(Connection con, int repositoryId, String executionListDep)
          throws SQLException {
        DependencyDao dependencyDao = new DependencyDao();
        return dependencyDao.isExecutable(con, repositoryId, executionListDep);
    }


    public static void deleteDependencyPrincOrDep(Connection con, int repositoryId, String executionListName)
          throws SQLException {
        DependencyDao dependencyDao = new DependencyDao();
        dependencyDao.deleteDependencyPrincOrDep(con, repositoryId, executionListName);
    }


    public static void deleteDependency(Connection con, int repositoryId, String executionListIdPrinc,
                                        String executionListIdDep)
          throws SQLException {
        DependencyDao dependencyDao = new DependencyDao();
        dependencyDao.deleteDependency(con, repositoryId, executionListIdPrinc, executionListIdDep);
    }


    public static void insertDependency(Connection con, int repositoryId,
                                        String executionListIdPrinc, String executionListIdDep)
          throws SQLException {
        DependencyDao dependencyDao = new DependencyDao();
        dependencyDao.insertDependency(con, repositoryId, executionListIdPrinc, executionListIdDep);
    }
}
