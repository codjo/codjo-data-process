/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.dao;
import net.codjo.dataprocess.common.exception.UserManagerException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static net.codjo.dataprocess.common.DataProcessConstants.NO_RESULT;
import static net.codjo.dataprocess.common.DataProcessConstants.USER_COMMAND_CREATE;
import static net.codjo.dataprocess.common.DataProcessConstants.USER_COMMAND_IS_EXIST;
import static net.codjo.dataprocess.common.DataProcessConstants.USER_COMMAND_LOAD;
import static net.codjo.dataprocess.common.DataProcessConstants.USER_COMMAND_SAVE;
/**
 *
 */
public class UserManagerDao {
    private Connection con;


    public UserManagerDao(Connection con) {
        this.con = con;
    }


    public String manageUser(String command, String userName, String userParam) throws SQLException {
        try {
            if (USER_COMMAND_IS_EXIST.equalsIgnoreCase(command)) {
                boolean isExist = isExist(userName);
                if (isExist) {
                    return "TRUE";
                }
                else {
                    return "FALSE";
                }
            }
            else if (USER_COMMAND_LOAD.equalsIgnoreCase(command)) {
                return loadUser(userName);
            }
            else if (USER_COMMAND_SAVE.equalsIgnoreCase(command)) {
                updateUser(userName, userParam);
                return NO_RESULT;
            }
            else if (USER_COMMAND_CREATE.equalsIgnoreCase(command)) {
                createUser(userName, userParam);
                return NO_RESULT;
            }
            else {
                return "Commande inconnue.";
            }
        }
        catch (UserManagerException ex) {
            return "ERROR : " + ex.getLocalizedMessage();
        }
    }


    boolean isExist(String userName) throws SQLException {
        PreparedStatement pStmt = con.prepareStatement("select USER_ID from PM_DP_USER where USER_NAME = ?");
        try {
            pStmt.setString(1, userName);
            ResultSet rs = pStmt.executeQuery();
            try {
                return rs.next();
            }
            finally {
                rs.close();
            }
        }
        finally {
            pStmt.close();
        }
    }


    String loadUser(String userName) throws UserManagerException, SQLException {
        PreparedStatement pStmt =
              con.prepareStatement("select USER_PARAM from PM_DP_USER where USER_NAME = ?");
        try {
            pStmt.setString(1, userName);
            ResultSet rs = pStmt.executeQuery();
            try {
                if (rs.next()) {
                    return rs.getString("USER_PARAM");
                }
                else {
                    throw new UserManagerException(
                          "L'utilisateur '" + userName + "' est introuvable dans PM_DP_USER.");
                }
            }
            finally {
                rs.close();
            }
        }
        finally {
            pStmt.close();
        }
    }


    void updateUser(String userName, String userParam) throws SQLException, UserManagerException {
        if (!isExist(userName)) {
            throw new UserManagerException(
                  "L'utilisateur '" + userName + "' est introuvable dans PM_DP_USER.");
        }
        PreparedStatement pStmt =
              con.prepareStatement("update PM_DP_USER set USER_PARAM = ? where USER_NAME = ?");
        try {
            pStmt.setString(1, userParam);
            pStmt.setString(2, userName);
            pStmt.executeUpdate();
        }
        finally {
            pStmt.close();
        }
    }


    void createUser(String userName, String userParam) throws SQLException {
        PreparedStatement pStmt =
              con.prepareStatement("insert into PM_DP_USER (USER_NAME, USER_PARAM) values (?, ?)");
        try {
            pStmt.setString(1, userName);
            pStmt.setString(2, userParam);
            pStmt.executeUpdate();
        }
        finally {
            pStmt.close();
        }
    }
}
