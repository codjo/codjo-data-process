/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.handlerhelper;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.codec.ExecutionListModelCodec;
import net.codjo.dataprocess.common.codec.UserTreatmentListCodec;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.context.DataProcessContextCodec;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.common.model.UserTreatment;
import net.codjo.dataprocess.common.report.OperationReport;
import net.codjo.dataprocess.server.dao.StatusDao;
import net.codjo.dataprocess.server.dao.TreatmentDao;
import net.codjo.dataprocess.server.kernel.TreatmentLauncher;
import net.codjo.dataprocess.server.repository.Repository;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 *
 */
public class TreatmentHandlerHelper {
    private TreatmentHandlerHelper() {
    }


    public static String getNotResolvableArguments(Connection con,
                                                   int repositoryId,
                                                   String xmlTrtExecutionModel,
                                                   String contextAsString) {
        ExecutionListModel executionListModel = new ExecutionListModelCodec().decode(xmlTrtExecutionModel);
        DataProcessContext context = DataProcessContextCodec.decode(contextAsString);
        return TreatmentLauncher.getNotResolvableArguments(con, repositoryId, executionListModel, context);
    }


    public static Object proceedTreatment(Connection con,
                                          int repositoryId,
                                          String treatmentId,
                                          String contextAsString,
                                          Object... param)
          throws TreatmentException, SQLException {
        DataProcessContext context = DataProcessContextCodec.decode(contextAsString);
        TreatmentLauncher treatmentLauncher = new TreatmentLauncher();
        return treatmentLauncher.proceedTreatment(con, repositoryId, treatmentId, context, param);
    }


    public static String proceedExecutionList(Connection con,
                                              int repositoryId,
                                              int familyId,
                                              String executionListName,
                                              String contextAsString,
                                              boolean loadDefaultContext)
          throws TreatmentException, SQLException {
        DataProcessContext context = DataProcessContextCodec.decode(contextAsString);
        TreatmentLauncher treatmentLauncher = new TreatmentLauncher();
        OperationReport report = treatmentLauncher.proceedTreatmentList(con,
                                                                        repositoryId,
                                                                        familyId,
                                                                        executionListName,
                                                                        context,
                                                                        loadDefaultContext);
        return OperationReport.encode(report);
    }


    public static void updateExecutionListStatus(Connection con,
                                                 String trtExecutionModel,
                                                 String contextAsString,
                                                 int status) throws SQLException {
        DataProcessContext context = DataProcessContextCodec.decode(contextAsString);
        ExecutionListModel executionListModel = new ExecutionListModelCodec().decode(trtExecutionModel);
        StatusDao statusDao = new StatusDao();
        statusDao.updateExecutionListStatus(con, executionListModel, context, status, true);
    }


    public static String getExecutionListModel(Connection con,
                                               String executionListName,
                                               int repositoryId,
                                               int familyId)
          throws TreatmentException, SQLException {
        TreatmentDao treatmentDao = new TreatmentDao();
        if (executionListName != null) {
            ExecutionListModel execListModel = treatmentDao.getExecutionListModel(con,
                                                                                  executionListName,
                                                                                  repositoryId,
                                                                                  familyId);
            return new ExecutionListModelCodec().encode(execListModel);
        }
        else {
            List<ExecutionListModel> trtExecModel = treatmentDao.getExecutionListModel(con,
                                                                                       repositoryId,
                                                                                       familyId);
            return new ExecutionListModelCodec().encodeList(trtExecModel);
        }
    }


    public static void save(Connection con, String trtExecutionModel, int repositoryId, int familyId)
          throws TreatmentException, SQLException {
        TreatmentDao treatmentDao = new TreatmentDao();
        List<ExecutionListModel> list = new ExecutionListModelCodec().decodeList(trtExecutionModel);
        treatmentDao.save(con, list, repositoryId, familyId);
    }


    public static String getAllTreatments(Connection con, int repositoryId) throws TreatmentException {
        List<UserTreatment> userTrtList = new ArrayList<UserTreatment>();

        Map<String, TreatmentModel> treatmentConfig = Repository.getTreatments(con, repositoryId);
        for (TreatmentModel treatmentModel : treatmentConfig.values()) {
            userTrtList.add(new UserTreatment(treatmentModel));
        }
        return UserTreatmentListCodec.encode(userTrtList, true);
    }


    public static String manageTreatmentModel(Connection con,
                                              DataProcessConstants.Command command,
                                              int repositoryId,
                                              String treatmentContentXml)
          throws TreatmentException, SQLException {
        TreatmentDao treatmentDao = new TreatmentDao();
        return treatmentDao.manageTreatmentModel(con, command, repositoryId, treatmentContentXml);
    }


    public static void reinitExecutionList(Connection con, int repositoryId) throws SQLException {
        TreatmentDao treatmentDao = new TreatmentDao();
        treatmentDao.reinitExecutionList(con, repositoryId);
    }


    public static void copyExecutionListsFromRepoToRepo(Connection con, int repositoryFrom, int repositoryTo)
          throws SQLException {
        TreatmentDao treatmentDao = new TreatmentDao();
        treatmentDao.copyExecutionListsFromRepoToRepo(con, repositoryFrom, repositoryTo);
    }


    public static void deleteExecutionLists(Connection con, int repositoryId, int familyId)
          throws SQLException {
        TreatmentDao treatmentDao = new TreatmentDao();
        treatmentDao.deleteExecutionLists(con, repositoryId, familyId);
    }
}
