package net.codjo.dataprocess.server.imports;
import net.codjo.dataprocess.common.Log;
import net.codjo.imports.common.ProcessorAdapter;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
/**
 *
 */
public class DeleteQuarantineTableProcessor extends ProcessorAdapter {
    private boolean truncate;
    private String whereClause = "";


    public DeleteQuarantineTableProcessor() {
    }


    public DeleteQuarantineTableProcessor(boolean truncate) {
        this.truncate = truncate;
    }


    public DeleteQuarantineTableProcessor(String whereClause) {
        this(false);
        this.whereClause = whereClause;
    }


    @Override
    public void preProceed(Connection con, String quarantineTableName, File file) throws SQLException {
        if (Log.isInfoEnabled()) {
            Log.info(getClass(),
                     "Exécution de : DeleteQuarantineTableProcessor.proceed(" + quarantineTableName
                     + "). Avec truncate = " + truncate);
        }
        deleteQuarantineTable(con, quarantineTableName);
        deleteQUserTable(con, quarantineTableName);
    }


    String buildDeleteQuarantineQuery(String quarantineTableName) {
        if (truncate) {
            return "truncate table " + quarantineTableName;
        }
        else {
            return " set rowcount 1000 "
                   + " while exists (select 1 from " + quarantineTableName + " " + whereClause + ")"
                   + " begin "
                   + " begin tran "
                   + "     delete from " + quarantineTableName + " " + whereClause
                   + "     if @@error > 0 "
                   + "         rollback "
                   + "     else "
                   + "         commit "
                   + " end "
                   + " set rowcount 0 ";
        }
    }


    String buildDeleteQUserQuery(String quarantineTableName) throws SQLException {
        String qUserTable = quarantineToUserQuarantineTableName(quarantineTableName);

        if (truncate) {
            return "truncate table " + qUserTable;
        }
        else {
            return " set rowcount 1000 "
                   + " while exists (select 1 from " + qUserTable + " " + whereClause + ")"
                   + " begin "
                   + " begin tran"
                   + "     delete from " + qUserTable + " " + whereClause
                   + "     if @@error > 0 "
                   + "         rollback "
                   + "     else "
                   + "         commit "
                   + " end "
                   + " set rowcount 0 ";
        }
    }


    private void deleteQuarantineTable(Connection con, String quarantineTableName) throws SQLException {
        String sql = buildDeleteQuarantineQuery(quarantineTableName);
        if (Log.isInfoEnabled()) {
            Log.info(getClass(), "Execution de : " + sql);
        }
        Statement stmt = con.createStatement();
        try {
            stmt.executeUpdate(sql);
        }
        finally {
            stmt.close();
        }
    }


    private void deleteQUserTable(Connection con, String quarantineTableName) throws SQLException {
        String sql = buildDeleteQUserQuery(quarantineTableName);
        if (Log.isInfoEnabled()) {
            Log.info(getClass(), "Execution de : " + sql);
        }
        Statement stmt = con.createStatement();
        try {
            stmt.executeUpdate(sql);
        }
        finally {
            stmt.close();
        }
    }


    String quarantineToFinalTableName(String quarantineTableName) {
        return quarantineTableName.substring(2, quarantineTableName.length());
    }


    String quarantineToUserQuarantineTableName(String quarantineTableName) {
        return quarantineTableName.substring(0, 4) + "_USER_"
               + quarantineTableName.substring(5, quarantineTableName.length());
    }
}
