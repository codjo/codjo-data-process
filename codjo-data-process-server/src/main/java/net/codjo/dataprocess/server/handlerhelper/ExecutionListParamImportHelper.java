package net.codjo.dataprocess.server.handlerhelper;
import net.codjo.dataprocess.common.codec.ExecutionListParamExportCodec;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.ExecutionListParamExport;
import net.codjo.dataprocess.common.model.ExecutionListParamExport.Family;
import net.codjo.dataprocess.common.util.ExecListParamImportReport;
import net.codjo.dataprocess.common.util.ExecListParamImportReport.ErrorType;
import net.codjo.dataprocess.server.dao.FamilyDao;
import net.codjo.dataprocess.server.dao.RepositoryDao;
import net.codjo.dataprocess.server.dao.TreatmentDao;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 *
 */
public class ExecutionListParamImportHelper {
    private ExecListParamImportReport paramImportReport = new ExecListParamImportReport();


    public String importExecutionListParam(Connection con, String contentXml, boolean createMissingFamily)
          throws SQLException, TreatmentException {
        ExecutionListParamExport paramExport = new ExecutionListParamExportCodec().decode(contentXml);
        String repositoryName = paramExport.getName();

        RepositoryDao repositoryDao = new RepositoryDao();
        int repositoryId = repositoryDao.getRepositoryIdFromName(con, repositoryName);

        if (!checkFamily(con, repositoryId, paramExport, createMissingFamily)
            || !checkExecList(con, repositoryId, repositoryName, paramExport)) {
            return paramImportReport.encode();
        }
        FamilyDao familyDao = new FamilyDao();
        Map<String, String> familyMap = familyDao.getFamilyMap(con, paramExport.getName());
        paramExport.updateExecutionListFamilyId(familyMap);
        TreatmentDao treatmentDao = new TreatmentDao();
        for (Family family : paramExport.getFamilyList()) {
            treatmentDao.insertExecutionListModel(con,
                                                  repositoryId,
                                                  repositoryName,
                                                  family.getExecutionListModelList());
        }
        return paramImportReport.encode();
    }


    private boolean checkExecList(Connection con,
                                  int repositoryId,
                                  String repositoryName,
                                  ExecutionListParamExport paramExport)
          throws SQLException {
        List<String> allReadyExistExecList = new ArrayList<String>();
        TreatmentDao treatmentDao = new TreatmentDao();
        List<String> execListNameList = treatmentDao.getRepositoryExecutionList(con, repositoryId);

        for (Family family : paramExport.getFamilyList()) {
            for (ExecutionListModel execListModel : family.getExecutionListModelList()) {
                if (execListNameList.contains(execListModel.getName())) {
                    allReadyExistExecList.add(execListModel.getName());
                }
            }
        }
        if (!allReadyExistExecList.isEmpty()) {
            paramImportReport.setErrorType(ErrorType.EXECUTION_LIST_ALLREADY_EXIST);
            paramImportReport.setErrorMessage(String.format(
                  "Certaines listes de traitements (leurs noms sont identiques) existent déjà dans le repository '%s'.",
                  repositoryName));
            paramImportReport.setAllreadyExistExecutionList(allReadyExistExecList);
            return false;
        }
        else {
            return true;
        }
    }


    private boolean checkFamily(Connection con, int repositoryId, ExecutionListParamExport paramExport,
                                boolean createMissingFamily) throws SQLException {
        FamilyDao familyDao = new FamilyDao();
        Map<String, String> familyMap = familyDao.getFamilyMap(con, paramExport.getName());
        List<String> missingFamilyList = new ArrayList<String>();
        if (paramExport.getFamilyList() == null) {
            paramImportReport.setErrorType(ErrorType.NO_FAMILY);
            paramImportReport.setErrorMessage("Il n'y a aucune famille à importer.");
            return false;
        }
        for (Family family : paramExport.getFamilyList()) {
            String familyName = family.getName();
            if (!familyMap.values().contains(familyName)) {
                missingFamilyList.add(familyName);
                if (createMissingFamily) {
                    familyDao.newFamily(con, repositoryId, familyName);
                }
            }
        }
        if (!createMissingFamily && !missingFamilyList.isEmpty()) {
            paramImportReport.setErrorType(ErrorType.FAMILY_DONT_EXIST);
            paramImportReport.setErrorMessage("Des familles n'existent pas. Veuillez les créer svp.");
            paramImportReport.setMissingFamilyList(missingFamilyList);
            return false;
        }
        return true;
    }
}
