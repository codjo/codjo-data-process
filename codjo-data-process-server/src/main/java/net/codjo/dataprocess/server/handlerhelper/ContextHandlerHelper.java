package net.codjo.dataprocess.server.handlerhelper;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.server.dao.ContextDao;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
public class ContextHandlerHelper {
    private ContextHandlerHelper() {
    }


    public static String saveContext(Connection con, String contextName, String contextAsString)
          throws SQLException {
        ContextDao contextDao = new ContextDao();
        return contextDao.saveContext(con, contextName, contextAsString);
    }


    public static void saveContextNameKeyValue(Connection con, String contextName, String contextKey,
                                               String contextValue, boolean deleteBefore)
          throws SQLException {
        ContextDao contextDao = new ContextDao();
        contextDao.saveContextNameKeyValue(con, contextName, contextKey, contextValue, deleteBefore);
    }


    public static DataProcessContext loadDataProcessContextByContextName(Connection con, String contextName)
          throws SQLException {
        ContextDao contextDao = new ContextDao();
        return contextDao.getDataProcessContext(con, contextName);
    }
}
