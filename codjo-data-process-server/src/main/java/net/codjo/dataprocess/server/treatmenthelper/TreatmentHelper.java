/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.treatmenthelper;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.codec.TreatmentRootCodec;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.common.model.TreatmentRoot;
import net.codjo.dataprocess.common.userparam.User;
import net.codjo.dataprocess.common.userparam.User.Repository;
import net.codjo.dataprocess.common.userparam.UserXStreamImpl;
import net.codjo.dataprocess.common.util.XMLUtils;
import net.codjo.dataprocess.server.kernel.TreatmentLauncher;
import net.codjo.dataprocess.server.util.SQLUtil;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.TransformerException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 *
 */
public class TreatmentHelper {
    private static final Logger LOG = Logger.getLogger(TreatmentHelper.class);
    private static final int LENGTH = 110;


    public List<TreatmentModel> loadAllTreatments(String uri) {
        TreatmentRoot treatmentRoot = TreatmentRootCodec.decodeFromResources(uri);
        return treatmentRoot.getTreatmentModelList();
    }


    public static void initRepositoryUserAccess(Connection con,
                                                String userName,
                                                List<RepositoryDescriptor> repositoryDescList)
          throws SQLException {
        User user = new User();
        user.setUserName(userName);
        for (RepositoryDescriptor repoDescriptor : repositoryDescList) {
            user.addRepository(new Repository(repoDescriptor.getRepositoryName()));
        }
        String xml = new UserXStreamImpl().toXml(user);
        PreparedStatement pStmt = con.prepareStatement(
              "delete from PM_DP_USER where USER_NAME = ? "
              + " insert into PM_DP_USER (USER_NAME, USER_PARAM) values (?, ?)");
        try {
            pStmt.setString(1, userName);
            pStmt.setString(2, userName);
            pStmt.setString(3, xml);
            pStmt.executeUpdate();
            LOG.info(String.format(
                  "Droits d'accès accordés à l'utilisateur '%s' pour tous les repositories.", userName));
        }
        finally {
            pStmt.close();
        }
    }


    public static void initRepository(Connection con, List<RepositoryDescriptor> repositoryDescList)
          throws Exception {
        try {
            con.setAutoCommit(false);

            LOG.info("Ajout des référentiels de traitement suivants :");
            for (RepositoryDescriptor repositoryDesc : repositoryDescList) {
                deleteRepository(con, repositoryDesc.getRepositoryId());
                insertAllRepositoryContent(con,
                                           repositoryDesc.getRepositoryId(),
                                           repositoryDesc.getRepositoryName(),
                                           repositoryDesc.getRepositoryPath());
                insertRepository(con, repositoryDesc.getRepositoryId(), repositoryDesc.getRepositoryName());
            }
            List<TreatmentFragment> treatmentFragmentList = checkIntegrityRepositoryContent(con);
            if (!treatmentFragmentList.isEmpty()) {
                String message = " est trop long ! : ";
                int maxLength = maxLengthTreatmentId(treatmentFragmentList) + message.length() + LENGTH;
                StringBuilder errorMessage = new StringBuilder();
                errorMessage.append("\n").append(StringUtils.repeat("#", maxLength));
                errorMessage.append("\n").append(StringUtils.repeat("+", maxLength));
                for (TreatmentFragment treatmentFragment : treatmentFragmentList) {
                    errorMessage.append("\n").append(treatmentFragment.getTreatmentId()).append(message)
                          .append(treatmentFragment.getContentFragment());
                }
                errorMessage.append("\n").append(StringUtils.repeat("+", maxLength));
                errorMessage.append("\n").append(StringUtils.repeat("#", maxLength));
                throw new TreatmentException(errorMessage.toString());
            }
            else {
                con.commit();
                LOG.info("Ajout terminé avec succès !");
            }
        }
        catch (Exception ex) {
            con.rollback();
            LOG.error("\nErreur durant l'ajout des référentiels de traitement.\n!!! Rollback effectué !!!\n",
                      ex);
            throw ex;
        }
        finally {
            con.setAutoCommit(true);
        }
    }


    private static List<TreatmentFragment> checkIntegrityRepositoryContent(Connection con)
          throws SQLException {
        List<TreatmentFragment> treatmentFragmentList = new ArrayList<TreatmentFragment>();
        Statement stmt = con.createStatement();
        try {
            ResultSet rs = stmt.executeQuery("select TREATMENT_ID, CONTENT from PM_REPOSITORY_CONTENT "
                                             + " where CONTENT not like '%</"
                                             + DataProcessConstants.TREATMENT_ENTITY_XML + ">%'");
            try {
                while (rs.next()) {
                    String content = rs.getString("CONTENT");
                    String contentFragment = content.substring(content.length() - LENGTH)
                          .replace(DataProcessConstants.SPECIAL_CHAR_REPLACER_N, " ")
                          .replace(DataProcessConstants.SPECIAL_CHAR_REPLACER_R, " ");
                    treatmentFragmentList.add(new TreatmentFragment(rs.getString("TREATMENT_ID"),
                                                                    contentFragment));
                }
                return treatmentFragmentList;
            }
            finally {
                rs.close();
            }
        }
        finally {
            stmt.close();
        }
    }


    private static void insertAllRepositoryContent(Connection con,
                                                   int repositoryId,
                                                   String repositoryName,
                                                   String[] repositoryListPath)
          throws SQLException, TransformerException, TreatmentException {
        LOG.info("---> " + repositoryName + " (repositoryId = " + repositoryId + ")");
        StringBuilder logStr = new StringBuilder("     fichiers : ");
        for (String repositoryPath : repositoryListPath) {
            logStr.append(repositoryPath).append(", ");
            String content = loadFileAsString(repositoryPath);
            if (content == null) {
                throw new IllegalArgumentException(repositoryPath + " n'a pas pu être trouvé et/ou chargé.");
            }
            insertRepositoryContent(con, repositoryId, content);
        }
        LOG.info(logStr.substring(0, logStr.length() - 2));
    }


