package net.codjo.dataprocess.server.dao;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.util.XMLUtils;
import net.codjo.dataprocess.server.repository.Repository;
import net.codjo.dataprocess.server.util.SQLUtil;
import net.codjo.mad.server.handler.HandlerException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 *
 */
public class RepositoryDao {

    public void renameRepository(Connection con, int repositoryId, String repositoryName)
          throws SQLException, HandlerException {
        PreparedStatement pstmt = con.prepareStatement(
              "select REPOSITORY_ID from PM_REPOSITORY where REPOSITORY_NAME = ? ");
        try {
            pstmt.setString(1, repositoryName);
            ResultSet rs = pstmt.executeQuery();
            try {
                if (rs.next()) {
                    throw new HandlerException(String.format(
                          "Le renommage est impossible car le référentiel '%s' existe déjà.",
                          repositoryName));
                }
            }
            finally {
                rs.close();
            }
        }
        finally {
            pstmt.close();
        }

        pstmt = con.prepareStatement(
              "update PM_REPOSITORY set REPOSITORY_NAME = ? where REPOSITORY_ID = ? ");
        try {
            pstmt.setString(1, repositoryName);
            pstmt.setInt(2, repositoryId);
            pstmt.executeUpdate();
        }
        finally {
            pstmt.close();
        }
    }


    public void deleteRepository(Connection con, int repositoryId) throws SQLException {
        Repository.reinitializeRepositoryCache();
        PreparedStatement pstmt = con.prepareStatement(
              " delete PM_REPOSITORY_CONTENT where REPOSITORY_ID = ? "
              + " delete PM_REPOSITORY where REPOSITORY_ID = ? "
              + " delete PM_DEPENDENCY where REPOSITORY_ID = ? "
              + " delete PM_EXECUTION_LIST_STATUS from PM_EXECUTION_LIST_STATUS "
              + " inner join PM_EXECUTION_LIST "
              + " on PM_EXECUTION_LIST_STATUS.EXECUTION_LIST_ID = PM_EXECUTION_LIST.EXECUTION_LIST_ID "
              + " where PM_EXECUTION_LIST.REPOSITORY_ID = ?"
              + " delete PM_TREATMENT_STATUS from PM_TREATMENT_STATUS "
              + " inner join PM_EXECUTION_LIST "
              + " on PM_TREATMENT_STATUS.EXECUTION_LIST_ID = PM_EXECUTION_LIST.EXECUTION_LIST_ID "
              + " where PM_EXECUTION_LIST.REPOSITORY_ID = ?"
              + " delete PM_TREATMENT from PM_TREATMENT inner join PM_EXECUTION_LIST "
              + " on PM_TREATMENT.EXECUTION_LIST_ID = PM_EXECUTION_LIST.EXECUTION_LIST_ID "
              + " where PM_EXECUTION_LIST.REPOSITORY_ID = ? "
              + " delete PM_EXECUTION_LIST where REPOSITORY_ID = ? ");
        try {
            int idx = 1;
            pstmt.setInt(idx++, repositoryId);
            pstmt.setInt(idx++, repositoryId);
            pstmt.setInt(idx++, repositoryId);
            pstmt.setInt(idx++, repositoryId);
            pstmt.setInt(idx++, repositoryId);
            pstmt.setInt(idx++, repositoryId);
            pstmt.setInt(idx, repositoryId);
            pstmt.executeUpdate();
        }
        finally {
            pstmt.close();
        }
    }


    public String newRepository(Connection con, String repositoryName) throws SQLException {
        PreparedStatement pstmt = con.prepareStatement(
              " select REPOSITORY_ID from PM_REPOSITORY where REPOSITORY_NAME = ? ");
        try {
            pstmt.setString(1, repositoryName);
            ResultSet rs = pstmt.executeQuery();
            try {
                if (rs.next()) {
                    return DataProcessConstants.REPOSITORY_ALREADY_EXISTS;
                }
            }
            finally {
                rs.close();
            }
        }
        finally {
            pstmt.close();
        }

        int nextRepositoryId = SQLUtil.getNextId(con, "PM_REPOSITORY", "REPOSITORY_ID");
        pstmt = con.prepareStatement(
              " insert into PM_REPOSITORY (REPOSITORY_ID, REPOSITORY_NAME) values (?, ?)");
        try {
            pstmt.setInt(1, nextRepositoryId);
            pstmt.setString(2, repositoryName);
            pstmt.executeUpdate();
            return Integer.toString(nextRepositoryId);
        }
        finally {
            pstmt.close();
        }
    }


