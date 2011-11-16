package net.codjo.dataprocess.server.handlerhelper;
import net.codjo.dataprocess.server.dao.UserManagerDao;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
public class UserManagerHandlerHelper {
    private UserManagerHandlerHelper() {
    }


    public static String manageUser(Connection con, String command, String userName, String userParam)
          throws SQLException {
        UserManagerDao userManagerDao = new UserManagerDao(con);
        return userManagerDao.manageUser(command, userName, userParam);
    }
}