    public static void insertRepository(Connection con, int repositoryId, String repositoryName)
          throws SQLException {
        PreparedStatement pStmt = con.prepareStatement(
              "insert into PM_REPOSITORY (REPOSITORY_ID, REPOSITORY_NAME) values (?, ?)");
        try {
            pStmt.setInt(1, repositoryId);
            pStmt.setString(2, repositoryName);
            pStmt.executeUpdate();
        }
        finally {
            pStmt.close();
        }
    }


    public static void updateDateRepositoryImport(Connection con, String date) throws SQLException {
        PreparedStatement pStmt = con.prepareStatement("delete from PM_DP_CONFIG where CLE = ? "
                                                       + "insert into PM_DP_CONFIG (CLE, VALEUR) values (?, ?)");
        try {
            pStmt.setString(1, DataProcessConstants.KEY_DATE_LAST_IMPORT_REPOSITORY);
            pStmt.setString(2, DataProcessConstants.KEY_DATE_LAST_IMPORT_REPOSITORY);
            pStmt.setString(3, date);
            pStmt.executeUpdate();
        }
        finally {
            pStmt.close();
        }
    }


    public static void insertRepositoryContent(Connection con, int repositoryId, String content)
          throws SQLException, TreatmentException, TransformerException {
        Document doc;
        try {
            doc = XMLUtils.parse(content);
        }
        catch (Exception ex) {
            throw new TreatmentException(ex);
        }
        PreparedStatement pStmt = con.prepareStatement(
              "insert into PM_REPOSITORY_CONTENT (REPOSITORY_CONTENT_ID, REPOSITORY_ID, TREATMENT_ID, CONTENT) values (?, ?, ?, ?)");
        try {
            NodeList nodes = doc.getElementsByTagName(DataProcessConstants.TREATMENT_ENTITY_XML);
            int nbNodes = nodes.getLength();
            for (int i = 0; i < nbNodes; i++) {
                Node node = nodes.item(i);
                String treatmentId = node.getAttributes().getNamedItem("id").getNodeValue();
                if (treatmentId.length() > 50) {
                    throw new TreatmentException(
                          "La taille de l'identifiant d'un traitement ('" + treatmentId
                          + "') dépasse 50 caractères.");
                }

                String contentNode = XMLUtils.nodeToString(node);
                pStmt.setInt(1, SQLUtil.getNextId(con, "PM_REPOSITORY_CONTENT", "REPOSITORY_CONTENT_ID"));
                pStmt.setInt(2, repositoryId);
                pStmt.setString(3, treatmentId);
                pStmt.setString(4, contentNode);
                pStmt.executeUpdate();
            }
        }
        finally {
            pStmt.close();
        }
    }


    public static void deleteRepository(Connection con, int repositoryId) throws SQLException {
        String sql = "delete PM_REPOSITORY_CONTENT where REPOSITORY_ID = ? "
                     + " delete PM_REPOSITORY where REPOSITORY_ID = ?";
        PreparedStatement pStmt = con.prepareStatement(sql);
        try {
            pStmt.setInt(1, repositoryId);
            pStmt.setInt(2, repositoryId);
            pStmt.executeUpdate();
        }
        finally {
            pStmt.close();
        }
    }


    private static String loadFileAsString(String fileName) {
        try {
            StringBuilder sb = new StringBuilder();
            String localPath = TreatmentHelper.class.getResource(fileName).getPath();
            BufferedReader in = new BufferedReader(new FileReader(localPath));
            try {
                String str = in.readLine();
                while (str != null) {
                    sb.append(str).append("\n");
                    str = in.readLine();
                }
                return sb.toString();
            }
            finally {
                in.close();
            }
        }
        catch (Exception ex) {
            LOG.error(ex);
        }
        return null;
    }


    public Object proceedTreatment(Connection con,
                                   String treatmentId,
                                   DataProcessContext context,
                                   String[] repositoryPath,
                                   Object... param)
          throws TreatmentException, SQLException {
        return new TreatmentLauncher().proceedTreatment(con, repositoryPath, treatmentId, context, param);
    }


    public Object proceedTreatmentWithLocalArgument(Connection con,
                                                    String treatmentId,
                                                    DataProcessContext context,
                                                    int repositoryIdForLocalArgument,
                                                    String execListNameForLocalArgument,
                                                    String[] repositoryPath,
                                                    Object... param)
          throws TreatmentException, SQLException {
        return new TreatmentLauncher().proceedTreatmentWithLocalArgument(con,
                                                                         treatmentId,
                                                                         context,
                                                                         repositoryIdForLocalArgument,
                                                                         execListNameForLocalArgument,
                                                                         repositoryPath,
                                                                         param);
    }


    private static class TreatmentFragment {
        private String treatmentId;
        private String contentFragment;


        TreatmentFragment(String treatmentId, String contentFragment) {
            this.treatmentId = treatmentId;
            this.contentFragment = contentFragment;
        }


        public String getTreatmentId() {
            return treatmentId;
        }


        public String getContentFragment() {
            return contentFragment;
        }
    }


    private static int maxLengthTreatmentId(List<TreatmentFragment> treatmentFragmentList) {
        int max = 0;
        for (TreatmentFragment treatmentFragment : treatmentFragmentList) {
            if (treatmentFragment.getTreatmentId().length() > max) {
                max = treatmentFragment.getTreatmentId().length();
            }
        }
        return max;
    }
}
