package net.codjo.dataprocess.server.handlerhelper;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.server.dao.ConfigDao;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
public class ConfigHandlerHelper {
    private ConfigHandlerHelper() {
    }


    public static String getConfigProperty(Connection con, String key)
          throws SQLException, TreatmentException {
        ConfigDao configDao = new ConfigDao();
        return configDao.getConfigProperty(con, key);
    }


    public static String getAllDefaultConfigProperty() {
        ConfigDao configDao = new ConfigDao();
        return configDao.getAllDefaultConfigProperty();
    }
}