    public String updateRepository(Connection con, int repositoryId, String content)
          throws SQLException, TreatmentException {
        Repository.reinitializeRepositoryCache();
        con.setAutoCommit(false);
        try {
            PreparedStatement pstmt
                  = con.prepareStatement("delete PM_REPOSITORY_CONTENT where REPOSITORY_ID = ?");
            try {
                pstmt.setInt(1, repositoryId);
                pstmt.executeUpdate();
            }
            finally {
                pstmt.close();
            }
            int nextRepositoryContentId =
                  SQLUtil.getNextId(con, "PM_REPOSITORY_CONTENT", "REPOSITORY_CONTENT_ID");
            pstmt = con.prepareStatement("insert into PM_REPOSITORY_CONTENT"
                                         + " (REPOSITORY_CONTENT_ID, REPOSITORY_ID, TREATMENT_ID, CONTENT) values (?, ?, ?, ?)");
            try {
                final Document doc = XMLUtils.parse(content);
                NodeList nodes = doc.getElementsByTagName(DataProcessConstants.TREATMENT_ENTITY_XML);
                int nbNodes = nodes.getLength();
                for (int i = 0; i < nbNodes; i++) {
                    Node node = nodes.item(i);
                    String treatmentId = node.getAttributes().getNamedItem("id").getNodeValue();
                    String contentNode = XMLUtils.nodeToString(node);
                    pstmt.setInt(1, nextRepositoryContentId);
                    pstmt.setInt(2, repositoryId);
                    pstmt.setString(3, treatmentId);
                    pstmt.setString(4, contentNode);
                    pstmt.executeUpdate();
                    nextRepositoryContentId++;
                }

                String treatmentId = checkIntegrityRepositoryContent(con, repositoryId);
                if (treatmentId != null) {
                    con.rollback();
                    Log.error(RepositoryDao.class, "Rollback effectué !\n" + treatmentId
                                                   + " est trop long ! Il serait tronqué en base de données.");
                    return treatmentId;
                }
                else {
                    con.commit();
                    return "OK";
                }
            }
            catch (Exception ex) {
                con.rollback();
                Log.error(RepositoryDao.class, "Rollback effectué", ex);
                throw new TreatmentException(ex.getLocalizedMessage(), ex);
            }
            finally {
                pstmt.close();
            }
        }
        finally {
            con.setAutoCommit(true);
        }
    }


    public int getRepositoryIdFromName(Connection con, String repositoryName)
          throws SQLException, TreatmentException {
        PreparedStatement pstmt = con.prepareStatement(
              "select REPOSITORY_ID from PM_REPOSITORY where REPOSITORY_NAME = ?");
        try {
            pstmt.setString(1, repositoryName);
            ResultSet rs = pstmt.executeQuery();
            try {
                if (rs.next()) {
                    return rs.getInt("REPOSITORY_ID");
                }
                else {
                    throw new TreatmentException(String.format("Le repository %s n'existe pas.",
                                                               repositoryName));
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


    public String getRepositoryNameFromId(Connection con, int repositoryId)
          throws SQLException, TreatmentException {
        PreparedStatement pstmt = con.prepareStatement(
              "select REPOSITORY_NAME from PM_REPOSITORY where REPOSITORY_ID = ?");
        try {
            pstmt.setInt(1, repositoryId);
            ResultSet rs = pstmt.executeQuery();
            try {
                if (rs.next()) {
                    return rs.getString("REPOSITORY_NAME");
                }
                else {
                    throw new TreatmentException(String.format("Le repository Id = %d n'existe pas.",
                                                               repositoryId));
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


    private static String checkIntegrityRepositoryContent(Connection con, int repositoryId)
          throws SQLException {
        PreparedStatement pstmt = con.prepareStatement("select TREATMENT_ID from PM_REPOSITORY_CONTENT "
                                                       + " where REPOSITORY_ID = ? and CONTENT not like '%</treatment>%'");
        try {
            pstmt.setInt(1, repositoryId);
            ResultSet rs = pstmt.executeQuery();
            try {
                if (rs.next()) {
                    return rs.getString("TREATMENT_ID");
                }
                return null;
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
