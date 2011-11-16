/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.repository;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.codec.TreatmentRootCodec;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.common.model.TreatmentRoot;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 */
public class Repository {
    private static final int INITIAL_VALUE_ID = -9999;
    private static final String[] INITIAL_REPOSITORY_PATH = new String[]{"", ""};
    private static final Map<String, TreatmentModel> TREATMENT_MAP = new HashMap<String, TreatmentModel>();
    private static final RepositoryId REPOSITORYID = new RepositoryId(INITIAL_VALUE_ID,
                                                                      INITIAL_REPOSITORY_PATH);


    private Repository() {
    }


    public static Map<String, TreatmentModel> getTreatments(Connection con, int repoId)
          throws TreatmentException {
        if (repoId == REPOSITORYID.getRepositoryId()) {
            if (Log.isDebugEnabled()) {
                Log.debug(Repository.class,
                          "Récupération des données du repository [id = " + repoId + "] à partir du cache.");
            }
            return TREATMENT_MAP;
        }
        try {
            loadAllTreatmentsFromRepository(con, repoId);
            if (Log.isDebugEnabled()) {
                Log.debug(Repository.class, "Récupération des données du repository [id = " + repoId
                                            + "] à partir de la base de données.");
            }
            REPOSITORYID.setRepositoryId(repoId);
            return TREATMENT_MAP;
        }
        catch (SQLException ex) {
            throw new TreatmentException("Erreur lors du chargement du repository [id = " + repoId + "]", ex);
        }
    }


    public static Map<String, TreatmentModel> getTreatments(String[] repositoryPath)
          throws TreatmentException {
        if (Arrays.equals(repositoryPath, REPOSITORYID.getRepositoryPath())) {
            if (Log.isDebugEnabled()) {
                Log.debug(Repository.class,
                          "Récupération des données du repository {path = " + Arrays.asList(repositoryPath)
                          + "} à partir du cache.");
            }
            return TREATMENT_MAP;
        }
        loadAllTreatments(repositoryPath);
        if (Log.isDebugEnabled()) {
            Log.debug(Repository.class, "Chargement du repository path = " + Arrays.asList(repositoryPath)
                                        + ".");
        }
        REPOSITORYID.setRepositoryPath(repositoryPath);
        return TREATMENT_MAP;
    }


    public static TreatmentModel getTreatmentById(Connection con, int repoId, String treatmentId)
          throws TreatmentException {
        Map<String, TreatmentModel> map = getTreatments(con, repoId);
        if (map.containsKey(treatmentId.trim())) {
            return map.get(treatmentId.trim());
        }
        else {
            throw new TreatmentException(
                  String.format("Le traitement '%s' est inexistant dans le repository [id = %s]",
                                treatmentId, repoId));
        }
    }


    public static TreatmentModel getTreatmentById(String[] repositoryPath, String treatmentId)
          throws TreatmentException {
        Map<String, TreatmentModel> map = getTreatments(repositoryPath);
        if (map.containsKey(treatmentId.trim())) {
            return map.get(treatmentId.trim());
        }
        else {
            throw new TreatmentException(
                  String.format("Le traitement '%s' est inexistant dans le repository {path = %s}",
                                treatmentId, Arrays.asList(repositoryPath)));
        }
    }


    public static void loadAllTreatments(String[] repositoryPath) {
        TREATMENT_MAP.clear();
        for (String path : repositoryPath) {
            TreatmentRoot treatRoot = TreatmentRootCodec.decodeFromResources(path);
            List<TreatmentModel> list = treatRoot.getTreatmentModelList();
            for (TreatmentModel trtModel : list) {
                TREATMENT_MAP.put(trtModel.getId(), trtModel);
            }
        }
    }


    public static void loadAllTreatmentsFromRepository(Connection con, int repositoryId) throws SQLException {
        PreparedStatement pStmt = con.prepareStatement(
              "select REPOSITORY_NAME from PM_REPOSITORY where REPOSITORY_ID = ?");
        try {
            pStmt.setInt(1, repositoryId);
            ResultSet rs = pStmt.executeQuery();
            try {
                if (rs.next()) {
                    String result = getRepositoryContent(con, repositoryId);
                    try {
                        TREATMENT_MAP.clear();
                        TreatmentRoot treatmentRoot = TreatmentRootCodec.decode(result);
                        List<TreatmentModel> list = treatmentRoot.getTreatmentModelList();
                        for (TreatmentModel trtModel : list) {
                            TREATMENT_MAP.put(trtModel.getId(), trtModel);
                        }
                    }
                    catch (Exception ex) {
                        throw new IllegalStateException("Erreur interne", ex);
                    }
                }
                else {
                    throw new IllegalStateException(
                          "Le repository [id = " + repositoryId + "] n'existe pas !");
                }
            }
            finally {
                rs.close();
            }
        }
        finally {
            pStmt.close();
        }
    }


    public static void reinitializeRepositoryCache() {
        REPOSITORYID.setRepositoryId(INITIAL_VALUE_ID);
        REPOSITORYID.setRepositoryPath(INITIAL_REPOSITORY_PATH);
        Log.info(Repository.class, "Réinitialisation du cache des référentiels de traitement.");
    }


    public static String getRepositoryContent(Connection con, int repositoryId) throws SQLException {
        StringBuilder content = new StringBuilder("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n<root>");
        PreparedStatement pstmt =
              con.prepareStatement("select CONTENT from PM_REPOSITORY_CONTENT where REPOSITORY_ID = ?");
        try {
            pstmt.setInt(1, repositoryId);
            ResultSet rs = pstmt.executeQuery();
            try {
                while (rs.next()) {
                    String treatmentTag = rs.getString("CONTENT")
                          .replace(DataProcessConstants.SPECIAL_CHAR_REPLACER_N, "\n")
                          .replace(DataProcessConstants.SPECIAL_CHAR_REPLACER_R, "\r");
                    content.append('\n').append(treatmentTag.trim());
                }
                content.append("\n</root>");
                return content.toString();
            }
            finally {
                rs.close();
            }
        }
        finally {
            pstmt.close();
        }
    }


    private static class RepositoryId {
        private int repositoryId;
        private String[] repositoryPath;


        RepositoryId(int repositoryId, String[] repositoryPath) {
            this.repositoryId = repositoryId;
            this.repositoryPath = repositoryPath;
        }


        public void setRepositoryId(int repositoryId) {
            this.repositoryId = repositoryId;
        }


        public int getRepositoryId() {
            return repositoryId;
        }


        public String[] getRepositoryPath() {
            return repositoryPath;
        }


        public void setRepositoryPath(String[] repositoryPath) {
            this.repositoryPath = repositoryPath;
        }
    }
}
