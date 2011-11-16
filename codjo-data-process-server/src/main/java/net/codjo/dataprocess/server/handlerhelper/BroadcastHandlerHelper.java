package net.codjo.dataprocess.server.handlerhelper;
import net.codjo.dataprocess.server.dao.BroadcastDao;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
/**
 *
 */
public class BroadcastHandlerHelper {
    private BroadcastHandlerHelper() {
    }


    public static void createExportConfigFromTemplate(Connection con,
                                                      String periode,
                                                      List<String> templateDelete,
                                                      List<String> templateSelect)
          throws SQLException {
        BroadcastDao broadcastDao = new BroadcastDao();
        broadcastDao.createExportConfigFromTemplate(con, periode, templateDelete, templateSelect);
    }
}
