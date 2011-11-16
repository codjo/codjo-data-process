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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
/**
 *
 */
public class DuplicateRowFactory implements QueryBuilder {
    private static final Logger LOG = Logger.getLogger(DuplicateRowFactory.class);


    public String buildQuery(Map<String, String> args, SqlHandler sqlHandler) throws HandlerException {
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
        if (tableName == null) {
            throw new IllegalArgumentException("'" + DataProcessConstants.TABLE_NAME_KEY + "'"
                                               + " n'a pas été trouvé dans la map d'arguments !");
        }
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select * from " + tableName + " where 1=0");
        List<String> upperIdList = new ArrayList<String>();
        for (String id : args.keySet()) {
            upperIdList.add(sqlUpper(id));
        }
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                if (!upperIdList.contains(rsmd.getColumnName(i))) {
                    fields.append(rsmd.getColumnName(i));
                    if (i < columnCount) {
                        fields.append(", ");
                    }
                }
            }
        }
        finally {
            rs.close();
            stmt.close();
        }

        StringBuilder sbInsert = new StringBuilder("insert into ");
        sbInsert.append(tableName).append(" (").append(fields).append(") select ").append(fields)
              .append(" from ").append(tableName).append(" where ");

        String and = " and ";
        for (String id : upperIdList) {
            sbInsert.append(id).append(" = ?").append(and);
        }

        String result = sbInsert.toString();
        if (result.endsWith(and)) {
            result = result.substring(0, result.length() - and.length());
        }
        LOG.debug(result);
        return result;
    }


    private static String getAndExtract(Map<String, String> args, String key) {
        String value = args.get(key);
        args.remove(key);
        return value;
    }


    String sqlUpper(String fieldName) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fieldName.length(); i++) {
            if (Character.isUpperCase(fieldName.charAt(i))) {
                sb.append('_');
            }
            sb.append(fieldName.charAt(i));
        }
        return sb.toString().toUpperCase();
    }
}
