package net.codjo.dataprocess.server.dao;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.context.DataProcessContextCodec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
/**
 *
 */
public class ContextDao {

    public String saveContext(Connection con, String contextName, String contextAsString)
          throws SQLException {
        con.setAutoCommit(false);
        DataProcessContext context = DataProcessContextCodec.decode(contextAsString);
        PreparedStatement deletePstmt = con.prepareStatement("delete PM_DP_CONTEXT where CONTEXT_NAME = ?");
        try {
            boolean error = false;
            deletePstmt.setString(1, contextName);
            deletePstmt.executeUpdate();
            for (Iterator<String> iterator = context.keySet().iterator(); iterator.hasNext() && !error;) {
                String key = iterator.next();
                String value = context.getProperty(key);
                try {
                    saveContextNameKeyValue(con, contextName, key, value, false);
                }
                catch (SQLException e) {
                    error = true;
                    Log.error(ContextDao.class, e);
                }
            }
            con.commit();
            if (error) {
                return DataProcessConstants.CONTEXT_SAVE_FAILED;
            }
            else {
                return DataProcessConstants.TRT_OK;
            }
        }
        catch (SQLException ex) {
            con.rollback();
            Log.error(ContextDao.class, "Rollback effectué", ex);
            throw ex;
        }
        finally {
            deletePstmt.close();
            con.setAutoCommit(true);
        }
    }


    public void saveContextNameKeyValue(Connection con,
                                        String contextName,
                                        String contextKey,
                                        String contextValue,
                                        boolean deleteBefore) throws SQLException {
        String deleteSql = " delete from PM_DP_CONTEXT where CONTEXT_NAME = ? and CONTEXT_KEY = ? ";
        String sql = " insert into PM_DP_CONTEXT (CONTEXT_NAME, CONTEXT_KEY, CONTEXT_VALUE) values (?,?,?) ";
        if (deleteBefore) {
            sql = deleteSql + sql;
        }
        PreparedStatement pstmt = con.prepareStatement(sql);
        try {
            int idx = 1;
            if (deleteBefore) {
                pstmt.setString(idx++, contextName);
                pstmt.setString(idx++, contextKey);
            }
            pstmt.setString(idx++, contextName);
            pstmt.setString(idx++, contextKey);
            pstmt.setString(idx, contextValue);
            pstmt.executeUpdate();
        }
        finally {
            pstmt.close();
        }
    }


    public DataProcessContext getDataProcessContext(Connection con, String contextName) throws SQLException {
        PreparedStatement pstmt = con.prepareStatement(
              " select CONTEXT_KEY, CONTEXT_VALUE from PM_DP_CONTEXT where CONTEXT_NAME = ? "
              + " order by CONTEXT_KEY, CONTEXT_VALUE");
        try {
            pstmt.setString(1, contextName);
            ResultSet rs = pstmt.executeQuery();
            try {
                DataProcessContext context = new DataProcessContext();
                while (rs.next()) {
                    context.setProperty(rs.getString("CONTEXT_KEY"), rs.getString("CONTEXT_VALUE"));
                }
                return context;
            }
            finally {
                rs.close();
            }
        }
        finally {
            pstmt.close();
        }
    }
}
