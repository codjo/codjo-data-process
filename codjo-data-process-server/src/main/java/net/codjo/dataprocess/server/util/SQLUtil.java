/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.util;
import net.codjo.dataprocess.common.Log;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
/**
 *
 */
public class SQLUtil {
    private static final String SAFE_TRUNCATE_SQL = "   set rowcount $[batchSize] \n"
                                                    + " while exists (select 1 from $[tableName]) \n"
                                                    + " begin \n"
                                                    + "      begin tran\n"
                                                    + "      delete from  $[tableName] \n"
                                                    + "      if @@error > 0 \n"
                                                    + "          rollback \n"
                                                    + "      else \n"
                                                    + "          commit \n"
                                                    + " end \n"
                                                    + " set rowcount 0";

    private static final String COUNT_ROWS_IN_TABLE_SQL = "select count(1) from $[table] $[whereClause]";


    private SQLUtil() {
    }


    private static String getSafeTruncateTableScript(String tableName, int batchSize) {
        return VarsCompiler
              .compile(SAFE_TRUNCATE_SQL, "tableName", tableName, "batchSize", String.valueOf(batchSize));
    }


    public static void truncateTable(String tableName, int batchSize, Connection connection)
          throws SQLException {
        Statement statement = connection.createStatement();
        try {
            statement.executeUpdate(getSafeTruncateTableScript(tableName, batchSize));
        }
        finally {
            statement.close();
        }
    }


    public static int executeUpdate(Connection connection, String sql) throws SQLException {
        int count;
        Statement statement = connection.createStatement();
        try {
            count = statement.executeUpdate(sql);
            if (Log.isDebugEnabled()) {
                Log.debug(SQLUtil.class, "(" + count + ") sql = " + sql);
            }
            return count;
        }
        finally {
            statement.close();
        }
    }


    public static void dropTempTable(Connection connection, String tableName) {
        try {
            connection.createStatement().executeUpdate("drop table " + tableName);
        }
        catch (SQLException ex) {
            ;
        }
    }


    static int countRowsTable(String table, String whereClause, Connection connection) throws SQLException {
        int count;
        Statement stmt = connection.createStatement();
        try {
            if (whereClause == null) {
                whereClause = "";
            }
            else {
                if (whereClause.trim().length() > 0 && !whereClause.trim().startsWith("where")) {
                    whereClause = " where " + whereClause;
                }
            }
            ResultSet rs = stmt.executeQuery(VarsCompiler.compile(COUNT_ROWS_IN_TABLE_SQL,
                                                                  "table",
                                                                  table,
                                                                  "whereClause",
                                                                  whereClause));
            try {
                rs.next();
                count = rs.getInt(1);
                if (Log.isDebugEnabled()) {
                    Log.debug(SQLUtil.class, "In table " + table + " : " + count + " rows");
                }
                return count;
            }
            finally {
                rs.close();
            }
        }
        finally {
            stmt.close();
        }
    }


    public static List<String> getTableFields(Connection con, String tableName) throws SQLException {
        List<String> tableList = new ArrayList<String>();

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select * from " + tableName + " where 1=0");
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                tableList.add(tableName + "." + rsmd.getColumnName(i));
            }
        }
        finally {
            rs.close();
            stmt.close();
        }
        return tableList;
    }


    public static void spoolTable(Connection con, String table) {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from " + table);
            ResultSetMetaData rsmd = rs.getMetaData();

            for (int col = 1; col <= rsmd.getColumnCount(); col++) {
                System.out.print(rsmd.getColumnName(col) + " | \t ");
            }
            System.out.print("\n");
            System.out.println(
                  "----------------------------------------------------------------------------");

            while (rs.next()) {
                for (int col = 1; col <= rsmd.getColumnCount(); col++) {
                    System.out.print(rs.getString(col) + " | \t ");
                }
                System.out.print("\n");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static int getNextId(Connection con, String table, String identityFieldName) throws SQLException {
        Statement stmt = con.createStatement();
        try {
            ResultSet rs = stmt.executeQuery(
                  "select max(" + identityFieldName + ") as " + identityFieldName + " from " + table);
            try {
                if (rs.next()) {
                    return rs.getInt(1) + 1;
                }
                else {
                    return 1;
                }
            }
            finally {
                rs.close();
            }
        }
        finally {
            stmt.close();
        }
    }
}
