/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.dao;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.codec.ListCodec;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.common.model.UserTreatment;
import net.codjo.dataprocess.common.util.XMLUtils;
import net.codjo.dataprocess.server.repository.Repository;
import net.codjo.dataprocess.server.util.SQLUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 *
 */
public class TreatmentDao {
    private static final String DELETE_TREATMENT =
          " delete PM_TREATMENT from PM_TREATMENT inner join PM_EXECUTION_LIST "
          + " on PM_TREATMENT.EXECUTION_LIST_ID = PM_EXECUTION_LIST.EXECUTION_LIST_ID "
          + " where FAMILY_ID = ? and PM_EXECUTION_LIST.REPOSITORY_ID = ?";
    private static final String DELETE_EXECUTION_LIST =
          " delete PM_EXECUTION_LIST where FAMILY_ID = ? and REPOSITORY_ID = ?";
    private static final String DELETE_EXECUTION_LIST_STATUS =
          " delete PM_EXECUTION_LIST_STATUS "
          + " from PM_EXECUTION_LIST_STATUS inner join PM_EXECUTION_LIST "
          + " on PM_EXECUTION_LIST_STATUS.EXECUTION_LIST_ID = PM_EXECUTION_LIST.EXECUTION_LIST_ID "
          + " where FAMILY_ID = ? and PM_EXECUTION_LIST.REPOSITORY_ID = ?";
    private static final String DELETE_TREATMENT_STATUS =
          " delete PM_TREATMENT_STATUS "
          + " from PM_TREATMENT_STATUS inner join PM_EXECUTION_LIST "
          + " on PM_TREATMENT_STATUS.EXECUTION_LIST_ID = PM_EXECUTION_LIST.EXECUTION_LIST_ID "
          + " where FAMILY_ID = ? and PM_EXECUTION_LIST.REPOSITORY_ID = ?";
    private static final String INSERT_EXECUTION_LIST =
          "insert into PM_EXECUTION_LIST (EXECUTION_LIST_ID, EXECUTION_LIST_NAME, PRIORITY, FAMILY_ID, REPOSITORY_ID)"
          + " values (?, ?, ?, ?, ?)";
    private static final String INSERT_EXECUTION_LIST_STATUS =
          "insert into PM_EXECUTION_LIST_STATUS (EXECUTION_LIST_ID, STATUS, EXECUTION_DATE)"
          + " select EXECUTION_LIST_ID, ?, ? from PM_EXECUTION_LIST "
          + " where EXECUTION_LIST_NAME = ? and FAMILY_ID = ? and REPOSITORY_ID = ?";
    private static final String INSERT_TREATMENT =
          " insert PM_TREATMENT (TREATMENT_ID, PRIORITY, EXECUTION_LIST_ID) "
          + " select ?, ?, EXECUTION_LIST_ID from PM_EXECUTION_LIST "
          + " where EXECUTION_LIST_NAME = ? and FAMILY_ID = ? and REPOSITORY_ID = ? ";
    private static final String INSERT_TREATMENT_STATUS =
          " insert PM_TREATMENT_STATUS (EXECUTION_LIST_ID, TREATMENT_ID, STATUS, EXECUTION_DATE) "
          + " select EXECUTION_LIST_ID, ?, ?, ? from PM_EXECUTION_LIST "
          + " where EXECUTION_LIST_NAME = ? and FAMILY_ID = ? and REPOSITORY_ID = ? ";


