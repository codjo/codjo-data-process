package net.codjo.dataprocess.server.handlerhelper;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.server.dao.FamilyDao;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
public class FamilyHandlerHelper {
    private FamilyHandlerHelper() {
    }


    public static String newFamily(Connection con, int repositoryId, String familyName) throws SQLException {
        FamilyDao familyDao = new FamilyDao();
        return familyDao.newFamily(con, repositoryId, familyName);
    }


    public static int getFamilyIdFromName(Connection con, int repositoryId, String familyName)
          throws SQLException, TreatmentException {
        FamilyDao familyDao = new FamilyDao();
        return familyDao.getFamilyIdFromName(con, repositoryId, familyName);
    }
}
