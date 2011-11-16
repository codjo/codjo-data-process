package net.codjo.dataprocess.server.util;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
/**
 *
 */
public class DatabaseTools {
    static final String EXCEPTION_THROWN = "EX";
    private static final String RESULTSET = "RS";
    private static final String RESULT_COUNT = "RC";


    private DatabaseTools() {
    }


    public static String getAllFieldNamesByTable(Connection connection) throws SQLException {
        String legalTables = getLegalTables(connection);
        StringBuilder columnList = new StringBuilder("");
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet columns = metaData.getColumns(null, null, null, null);
        try {
            while (columns.next()) {
                String tableName = columns.getString("TABLE_NAME");
                if (legalTables.contains("," + tableName + ",")) {
                    if (columnList.length() > 0) {
                        columnList.append(",");
                    }
                    columnList.append(tableName).append(".").append(columns.getString("COLUMN_NAME"));
                }
            }
        }
        finally {
            columns.close();
        }
        return columnList.toString();
    }


    private static String getLegalTables(Connection connection) throws SQLException {
        StringBuilder tableList = new StringBuilder(",");
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE", "VIEW"});
        try {
            while (tables.next()) {
                tableList.append(tables.getString("TABLE_NAME")).append(",");
            }
        }
        finally {
            tables.close();
        }
        return tableList.toString();
    }


    public static String executeQuery(String user, Connection connection, String sql, int page, int pageSize)
          throws Exception {
        String typeOfResult;
        StringBuilder result = new StringBuilder();
        sql = sql.trim();
        String logResult;
        try {
            if (isQuery(sql)) {
                Statement stmt = connection.createStatement();
                try {
                    ResultSet rs = stmt.executeQuery(sql);
                    try {
                        int count = insertResultSetToStringBuilder(rs, result, page, pageSize);
                        typeOfResult = RESULTSET;
                        logResult = String.valueOf(count) + " rows";
                    }
                    finally {
                        rs.close();
                    }
                }
                finally {
                    stmt.close();
                }
            }
            else {
                Statement stmt = connection.createStatement();
                try {
                    result.append(stmt.executeUpdate(sql));
                    typeOfResult = RESULT_COUNT;
                    logResult = result.toString();
                }
                finally {
                    stmt.close();
                }
            }
        }
        catch (SQLException ex) {
            typeOfResult = EXCEPTION_THROWN;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ex.printStackTrace(new PrintStream(byteArrayOutputStream));
            result.append(byteArrayOutputStream.toString());
            logResult = result.toString();
        }
        log(connection, user, sql, logResult);
        return typeOfResult + "\n" + result;
    }


    private static int insertResultSetToStringBuilder(ResultSet resultSet, StringBuilder result, int page,
                                                      int pageSize) throws Exception {
        int lineReaded = 0;
        try {
            int beginRow = (page - 1) * pageSize + 1;
            int endRow = page * pageSize;
            int currentRow = 0;
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();

            StringBuilder columns = new StringBuilder();
            //columns
            for (int count = 1; count <= columnCount; count++) {
                String columnName = resultSetMetaData.getColumnName(count);
                if (columnName == null || columnName.trim().length() == 0) {
                    columnName = new StringBuilder().append("Column ").append(count).toString();
                }
                columns.append(columnName).append("\t");
            }
            columns.append("\n");

            //lines
            StringBuilder rows = new StringBuilder();
            while (resultSet.next()) {
                currentRow++;
                if (currentRow >= beginRow && currentRow <= endRow) {
                    lineReaded++;
                    for (int count = 1; count <= columnCount; count++) {
                        String value = encode(resultSet.getString(count));
                        rows.append(value).append("\t");
                    }
                    rows.append("\n");
                }
            }
            StringBuilder header = new StringBuilder();
            header.append(resultSetMetaData.getColumnCount()).append("\n").append(currentRow).append("\n");
            result.append(header).append(columns).append(rows);
            return currentRow;
        }
        catch (OutOfMemoryError outOfMemoryError) {
            throw new Exception("Impossible de charger le résultat de la requête, out of memory ("
                                + lineReaded + " lignes lues)", new Exception(outOfMemoryError));
        }
    }


    private static String encode(String toEncode) {
        if (toEncode == null) {
            return "NULL";
        }
        StringBuilder buffer = new StringBuilder(toEncode);
        replaceString(buffer, "\n", "\\n");
        replaceString(buffer, "\r", "\\r");
        replaceString(buffer, "\t", "\\t");
        return buffer.toString();
    }


    private static void replaceString(StringBuilder buffer, String replaceWhat, String replaceBy) {
        while (buffer.indexOf(replaceWhat) >= 0) {
            int index = buffer.indexOf(replaceWhat);
            buffer.replace(index, index + replaceWhat.length(), replaceBy);
        }
    }


    private static boolean isQuery(String sql) {
        String toTest = sql.trim().toUpperCase();
        return toTest.startsWith("SELECT");
    }


    private static void log(Connection con, String user, String sql, String result) throws SQLException {
        PreparedStatement pStmt = con.prepareStatement(
              "insert into T_DIRECTSQL_LOG (INITIATOR,FLAG,REQUEST_DATE,SQL_REQUEST,RESULT) "
              + " values (?,' ',?,?,?)");
        try {
            pStmt.setString(1, user);
            pStmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            pStmt.setString(3, sql);
            pStmt.setString(4, result);
            pStmt.executeUpdate();
        }
        finally {
            pStmt.close();
        }
    }
}