    public List<ExecutionListModel> getExecutionListModel(Connection con, int repositoryId, int familyId)
          throws SQLException, TreatmentException {
        String familyClause = "";
        if (familyId != 0) {
            familyClause += " PM_EXECUTION_LIST.FAMILY_ID = ? and ";
        }
        String sql =
              "select PM_EXECUTION_LIST.EXECUTION_LIST_ID, PM_EXECUTION_LIST.REPOSITORY_ID,"
              + " PM_EXECUTION_LIST.EXECUTION_LIST_NAME,"
              + " PM_EXECUTION_LIST.PRIORITY, PM_EXECUTION_LIST_STATUS.STATUS,"
              + " PM_EXECUTION_LIST.FAMILY_ID, "
              + " PM_EXECUTION_LIST_STATUS.EXECUTION_DATE "
              + " from PM_EXECUTION_LIST "
              + " inner join PM_EXECUTION_LIST_STATUS "
              + " on PM_EXECUTION_LIST.EXECUTION_LIST_ID = PM_EXECUTION_LIST_STATUS.EXECUTION_LIST_ID "
              + " where " + familyClause
              + " PM_EXECUTION_LIST.REPOSITORY_ID = ? ";
        PreparedStatement stmt = con.prepareStatement(sql);
        try {
            int idx = 1;
            if (familyId != 0) {
                stmt.setInt(idx++, familyId);
            }
            stmt.setInt(idx, repositoryId);
            ResultSet rs = stmt.executeQuery();
            try {
                List<ExecutionListModel> result = new ArrayList<ExecutionListModel>();
                while (rs.next()) {
                    ExecutionListModel executionList = ExecutionListModel.buildExecutionListModel(rs);
                    int executionListId = executionList.getId();
                    try {
                        Map<UserTreatment, Integer> usrTrtMap = getPriorityMap(con, repositoryId,
                                                                               executionListId);
                        executionList.setPriorityMap(usrTrtMap);
                        result.add(executionList);
                    }
                    catch (TreatmentException ex) {
                        throw new TreatmentException("Erreur lors du chargement de la liste de traitements '"
                                                     + executionList.getName() + "' (id = " + executionListId
                                                     + ")", ex);
                    }
                }
                return result;
            }
            finally {
                rs.close();
            }
        }
        finally {
            stmt.close();
        }
    }


