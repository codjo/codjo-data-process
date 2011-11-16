package net.codjo.dataprocess.server.dao;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.util.CommonUtils;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
/**
 *
 */
public class UtilDao {

    public boolean executeSql(Connection con, String query) throws SQLException {
        Statement stmt = con.createStatement();
        try {
            return stmt.execute(query);
        }
        finally {
            stmt.close();
        }
    }


    public void dropTables(Connection con, List<String> tempTables) throws SQLException {
        Statement stmt = con.createStatement();
        try {
            for (String tempTable : tempTables) {
                try {
                    stmt.executeUpdate("drop table " + tempTable);
                }
                catch (SQLException ex) {
                    Log.warn(getClass(), ex);
                }
            }
        }
        finally {
            stmt.close();
        }
    }


    /**
     * Retourne une chaine contenant le resultat renvoyé par une requête sql <code>sql</code>
     *
     * @param column : indique si on met en entête les noms de colonnes
     *
     * @return le résultat de la requête
     */
    public String exportSqlQueryToStringFormat(Connection con, String sql,
                                               String separator,
                                               String quote,
                                               Boolean column) throws SQLException {
        Statement stmt = con.createStatement();
        try {
            ResultSet rs = stmt.executeQuery(sql);
            try {
                return CommonUtils.resultSetToStringFormat(rs, separator, quote, column);
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
