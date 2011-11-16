package net.codjo.dataprocess.server.util;
import net.codjo.dataprocess.common.Log;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
/**
 *
 */
public class SQLUtilCopyTable {

    private static final String METADATA_SQL = "select * from $[table]  where 1=0";
    private static final String BURST_INSERT_SQL = "   begin tran\n\n"
                                                   + " set rowcount 0 \n\n"
                                                   + " insert into $[destinationTable] ( $[fieldsDestList] ) \n"
                                                   + "     select $[fieldsSelectList]  \n"
                                                   + "     from $[sourceTable]  SRC \n"
                                                   + "     inner join $[tmpTableName] TMP \n"
                                                   + "     on SRC.$[pkFieldName]=TMP.$[tmpPKName] \n"
                                                   + "     where TMP.FLAG = 1 \n"
                                                   + " commit \n";

    private static final String INSERT_INTO_T_TRANSFER =
          " insert into T_TRANSFER(OPERATION_KEY,SOURCE_ID,FLAG) "
          + " select $[key],$[pkFieldName],0  from $[sourceTable] "
          + "   $[whereClause]";

    private static final String FLAG_LINES_TO_COPY =
          "   set rowcount $[batchSize] \n "
          + " begin tran \n "
          + "  update T_TRANSFER set FLAG = 1 where OPERATION_KEY = $[key] \n "
          + " commit \n"
          + " set rowcount 0 \n";

    private static final String DELETE_LINES_COPIED =
          "  begin tran \n "
          + "  delete from T_TRANSFER where FLAG = 1 and OPERATION_KEY = $[key] \n"
          + "commit \n";

    private static final String CLEAN_T_TRANSFER =
          "  begin tran \n "
          + "  delete from T_TRANSFER where OPERATION_KEY = $[key] \n"
          + "commit \n";


    private SQLUtilCopyTable() {
    }


    static List<String> determineCommonFields(String sourceTable, String destinationTable,
                                              Connection connection) throws SQLException {
        List<String> srcFields = listColumnNamesOfATable(connection, sourceTable);
        List<String> destFields = listColumnNamesOfATable(connection, destinationTable);
        destFields.retainAll(srcFields);
        return destFields;
    }


    static List<String> listColumnNamesOfATable(Connection connection, String table)
          throws SQLException {
        List<String> fields = new ArrayList<String>();
        ResultSet columnsResultSet = connection.createStatement()
              .executeQuery(VarsCompiler.compile(METADATA_SQL, "table", table));
        ResultSetMetaData setMetaData = columnsResultSet.getMetaData();
        for (int colIndex = 1; colIndex <= setMetaData.getColumnCount(); colIndex++) {
            fields.add(setMetaData.getColumnName(colIndex));
        }
        columnsResultSet.close();
        return fields;
    }


    static String generateTransfertSqlScript(String sourceTable,
                                             String pkFieldName,
                                             String destinationTable,
                                             String tmpTableName,
                                             String tmpPKName,
                                             Connection connection,
                                             Map<String, String> treatmentOnColumn)
          throws SQLException {

        List<String> commonFields = determineCommonFields(sourceTable, destinationTable, connection);
        StringBuilder fieldsDestList = new StringBuilder();
        StringBuilder fieldsSelectList = new StringBuilder();

        for (String column : treatmentOnColumn.keySet()) {
            if (!commonFields.contains(column)) {
                commonFields.add(column);
            }
        }

        for (String field : commonFields) {
            if (fieldsDestList.length() > 0) {
                fieldsDestList.append(",\n");
                fieldsSelectList.append(",\n");
            }
            fieldsDestList.append(field);
            if (treatmentOnColumn.get(field) != null) {
                fieldsSelectList.append(treatmentOnColumn.get(field));
            }
            else {
                fieldsSelectList.append(field);
            }
        }

        Map<String, String> variables = new HashMap<String, String>();
        variables.put("destinationTable", destinationTable);
        variables.put("fieldsDestList", fieldsDestList.toString());
        variables.put("fieldsSelectList", fieldsSelectList.toString());
        variables.put("sourceTable", sourceTable);
        variables.put("tmpTableName", tmpTableName);
        variables.put("pkFieldName", pkFieldName);
        variables.put("tmpPKName", tmpPKName);

        return VarsCompiler.compile(BURST_INSERT_SQL, variables);
    }


    public static synchronized void copyTable(Connection connection,
                                              String sourceTable,
                                              String pkFieldName,
                                              String destinationTable,
                                              String whereClause,
                                              int batchSize,
                                              Map<String, String> treatmentOnColumn,
                                              boolean cleanTableTransfert,
                                              ResultCopyTable resultCopyTable) {

        if (whereClause == null) {
            whereClause = "";
        }
        else {
            if (whereClause.trim().length() > 0 && !whereClause.trim().startsWith("where")) {
                whereClause = " where " + whereClause;
            }
        }

        int operationKey = new Random().nextInt();
        resultCopyTable.setKey(operationKey);

        Map<String, String> variables = new HashMap<String, String>();
        variables.put("destinationTable", destinationTable);
        variables.put("sourceTable", sourceTable);
        variables.put("pkFieldName", pkFieldName);
        variables.put("whereClause", whereClause);
        variables.put("batchSize", Integer.toString(batchSize));
        variables.put("key", Integer.toString(operationKey));

        try {
            SQLUtil.executeUpdate(connection, VarsCompiler.compile(INSERT_INTO_T_TRANSFER, variables));

            while (SQLUtil.countRowsTable("T_TRANSFER", " OPERATION_KEY = " + operationKey, connection) > 0) {
                SQLUtil.executeUpdate(connection, VarsCompiler.compile(FLAG_LINES_TO_COPY, variables));
                SQLUtil.executeUpdate(connection,
                                      generateTransfertSqlScript(sourceTable,
                                                                 pkFieldName,
                                                                 destinationTable,
                                                                 "T_TRANSFER",
                                                                 "SOURCE_ID",
                                                                 connection,
                                                                 treatmentOnColumn));
                SQLUtil.executeUpdate(connection, VarsCompiler.compile(DELETE_LINES_COPIED, variables));
            }
        }
        catch (Exception ex) {
            resultCopyTable.setException(ex);
        }
        finally {
            if (cleanTableTransfert) {
                try {
                    SQLUtil.executeUpdate(connection, VarsCompiler.compile(CLEAN_T_TRANSFER, variables));
                    Log.debug(SQLUtilCopyTable.class, "Nettoyage de la table T_TRANSFER");
                }
                catch (SQLException ex) {
                    resultCopyTable.setException(ex);
                }
            }
        }
    }


    public static class ResultCopyTable {
        private Exception exception;
        private int key;


        public ResultCopyTable() {
        }


        public int getKey() {
            return key;
        }


        public Exception getException() {
            return exception;
        }


        public void setException(Exception exception) {
            this.exception = exception;
        }


        public void setKey(int key) {
            this.key = key;
        }
    }
}