    public ExecutionListModel getExecutionListModel(Connection con,
                                                    String executionListName,
                                                    int repositoryId,
                                                    int familyId)
          throws SQLException, TreatmentException {
        String sql =
              "select PM_EXECUTION_LIST.EXECUTION_LIST_ID, PM_EXECUTION_LIST.REPOSITORY_ID,"
              + " PM_EXECUTION_LIST.EXECUTION_LIST_NAME,"
              + " PM_EXECUTION_LIST.PRIORITY, PM_EXECUTION_LIST_STATUS.STATUS,"
              + " PM_EXECUTION_LIST.FAMILY_ID, "
              + " PM_EXECUTION_LIST_STATUS.EXECUTION_DATE "
              + " from PM_EXECUTION_LIST "
              + " inner join PM_EXECUTION_LIST_STATUS "
              + " on PM_EXECUTION_LIST.EXECUTION_LIST_ID = PM_EXECUTION_LIST_STATUS.EXECUTION_LIST_ID "
              + " where PM_EXECUTION_LIST.EXECUTION_LIST_NAME = ? and PM_EXECUTION_LIST.FAMILY_ID = ? "
              + " and PM_EXECUTION_LIST.REPOSITORY_ID = ? ";
        PreparedStatement stmt = con.prepareStatement(sql);
        try {
            int idx = 1;
            stmt.setString(idx++, executionListName);
            stmt.setInt(idx++, familyId);
            stmt.setInt(idx, repositoryId);
            ResultSet rs = stmt.executeQuery();
            try {
                ExecutionListModel executionList;
                if (rs.next()) {
                    executionList = ExecutionListModel.buildExecutionListModel(rs);
                    int executionListId = executionList.getId();
                    try {
                        Map<UserTreatment, Integer> usrTrtMap = getPriorityMap(con, repositoryId,
                                                                               executionListId);
                        executionList.setPriorityMap(usrTrtMap);
                        return executionList;
                    }
                    catch (TreatmentException ex) {
                        throw new TreatmentException("Erreur lors du chargement de la liste de traitements '"
                                                     + executionList.getName() + "' (id = " + executionListId
                                                     + ")", ex);
                    }
                }
                else {
                    throw new TreatmentException(
                          "Liste de traitements '" + executionListName + "' non trouvée (repository id = "
                          + repositoryId + ", family id = " + familyId + ")");
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


    private static Map<UserTreatment, Integer> getPriorityMap(Connection con,
                                                              int repositoryId,
                                                              int executionListId)
          throws SQLException, TreatmentException {
        Map<UserTreatment, Integer> usrTrtMap = new HashMap<UserTreatment, Integer>();
        PreparedStatement stmt = con.prepareStatement(
              "select TREATMENT_ID, EXECUTION_LIST_ID, PRIORITY from PM_TREATMENT "
              + " where EXECUTION_LIST_ID = ? ");
        try {
            stmt.setInt(1, executionListId);
            ResultSet rs = stmt.executeQuery();
            try {
                while (rs.next()) {
                    UserTreatment usrTrt =
                          buildUserTreatment(con, repositoryId, rs.getString("TREATMENT_ID"));
                    Integer priority = rs.getInt("PRIORITY");
                    usrTrtMap.put(usrTrt, priority);
                }
                return usrTrtMap;
            }
            finally {
                rs.close();
            }
        }
        finally {
            stmt.close();
        }
    }


    public static UserTreatment buildUserTreatment(Connection con, int repositoryId, String treatmentId)
          throws TreatmentException {
        TreatmentModel trtModel = Repository.getTreatmentById(con, repositoryId, treatmentId);

        if (trtModel != null) {
            return new UserTreatment(trtModel);
        }
        return null;
    }


    public void save(Connection con, List<ExecutionListModel> trtList, int repositoryId, int familyId)
          throws SQLException, TreatmentException {
        con.setAutoCommit(false);
        try {
            PreparedStatement stmt = con.prepareStatement(DELETE_EXECUTION_LIST_STATUS +
                                                          DELETE_TREATMENT_STATUS +
                                                          DELETE_TREATMENT +
                                                          DELETE_EXECUTION_LIST);
            try {
                stmt.setInt(1, familyId);
                stmt.setInt(2, repositoryId);
                stmt.setInt(3, familyId);
                stmt.setInt(4, repositoryId);
                stmt.setInt(5, familyId);
                stmt.setInt(6, repositoryId);
                stmt.setInt(7, familyId);
                stmt.setInt(8, repositoryId);
                stmt.executeUpdate();
            }
            finally {
                stmt.close();
            }
            for (ExecutionListModel trtEx : trtList) {
                stmt = con.prepareStatement(INSERT_EXECUTION_LIST);
                try {
                    stmt.setInt(1, getExecutionListId(con, trtEx));
                    stmt.setString(2, trtEx.getName());
                    stmt.setInt(3, getPriority(con, trtEx, familyId));
                    stmt.setInt(4, familyId);
                    stmt.setInt(5, repositoryId);
                    stmt.executeUpdate();
                }
                finally {
                    stmt.close();
                }

                stmt = con.prepareStatement(INSERT_EXECUTION_LIST_STATUS);
                try {
                    stmt.setInt(1, trtEx.getStatus());
                    stmt.setTimestamp(2, trtEx.getExecutionDate());
                    stmt.setString(3, trtEx.getName());
                    stmt.setInt(4, familyId);
                    stmt.setInt(5, repositoryId);
                    stmt.executeUpdate();
                }
                finally {
                    stmt.close();
                }

                if (!trtEx.getPriorityMap().isEmpty()) {
                    for (UserTreatment userTreatment : trtEx.getPriorityMap().keySet()) {
                        stmt = con.prepareStatement(INSERT_TREATMENT);
                        try {
                            stmt.setString(1, userTreatment.getId());
                            stmt.setInt(2, trtEx.getPriorityMap().get(userTreatment));
                            stmt.setString(3, trtEx.getName());
                            stmt.setInt(4, familyId);
                            stmt.setInt(5, repositoryId);
                            stmt.executeUpdate();
                        }
                        finally {
                            stmt.close();
                        }
                        stmt = con.prepareStatement(INSERT_TREATMENT_STATUS);
                        try {
                            stmt.setString(1, userTreatment.getId());
                            stmt.setInt(2, DataProcessConstants.TO_DO);
                            stmt.setTimestamp(3, new Timestamp(0));
                            stmt.setString(4, trtEx.getName());
                            stmt.setInt(5, familyId);
                            stmt.setInt(6, repositoryId);
                            stmt.executeUpdate();
                        }
                        finally {
                            stmt.close();
                        }
                    }
                }
            }
            con.commit();
        }
        catch (SQLException ex) {
            con.rollback();
            Log.error(getClass(), "Rollback effectué", ex);
            throw ex;
        }
        finally {
            con.setAutoCommit(true);
        }
    }


    public void deleteExecutionLists(Connection con, int repositoryId, int familyId) throws SQLException {
        con.setAutoCommit(false);
        try {
            PreparedStatement pstmt = con.prepareStatement(
                  " delete PM_EXECUTION_LIST_STATUS from PM_EXECUTION_LIST_STATUS "
                  + " inner join PM_EXECUTION_LIST "
                  + " on PM_EXECUTION_LIST_STATUS.EXECUTION_LIST_ID = PM_EXECUTION_LIST.EXECUTION_LIST_ID "
                  + " where PM_EXECUTION_LIST.REPOSITORY_ID = ? "
                  + " and PM_EXECUTION_LIST.FAMILY_ID = ? ");
            try {
                pstmt.setInt(1, repositoryId);
                pstmt.setInt(2, familyId);
                pstmt.executeUpdate();
            }
            finally {
                pstmt.close();
            }

            pstmt = con.prepareStatement(
                  " delete PM_TREATMENT from PM_TREATMENT inner join PM_EXECUTION_LIST "
                  + " on PM_TREATMENT.EXECUTION_LIST_ID = PM_EXECUTION_LIST.EXECUTION_LIST_ID "
                  + " where PM_EXECUTION_LIST.REPOSITORY_ID = ?  "
                  + " and PM_EXECUTION_LIST.FAMILY_ID = ? ");
            try {
                pstmt.setInt(1, repositoryId);
                pstmt.setInt(2, familyId);
                pstmt.executeUpdate();
            }
            finally {
                pstmt.close();
            }

            pstmt = con.prepareStatement(
                  "delete PM_DEPENDENCY from PM_DEPENDENCY inner join PM_EXECUTION_LIST "
                  + " on (PM_DEPENDENCY.EXECUTION_LIST_ID_PRINC = PM_EXECUTION_LIST.EXECUTION_LIST_NAME "
                  + " or PM_DEPENDENCY.EXECUTION_LIST_ID_DEP = PM_EXECUTION_LIST.EXECUTION_LIST_NAME) "
                  + " where PM_EXECUTION_LIST.REPOSITORY_ID = ?  "
                  + " and PM_EXECUTION_LIST.FAMILY_ID = ? "
                  + " and PM_DEPENDENCY.REPOSITORY_ID = ? ");
            try {
                pstmt.setInt(1, repositoryId);
                pstmt.setInt(2, familyId);
                pstmt.setInt(3, repositoryId);
                pstmt.executeUpdate();
            }
            finally {
                pstmt.close();
            }

            pstmt = con.prepareStatement(" delete PM_EXECUTION_LIST from PM_EXECUTION_LIST "
                                         + " where PM_EXECUTION_LIST.REPOSITORY_ID = ? "
                                         + " and PM_EXECUTION_LIST.FAMILY_ID = ? ");
            try {
                pstmt.setInt(1, repositoryId);
                pstmt.setInt(2, familyId);
                pstmt.executeUpdate();
            }
            finally {
                pstmt.close();
            }
            con.commit();
        }
        catch (SQLException ex) {
            con.rollback();
            Log.error(TreatmentDao.class, "Rollback effectué", ex);
            throw ex;
        }
        finally {
            con.setAutoCommit(true);
        }
    }


    public void copyExecutionListsFromRepoToRepo(Connection con, int repositoryFrom, int repositoryTo)
          throws SQLException {
        con.setAutoCommit(false);
        try {
            PreparedStatement pstmt = con.prepareStatement(
                  " select EXECUTION_LIST_ID, REPOSITORY_ID, FAMILY_ID, EXECUTION_LIST_NAME, PRIORITY "
                  + " from PM_EXECUTION_LIST where REPOSITORY_ID = ? ");
            try {
                pstmt.setInt(1, repositoryFrom);
                ResultSet rs = pstmt.executeQuery();
                try {
                    int newExecutionListId = getNextExecutionListId(con);
                    while (rs.next()) {
                        PreparedStatement pstmt2 = con.prepareStatement(" insert into PM_EXECUTION_LIST "
                                                                        + " (EXECUTION_LIST_ID, REPOSITORY_ID, FAMILY_ID, EXECUTION_LIST_NAME, PRIORITY) "
                                                                        + " values (?, ?, ?, ?, ?)");
                        try {
                            pstmt2.setInt(1, newExecutionListId);
                            pstmt2.setInt(2, repositoryTo);
                            pstmt2.setInt(3, rs.getInt("FAMILY_ID"));
                            pstmt2.setString(4, rs.getString("EXECUTION_LIST_NAME"));
                            pstmt2.setInt(5, rs.getInt("PRIORITY"));
                            pstmt2.executeUpdate();

                            pstmt2 = con.prepareStatement(
                                  " insert into PM_EXECUTION_LIST_STATUS (EXECUTION_LIST_ID, STATUS)  values (?, ?)");
                            pstmt2.setInt(1, newExecutionListId);
                            pstmt2.setInt(2, DataProcessConstants.TO_DO);
                            pstmt2.executeUpdate();

                            pstmt2 = con.prepareStatement(
                                  " insert into PM_TREATMENT (TREATMENT_ID, PRIORITY, EXECUTION_LIST_ID) "
                                  + " select TREATMENT_ID, PRIORITY, ? from PM_TREATMENT "
                                  + " where EXECUTION_LIST_ID = ? ");
                            pstmt2.setInt(1, newExecutionListId);
                            pstmt2.setInt(2, rs.getInt("EXECUTION_LIST_ID"));
                            pstmt2.executeUpdate();

                            newExecutionListId++;
                        }
                        finally {
                            pstmt2.close();
                        }
                    }
                }
                finally {
                    rs.close();
                }
            }
            finally {
                pstmt.close();
            }
            PreparedStatement pstmt3 = con.prepareStatement(" insert into PM_DEPENDENCY "
                                                            + " (EXECUTION_LIST_ID_PRINC, EXECUTION_LIST_ID_DEP, REPOSITORY_ID) "
                                                            + " select EXECUTION_LIST_ID_PRINC, EXECUTION_LIST_ID_DEP, ? "
                                                            + " from PM_DEPENDENCY where REPOSITORY_ID = ?");
            try {
                pstmt3.setInt(1, repositoryTo);
                pstmt3.setInt(2, repositoryFrom);
                pstmt3.executeUpdate();
            }
            finally {
                pstmt3.close();
            }
            con.commit();
        }
        catch (SQLException ex) {
            con.rollback();
            Log.error(TreatmentDao.class, "Rollback effectué", ex);
            throw ex;
        }
        finally {
            con.setAutoCommit(true);
        }
    }


    private static int getNextExecutionListId(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        try {
            ResultSet rs = stmt.executeQuery("select max(EXECUTION_LIST_ID) "
                                             + " as EXECUTION_LIST_ID from PM_EXECUTION_LIST");
            try {
                if (rs.next()) {
                    return rs.getInt("EXECUTION_LIST_ID") + 1;
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


    public void reinitExecutionList(Connection con, int repositoryId) throws SQLException {
        PreparedStatement pstmt = con.prepareStatement(" update PM_EXECUTION_LIST_STATUS "
                                                       + " set PM_EXECUTION_LIST_STATUS.EXECUTION_DATE = null, "
                                                       + " PM_EXECUTION_LIST_STATUS.STATUS = "
                                                       + DataProcessConstants.TO_DO
                                                       + " from PM_EXECUTION_LIST_STATUS inner join PM_EXECUTION_LIST "
                                                       + " on PM_EXECUTION_LIST_STATUS.EXECUTION_LIST_ID = PM_EXECUTION_LIST.EXECUTION_LIST_ID "
                                                       + " where PM_EXECUTION_LIST.REPOSITORY_ID = ?");
        try {
            pstmt.setInt(1, repositoryId);
            pstmt.executeUpdate();
        }
        finally {
            pstmt.close();
        }
    }


    public String manageTreatmentModel(Connection con,
                                       DataProcessConstants.Command command,
                                       int repositoryId,
                                       String treatmentContentXml)
          throws TreatmentException, SQLException {
        String treatmentId = null;
        con.setAutoCommit(false);
        try {
            String result = DataProcessConstants.NO_RESULT;
            if (command != DataProcessConstants.Command.COPY) {
                Document doc = XMLUtils.parse(treatmentContentXml);
                NodeList nodes = doc.getElementsByTagName(DataProcessConstants.TREATMENT_ENTITY_XML);
                Node node = nodes.item(0);
                treatmentId = node.getAttributes().getNamedItem("id").getNodeValue();
                if (treatmentId.length() > 50) {
                    throw new TreatmentException(String.format("La taille de l'identifiant du traitement '%s'"
                                                               + " dépasse 50 caractères.", treatmentId));
                }
            }
            switch (command) {
                case CREATE:
                    manageTreatmentModelCreate(con, repositoryId, treatmentContentXml, treatmentId);
                    break;
                case DELETE:
                    manageTreatmentModelDelete(con, repositoryId, treatmentId);
                    break;
                case READ:
                    String sql =
                          "select CONTENT from PM_REPOSITORY_CONTENT where REPOSITORY_ID = ?  and TREATMENT_ID = ?";
                    PreparedStatement pstmt = con.prepareStatement(sql);
                    try {
                        pstmt.setInt(1, repositoryId);
                        pstmt.setString(2, treatmentId);
                        ResultSet rs = pstmt.executeQuery();
                        try {
                            if (rs.next()) {
                                result = rs.getString("CONTENT")
                                      .replace(DataProcessConstants.SPECIAL_CHAR_REPLACER_N, "\n")
                                      .replace(DataProcessConstants.SPECIAL_CHAR_REPLACER_R, "\r");
                            }
                            else {
                                treatmentModelNotFoundError(repositoryId, treatmentId);
                            }
                        }
                        finally {
                            rs.close();
                        }
                    }
                    finally {
                        pstmt.close();
                    }
                    break;
                case UPDATE:
                    manageTreatmentModelUpdate(con, repositoryId, treatmentContentXml, treatmentId);
                    break;
                case IS_EXIST:
                    result = manageTreatmentModelIsExist(con, repositoryId, treatmentId);
                    break;
                case COPY:
                    manageTreatmentModelCopy(con, repositoryId, treatmentContentXml);
                    break;
            }
            con.commit();
            return result;
        }
        catch (Exception ex) {
            con.rollback();
            throw new TreatmentException(ex.getLocalizedMessage(), ex);
        }
        finally {
            con.setAutoCommit(true);
        }
    }


    private static void manageTreatmentModelCopy(Connection con, int repositoryId, String treatmentContentXml)
          throws SQLException {
        String treatmentId;
        int repositoryIdDest = Integer.parseInt(treatmentContentXml);
        PreparedStatement pstmtSelect = con.prepareStatement(
              "select TREATMENT_ID, CONTENT from PM_REPOSITORY_CONTENT where REPOSITORY_ID = ?");
        try {
            pstmtSelect.setInt(1, repositoryId);
            ResultSet rs = pstmtSelect.executeQuery();
            try {
                while (rs.next()) {
                    treatmentId = rs.getString("TREATMENT_ID");
                    String content = rs.getString("CONTENT");
                    PreparedStatement pstmtInsert = con.prepareStatement(
                          "insert into PM_REPOSITORY_CONTENT (REPOSITORY_CONTENT_ID , REPOSITORY_ID, TREATMENT_ID, CONTENT )"
                          + " values (?, ?, ?, ?)");
                    try {
                        pstmtInsert.setInt(1, SQLUtil.getNextId(con,
                                                                "PM_REPOSITORY_CONTENT",
                                                                "REPOSITORY_CONTENT_ID"));
                        pstmtInsert.setInt(2, repositoryIdDest);
                        pstmtInsert.setString(3, treatmentId);
                        pstmtInsert.setString(4, content);
                        pstmtInsert.executeUpdate();
                    }
                    finally {
                        pstmtInsert.close();
                    }
                }
            }
            finally {
                rs.close();
            }
        }
        finally {
            pstmtSelect.close();
        }
    }


    private static String manageTreatmentModelIsExist(Connection con, int repositoryId, String treatmentId)
          throws SQLException {
        PreparedStatement pstmt = con.prepareStatement(
              "select TREATMENT_ID from PM_REPOSITORY_CONTENT where REPOSITORY_ID = ? and TREATMENT_ID = ?");
        try {
            pstmt.setInt(1, repositoryId);
            pstmt.setString(2, treatmentId);
            ResultSet rs = pstmt.executeQuery();
            try {
                if (rs.next()) {
                    return "TRUE";
                }
                else {
                    return "FALSE";
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


    private static void manageTreatmentModelUpdate(Connection con,
                                                   int repositoryId,
                                                   String treatmentContentXml, String treatmentId)
          throws TreatmentException, SQLException {
        PreparedStatement pstmt = con.prepareStatement("update PM_REPOSITORY_CONTENT set CONTENT = ? "
                                                       + "where REPOSITORY_ID = ? and TREATMENT_ID = ?");
        try {
            pstmt.setString(1, XMLUtils.flattenAndReplaceCRLF(treatmentContentXml, false));
            pstmt.setInt(2, repositoryId);
            pstmt.setString(3, treatmentId);
            int rowCount = pstmt.executeUpdate();
            if (rowCount == 0) {
                treatmentModelNotFoundError(repositoryId, treatmentId);
            }
        }
        finally {
            pstmt.close();
        }

        pstmt = con.prepareStatement(
              "select TREATMENT_ID from PM_REPOSITORY_CONTENT where REPOSITORY_ID = ? and TREATMENT_ID = ? and CONTENT like '%</treatment>%'");
        try {
            pstmt.setInt(1, repositoryId);
            pstmt.setString(2, treatmentId);
            ResultSet rs = pstmt.executeQuery();
            try {
                if (!rs.next()) {
                    throw new TreatmentException(String.format("Le traitement %s est trop long !",
                                                               treatmentId));
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


    private static void manageTreatmentModelDelete(Connection con, int repositoryId, String treatmentId)
          throws TreatmentException, SQLException {
        PreparedStatement pstmt = con.prepareStatement("select EXECUTION_LIST_NAME from PM_EXECUTION_LIST"
                                                       + " inner join PM_TREATMENT"
                                                       + " on PM_EXECUTION_LIST.EXECUTION_LIST_ID = PM_TREATMENT.EXECUTION_LIST_ID"
                                                       + " where PM_TREATMENT.TREATMENT_ID = ?"
                                                       + " and PM_EXECUTION_LIST.REPOSITORY_ID = ?");
        try {
            pstmt.setString(1, treatmentId);
            pstmt.setInt(2, repositoryId);
            ResultSet rs = pstmt.executeQuery();
            try {
                if (rs.next()) {
                    List<String> executionListNames = new ArrayList<String>();
                    do {
                        executionListNames.add(rs.getString("EXECUTION_LIST_NAME"));
                    }
                    while (rs.next());
                    treatmentModelInUseError(treatmentId, executionListNames);
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
              "delete PM_REPOSITORY_CONTENT where REPOSITORY_ID = ? and TREATMENT_ID = ?");
        try {
            pstmt.setInt(1, repositoryId);
            pstmt.setString(2, treatmentId);
            int rowCount = pstmt.executeUpdate();
            if (rowCount == 0) {
                treatmentModelNotFoundError(repositoryId, treatmentId);
            }
        }
        finally {
            pstmt.close();
        }
    }


    private static void manageTreatmentModelCreate(Connection con,
                                                   int repositoryId,
                                                   String treatmentContentXml, String treatmentId)
          throws SQLException, TreatmentException {
        int nextRepositoryContentId =
              SQLUtil.getNextId(con, "PM_REPOSITORY_CONTENT", "REPOSITORY_CONTENT_ID");
        PreparedStatement pstmt = con.prepareStatement("insert into PM_REPOSITORY_CONTENT"
                                                       + " (REPOSITORY_CONTENT_ID, REPOSITORY_ID, TREATMENT_ID, CONTENT)"
                                                       + " values (?, ?, ?, ?)");
        try {
            pstmt.setInt(1, nextRepositoryContentId);
            pstmt.setInt(2, repositoryId);
            pstmt.setString(3, treatmentId);
            pstmt.setString(4, XMLUtils.flattenAndReplaceCRLF(treatmentContentXml, false));
            pstmt.executeUpdate();

            pstmt = con.prepareStatement(
                  "select TREATMENT_ID from PM_REPOSITORY_CONTENT where REPOSITORY_CONTENT_ID = ?"
                  + " and CONTENT like '%</treatment>%'");
            pstmt.setInt(1, nextRepositoryContentId);
            ResultSet rs = pstmt.executeQuery();
            try {
                if (!rs.next()) {
                    throw new TreatmentException(String.format("Le traitement %s est trop long !",
                                                               treatmentId));
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


    public List<String> getRepositoryExecutionList(Connection con, int repositoryId) throws SQLException {
        PreparedStatement pstmt = con.prepareStatement(
              "select EXECUTION_LIST_NAME from PM_EXECUTION_LIST where REPOSITORY_ID = ? "
              + " order by EXECUTION_LIST_NAME");
        try {
            pstmt.setInt(1, repositoryId);
            ResultSet rs = pstmt.executeQuery();
            try {
                List<String> execListNames = new ArrayList<String>();
                while (rs.next()) {
                    execListNames.add(rs.getString("EXECUTION_LIST_NAME"));
                }
                return execListNames;
            }
            finally {
                rs.close();
            }
        }
        finally {
            pstmt.close();
        }
    }


    public List<String> getTreatmentIdList(Connection con, int repositoryId) throws SQLException {
        PreparedStatement pstmt = con.prepareStatement(
              "select TREATMENT_ID from PM_REPOSITORY_CONTENT where REPOSITORY_ID = ? "
              + " order by TREATMENT_ID");
        try {
            pstmt.setInt(1, repositoryId);
            ResultSet rs = pstmt.executeQuery();
            try {
                List<String> treatmentIdList = new ArrayList<String>();
                while (rs.next()) {
                    treatmentIdList.add(rs.getString("TREATMENT_ID"));
                }
                return treatmentIdList;
            }
            finally {
                rs.close();
            }
        }
        finally {
            pstmt.close();
        }
    }


    public void insertExecutionListModel(Connection con, int repositoryId, String repositoryName,
                                         List<ExecutionListModel> executionListModelList)
          throws SQLException, TreatmentException {
        PreparedStatement pstmt = con.prepareStatement("insert into PM_EXECUTION_LIST "
                                                       + "(EXECUTION_LIST_ID, EXECUTION_LIST_NAME, PRIORITY, FAMILY_ID, REPOSITORY_ID) "
                                                       + "values (?, ?, ?, ?, ?)");
        try {
            StatusDao statusDao = new StatusDao();
            for (ExecutionListModel execListModel : executionListModelList) {
                int idExecutionList = SQLUtil.getNextId(con, "PM_EXECUTION_LIST", "EXECUTION_LIST_ID");
                pstmt.setInt(1, idExecutionList);
                pstmt.setString(2, execListModel.getName());
                pstmt.setInt(3, execListModel.getPriority());
                pstmt.setInt(4, execListModel.getFamilyId());
                pstmt.setInt(5, repositoryId);
                pstmt.executeUpdate();

                statusDao.createDefaultExecutionListStatus(con, idExecutionList);
                insertTreatment(con, repositoryId, repositoryName, idExecutionList,
                                execListModel.getSortedTreatmentList());
            }
        }
        finally {
            pstmt.close();
        }
    }


    private void insertTreatment(Connection con, int repositoryId, String repositoryName, int idExecutionList,
                                 List<UserTreatment> userTreatmentList)
          throws SQLException, TreatmentException {
        PreparedStatement pstmt = con.prepareStatement(
              "insert into PM_TREATMENT (TREATMENT_ID, PRIORITY, EXECUTION_LIST_ID) values (?, ?, ?)");
        try {
            List<String> treatmentIdList = getTreatmentIdList(con, repositoryId);
            for (UserTreatment userTreatment : userTreatmentList) {
                if (!treatmentIdList.contains(userTreatment.getId())) {
                    throw new TreatmentException(String.format(
                          "Le traitements '%s' n'existe pas dans le repository '%s'.",
                          userTreatment.getId(), repositoryName));
                }
                pstmt.setString(1, userTreatment.getId());
                pstmt.setInt(2, userTreatment.getPriority());
                pstmt.setInt(3, idExecutionList);
                pstmt.executeUpdate();
            }
        }
        finally {
            pstmt.close();
        }
    }


    private static void treatmentModelNotFoundError(int repositoryId, String treatmentId)
          throws TreatmentException {
        String errorMessage = "\n'%s' est inexistant dans le repository (repositoryId = %d)";
        throw new TreatmentException(String.format(errorMessage, treatmentId, repositoryId));
    }


    private static void treatmentModelInUseError(String treatmentId, List<String> executionListName)
          throws TreatmentException {
        String errorMessage = "\nLe traitement '%s' est utilisé dans des listes de traitement : %s"
                              + ".\nSa suppression est donc impossible.";
        throw new TreatmentException(String.format(errorMessage,
                                                   treatmentId,
                                                   new ListCodec().encode(executionListName)));
    }


    private static int getPriority(Connection con, ExecutionListModel trtEx, int familyId)
          throws SQLException {
        if (trtEx.getPriority() != 0) {
            return trtEx.getPriority();
        }
        else {
            PreparedStatement stmt = con.prepareStatement(
                  " select max(PRIORITY) as PRIORITY from PM_EXECUTION_LIST  where FAMILY_ID = ?");
            try {
                stmt.setInt(1, familyId);
                ResultSet rs = stmt.executeQuery();
                try {
                    if (rs.next()) {
                        return rs.getInt("PRIORITY") + 1;
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


    private static int getExecutionListId(Connection con, ExecutionListModel trtEx) throws SQLException {
        if (trtEx.getId() != 0) {
            return trtEx.getId();
        }
        else {
            return SQLUtil.getNextId(con, "PM_EXECUTION_LIST", "EXECUTION_LIST_ID");
        }
    }
}
