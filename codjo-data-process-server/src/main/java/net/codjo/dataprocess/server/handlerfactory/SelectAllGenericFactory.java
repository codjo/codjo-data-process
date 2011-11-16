package net.codjo.dataprocess.server.handlerfactory;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.mad.server.handler.HandlerException;
import net.codjo.mad.server.handler.sql.QueryBuilder;
import net.codjo.mad.server.handler.sql.SqlHandler;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import org.apache.log4j.Logger;
/**
 *
 */
public class SelectAllGenericFactory implements QueryBuilder {
    private static final Logger LOG = Logger.getLogger(SelectAllGenericFactory.class);


    public String buildQuery(Map args, SqlHandler sqlHandler) throws HandlerException {
        Connection con = null;
        try {
            con = sqlHandler.getConnection();
            return proceed(con, args);
        }
        catch (Exception ex) {
            LOG.error(ex);
        }
        finally {
            try {
                if (con != null) {
                    con.close();
                }
            }
            catch (Exception ex) {
                LOG.error(ex);
            }
        }
        return "";
    }


    String proceed(Connection con, Map<String, String> args) throws SQLException {
        StringBuilder fields = new StringBuilder();
        String tableName = getAndExtract(args, DataProcessConstants.TABLE_NAME_KEY);
        String whereClause = getAndExtract(args, DataProcessConstants.WHERE_CLAUSE_KEY);
        if (tableName == null) {
            throw new IllegalArgumentException("'" + DataProcessConstants.TABLE_NAME_KEY + "'"
                                               + " n'a pas été trouvé dans la map d'arguments !");
        }
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select * from " + tableName + " where 1=0");
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                fields.append(rsmd.getColumnName(i));
                if (i < columnCount) {
                    fields.append(", ");
                }
            }
        }
        finally {
            rs.close();
            stmt.close();
        }

        StringBuilder sbSelect = new StringBuilder("Select ");
        sbSelect.append(fields).append(" from ").append(tableName);
        if (whereClause != null && whereClause.trim().length() != 0) {
            sbSelect.append(" where ").append(whereClause);
        }
        return sbSelect.toString();
    }


    private static String getAndExtract(Map<String, String> args, String key) {
        String value = args.get(key);
        args.remove(key);
        return value;
    }
}