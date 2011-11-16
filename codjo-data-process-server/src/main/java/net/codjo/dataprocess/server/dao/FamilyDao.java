package net.codjo.dataprocess.server.dao;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.server.util.SQLUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
/**
 *
 */
public class FamilyDao {

    public String newFamily(Connection con, int repositoryId, String familyName) throws SQLException {
        PreparedStatement pstmt = con.prepareStatement(
              "select FAMILY_ID from PM_FAMILY where FAMILY_NAME = ? and REPOSITORY_ID = ?");
        try {
            pstmt.setString(1, familyName);
            pstmt.setInt(2, repositoryId);
            ResultSet rs = pstmt.executeQuery();
            try {
                if (rs.next()) {
                    return DataProcessConstants.FAMILY_ALREADY_EXISTS;
                }
            }
            finally {
                rs.close();
            }
            int nextFamilyId = SQLUtil.getNextId(con, "PM_FAMILY", "FAMILY_ID");
            pstmt = con.prepareStatement(
                  "insert into PM_FAMILY (FAMILY_ID, REPOSITORY_ID, FAMILY_NAME) values (?, ?, ?)");
            pstmt.setInt(1, nextFamilyId);
            pstmt.setInt(2, repositoryId);
            pstmt.setString(3, familyName);
            pstmt.executeUpdate();

            return Integer.toString(nextFamilyId);
        }
        finally {
            pstmt.close();
        }
    }


    public int getFamilyIdFromName(Connection con, int repositoryId, String familyName)
          throws SQLException, TreatmentException {
        PreparedStatement stmt = con.prepareStatement("select FAMILY_ID from PM_FAMILY "
                                                      + " where FAMILY_NAME = ? and REPOSITORY_ID = ?");
        try {
            stmt.setString(1, familyName);
            stmt.setInt(2, repositoryId);
            ResultSet rs = stmt.executeQuery();
            try {
                if (rs.next()) {
                    return rs.getInt("FAMILY_ID");
                }
                else {
                    throw new TreatmentException(
                          "La famille '" + familyName + "' est inexistante dans le repository id = "
                          + repositoryId);
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


    public Map<String, String> getFamilyMap(Connection con, String repositoryName) throws SQLException {
        Map<String, String> familyMap = new HashMap<String, String>();
        PreparedStatement pstmt = con.prepareStatement(" select PM_FAMILY.FAMILY_ID, PM_FAMILY.FAMILY_NAME "
                                                       + "from PM_FAMILY "
                                                       + "inner join PM_REPOSITORY "
                                                       + "on PM_FAMILY.REPOSITORY_ID = PM_REPOSITORY.REPOSITORY_ID "
                                                       + "where PM_REPOSITORY.REPOSITORY_NAME = ? "
                                                       + "order by PM_FAMILY.FAMILY_ID, PM_FAMILY.FAMILY_NAME");
        try {
            pstmt.setString(1, repositoryName);
            ResultSet rs = pstmt.executeQuery();
            try {
                while (rs.next()) {
                    familyMap.put(rs.getString("FAMILY_ID"), rs.getString("FAMILY_NAME"));
                }
                return familyMap;
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
