package net.codjo.dataprocess.server.dao;
import net.codjo.dataprocess.common.codec.MapXmlCodec;
import net.codjo.dataprocess.common.exception.TreatmentException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static net.codjo.dataprocess.common.DataProcessConstants.KEY_CONFIRMATION_OF_TREATMENT_EXEC;
import static net.codjo.dataprocess.common.DataProcessConstants.KEY_DATE_LAST_IMPORT_REPOSITORY;
/**
 *
 */
public class ConfigDao {
    private static final Map<String, String> CONFIG_KEY_STORE = new HashMap<String, String>();


    static {
        CONFIG_KEY_STORE.put(KEY_CONFIRMATION_OF_TREATMENT_EXEC, "askConfirmationOfTreatmentExecution");
        CONFIG_KEY_STORE.put(KEY_DATE_LAST_IMPORT_REPOSITORY, "");
        CONFIG_KEY_STORE.put("rowcount", "1000");
    }


    public String getConfigProperty(Connection con, String key) throws SQLException, TreatmentException {
        PreparedStatement pstmt =
              con.prepareStatement("select VALEUR from PM_DP_CONFIG where CLE = ? order by VALEUR");
        try {
            pstmt.setString(1, key);
            ResultSet rs = pstmt.executeQuery();
            try {
                if (rs.next()) {
                    return rs.getString("VALEUR");
                }
                else {
                    String result = CONFIG_KEY_STORE.get(key);
                    if (result != null) {
                        return result;
                    }
                    else {
                        throw new TreatmentException("La clé '" + key + "' n'est pas définie.");
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
    }


    public String getAllDefaultConfigProperty() {
        return MapXmlCodec.encode(CONFIG_KEY_STORE);
    }
}
