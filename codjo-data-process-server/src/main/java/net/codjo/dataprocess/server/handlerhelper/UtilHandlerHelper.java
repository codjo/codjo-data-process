package net.codjo.dataprocess.server.handlerhelper;
import net.codjo.dataprocess.server.dao.UtilDao;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
public class UtilHandlerHelper {
    private UtilHandlerHelper() {
    }


    public static boolean executeSql(Connection con, String query) throws SQLException {
        UtilDao utilDao = new UtilDao();
        return utilDao.executeSql(con, query);
    }


    public static String exportSqlQueryToStringFormat(Connection con, String sql, String separator,
                                                      String quote, Boolean column)
          throws SQLException {
        UtilDao utilDao = new UtilDao();
        return utilDao.exportSqlQueryToStringFormat(con, sql, separator, quote, column);
    }
}
