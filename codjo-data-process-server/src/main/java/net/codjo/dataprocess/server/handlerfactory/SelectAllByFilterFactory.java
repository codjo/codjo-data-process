package net.codjo.dataprocess.server.handlerfactory;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.mad.server.handler.HandlerException;
import net.codjo.mad.server.handler.sql.QueryBuilder;
import net.codjo.mad.server.handler.sql.SqlHandler;
import net.codjo.util.string.StringUtil;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
/**
 *
 */
public class SelectAllByFilterFactory implements QueryBuilder {
    private static final Logger LOG = Logger.getLogger(SelectAllByFilterFactory.class);


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
            args.clear();
        }
        else {
            removeNullValue(args);
            if (!args.isEmpty()) {
                sbSelect.append(" where ");
                List<String> list = new ArrayList<String>();
                for (Entry entry : args.entrySet()) {
                    list.add(entry.getKey().toString());
                }
                Collections.sort(list);
                int size = list.size();
                for (String field : list) {
                    sbSelect.append(StringUtil.javaToSqlName(field)).append(" = ?");
                    size--;
                    if (size != 0) {
                        sbSelect.append(" and ");
                    }
                }
            }
        }
        return sbSelect.toString();
    }


    private static void removeNullValue(Map<String, String> args) {
        Map<String, String> argsWithoutNull = new HashMap<String, String>();
        for (Entry<String, String> entry : args.entrySet()) {
            if (!"null".equals(entry.getValue())) {
                argsWithoutNull.put(entry.getKey(), entry.getValue());
            }
        }
        args.clear();
        args.putAll(argsWithoutNull);
    }


    private static String getAndExtract(Map<String, String> args, String key) {
        String value = args.get(key);
        args.remove(key);
        return value;
    }
}