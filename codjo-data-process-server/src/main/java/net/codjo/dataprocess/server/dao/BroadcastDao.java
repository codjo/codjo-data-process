package net.codjo.dataprocess.server.dao;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.server.util.SQLUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
/**
 *
 */
public class BroadcastDao {
    public void createExportConfigFromTemplate(Connection con,
                                               String periode,
                                               List<String> templateDelete,
                                               List<String> templateSelect)
          throws SQLException {
        con.prepareStatement(createDeleteSql(templateDelete)).executeUpdate();
        String sql = createSelectSql(templateSelect);
        ResultSet rs = con.prepareStatement(sql).executeQuery();
        try {
            while (rs.next()) {
                int fileId = rs.getInt("FILE_ID");
                String fileName = rs.getString("FILE_NAME").replace("$periode$", periode);
                PreparedStatement pstmt = con.prepareStatement(
                      "insert into PM_BROADCAST_FILES (FILE_ID, FILE_NAME, "
                      + "DESTINATION_SYSTEM, FILE_DESTINATION_LOCATION, FILE_HEADER, "
                      + "FILE_HEADER_TEXT, AUTO_DISTRIBUTION, DISTRIBUTION_METHOD, "
                      + "HISTORISE_FILE, CFT_BATCH_FILE, SECTION_SEPARATOR) "
                      + "select ?, ?, DESTINATION_SYSTEM, FILE_DESTINATION_LOCATION, FILE_HEADER, "
                      + "FILE_HEADER_TEXT, AUTO_DISTRIBUTION, DISTRIBUTION_METHOD, "
                      + "HISTORISE_FILE, CFT_BATCH_FILE, SECTION_SEPARATOR "
                      + "from PM_BROADCAST_FILES "
                      + "where FILE_ID = ?");
                try {
                    int newFileId = SQLUtil.getNextId(con, "PM_BROADCAST_FILES", "FILE_ID");
                    pstmt.setInt(1, newFileId);
                    pstmt.setString(2, fileName);
                    pstmt.setInt(3, fileId);
                    pstmt.executeUpdate();

                    duplicateDataInBroadcastFileContents(con, fileId, newFileId);
                }
                finally {
                    pstmt.close();
                }
            }
        }
        finally {
            rs.close();
        }
    }


    String createSelectSql(List<String> templateSelect) {
        StringBuilder part = new StringBuilder();
        for (String str : templateSelect) {
            str = str.trim();
            if (str.startsWith("not")) {
                part.append(" FILE_NAME not like '").append(str.substring(4)).append("' ");
            }
            else {
                part.append(" FILE_NAME like '").append(str).append("' ");
            }
            if (templateSelect.size() > 1) {
                part.append(" and ");
            }
        }
        String sql = "select FILE_ID, FILE_NAME from PM_BROADCAST_FILES where ";
        sql = sql + part;
        if (templateSelect.size() > 1) {
            sql = sql.substring(0, sql.length() - " and ".length());
        }
        sql = sql + " order by FILE_ID, FILE_NAME";
        Log.debug(getClass(), sql);
        return sql;
    }


    String createDeleteSql(List<String> templateDelete) {
        StringBuilder part = new StringBuilder();
        for (String str : templateDelete) {
            str = str.trim();
            if (str.startsWith("not")) {
                part.append(" FILE_NAME not like '").append(str.substring(4)).append("' ");
            }
            else {
                part.append(" FILE_NAME like '").append(str).append("' ");
            }
            if (templateDelete.size() > 1) {
                part.append(" and ");
            }
        }
        String sql = "delete from PM_BROADCAST_FILES where ";
        sql = sql + part;
        if (templateDelete.size() > 1) {
            sql = sql.substring(0, sql.length() - " and ".length());
        }
        Log.debug(getClass(), sql);
        return sql;
    }


    private static void duplicateDataInBroadcastFileContents(Connection con, int fileId, int newFileId)
          throws SQLException {
        PreparedStatement pstmt = con.prepareStatement(
              "select SECTION_ID, SECTION_POSITION, SECTION_HEADER, SECTION_HEADER_TEXT, COLUMN_SEPARATOR, COLUMN_HEADER "
              + " from PM_BROADCAST_FILE_CONTENTS "
              + " where FILE_ID = ?");
        try {
            pstmt.setInt(1, fileId);
            ResultSet rs = pstmt.executeQuery();
            try {
                PreparedStatement pstmt2 = con.prepareStatement("insert into PM_BROADCAST_FILE_CONTENTS "
                                                                + " (CONTENT_ID, FILE_ID, SECTION_ID, SECTION_POSITION, SECTION_HEADER, SECTION_HEADER_TEXT, COLUMN_SEPARATOR, COLUMN_HEADER)"
                                                                + " values (?, ?, ?, ?, ?, ?, ?, ?)");
                try {
                    while (rs.next()) {
                        pstmt2.setInt(1, SQLUtil.getNextId(con, "PM_BROADCAST_FILE_CONTENTS", "CONTENT_ID"));
                        pstmt2.setInt(2, newFileId);
                        pstmt2.setInt(3, rs.getInt("SECTION_ID"));
                        pstmt2.setInt(4, rs.getInt("SECTION_POSITION"));
                        pstmt2.setBoolean(5, rs.getBoolean("SECTION_HEADER"));
                        pstmt2.setString(6, rs.getString("SECTION_HEADER_TEXT"));
                        pstmt2.setString(7, rs.getString("COLUMN_SEPARATOR"));
                        pstmt2.setBoolean(8, rs.getBoolean("COLUMN_HEADER"));
                        pstmt2.executeUpdate();
                    }
                }
                finally {
                    pstmt2.close();
                }
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
