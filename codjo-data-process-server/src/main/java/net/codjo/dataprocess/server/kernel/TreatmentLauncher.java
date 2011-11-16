/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.kernel;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.codec.ListCodec;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.common.model.UserTreatment;
import net.codjo.dataprocess.common.report.OperationReport;
import net.codjo.dataprocess.common.report.TreatmentReport;
import net.codjo.dataprocess.server.dao.ContextDao;
import net.codjo.dataprocess.server.dao.StatusDao;
import net.codjo.dataprocess.server.dao.TreatmentDao;
import net.codjo.dataprocess.server.repository.Repository;
import net.codjo.workflow.common.protocol.JobProtocolParticipant;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static net.codjo.dataprocess.common.DataProcessConstants.DONE;
import static net.codjo.dataprocess.common.DataProcessConstants.FAILED;
import static net.codjo.dataprocess.common.DataProcessConstants.TO_DO;
/**
 *
 */
public class TreatmentLauncher {

    public Object proceedTreatment(Connection con,
                                   int repositoryId,
                                   String treatmentId,
                                   DataProcessContext context,
                                   Object... param)
          throws TreatmentException, SQLException {
        TreatmentModel treatmentModel = Repository.getTreatmentById(con, repositoryId, treatmentId);
        AbstractTreatment treatment = TreatmentFactory.buildTreatment(con, treatmentModel, repositoryId);
        treatment.configure(context);
        return treatment.proceedTreatment(context, param);
    }


    public Object proceedTreatment(Connection con,
                                   String[] repositoryPath,
                                   String treatmentId,
                                   DataProcessContext context,
                                   Object... param)
          throws TreatmentException, SQLException {
        TreatmentModel treatmentModel = Repository.getTreatmentById(repositoryPath, treatmentId);
        AbstractTreatment treatment = TreatmentFactory.buildTreatment(con, treatmentModel);
        treatment.configure(context);
        return treatment.proceedTreatment(context, param);
    }


    public Object proceedTreatmentWithLocalArgument(Connection con,
                                                    String treatmentId,
                                                    DataProcessContext context,
                                                    int repositoryIdForLocalArgument,
                                                    String execListNameForLocalArgument,
                                                    String[] repositoryPath,
                                                    Object... param)
          throws TreatmentException, SQLException {
        if (execListNameForLocalArgument == null || repositoryIdForLocalArgument == 0) {
            throw new IllegalArgumentException(
                  "Erreur: RepositoryId doit être != 0 et executionListName doit être != null");
        }

        TreatmentModel treatmentModel = Repository.getTreatmentById(repositoryPath, treatmentId);
        ExecutionListModel executionListModelForLocalArgument = new ExecutionListModel();
        executionListModelForLocalArgument.setName(execListNameForLocalArgument);
        AbstractTreatment treatment = TreatmentFactory.buildTreatment(con,
                                                                      treatmentModel,
                                                                      repositoryIdForLocalArgument,
                                                                      executionListModelForLocalArgument);
        treatment.configure(context);
        return treatment.proceedTreatment(context, param);
    }


    public OperationReport proceedTreatmentList(Connection con,
                                                int repositoryId,
                                                ExecutionListModel executionListModel,
                                                DataProcessContext context,
                                                JobProtocolParticipant jobProtocolParticipant)
          throws TreatmentException, SQLException {
        OperationReport operationReport = new OperationReport();
        StatusDao statusDao = new StatusDao();
        TreatmentResultSender treatmentResultSender = new TreatmentResultSender(jobProtocolParticipant);

        List<UserTreatment> userTreatmentList =
              UserTreatment.orderList(UserTreatment.buildUserTrtListWithPriority(executionListModel.getPriorityMap()));
        statusDao.updateExecutionListStatus(con, executionListModel, context, TO_DO, true);

        if (!userTreatmentList.isEmpty()) {
            for (UserTreatment userTrt : userTreatmentList) {
                TreatmentReport treatmentReport = new TreatmentReport(userTrt.getId());
                TreatmentModel treatmentModel = Repository.getTreatmentById(con, repositoryId,
                                                                            userTrt.getId());
                AbstractTreatment currentTrt = null;
                try {
                    statusDao.updateExecutionListStatus(con, executionListModel, context, TO_DO, false);
                    statusDao.updateTreatmentStatus(con, executionListModel, treatmentModel, context, TO_DO);
                    currentTrt = TreatmentFactory.buildTreatment(con, treatmentModel, repositoryId,
                                                                 executionListModel);
                    currentTrt.setTreatmentResultSender(treatmentResultSender);
                    currentTrt.configure(context);
                    Object result = currentTrt.proceedTreatment(context);
                    currentTrt.setResult(result);
                    treatmentReport.setResult(result != null ? result.toString() : null);
                    treatmentResultSender.sendMessage(treatmentModel);
                    statusDao.updateExecutionListStatus(con, executionListModel, context, DONE, false);
                    statusDao.updateTreatmentStatus(con, executionListModel, treatmentModel, context, DONE);
                }
                catch (Exception ex) {
                    Log.warn(getClass(), "\n**************************************************\n"
                                         + "Echec du traitement '" + treatmentModel.getTitle()
                                         + "' : \n" + ex.getLocalizedMessage() +
                                         "\n**************************************************\n", ex);
                    treatmentReport.setErrorMessage(ex.getLocalizedMessage());
                    statusDao.updateExecutionListStatus(con, executionListModel, context, FAILED, false);
                    statusDao.updateTreatmentStatus(con, executionListModel, treatmentModel, context,
                                                    FAILED, ex.getLocalizedMessage());
                    treatmentResultSender.sendMessage(treatmentModel, ex);
                    if (currentTrt != null) {
                        currentTrt.setError(ex);
                    }
                }
                finally {
                    operationReport.addTreatmentReport(treatmentReport);
                }
            }
        }
        else {
            statusDao.updateExecutionListStatus(con, executionListModel, context, DONE, true);
            treatmentResultSender.sendMessage(null);
        }
        return operationReport;
    }


    public OperationReport proceedTreatmentList(Connection con,
                                                int repositoryId,
                                                int familyId,
                                                String executionListName,
                                                DataProcessContext localContext,
                                                boolean loadDefaultContext)
          throws TreatmentException, SQLException {
        TreatmentDao treatmentDao = new TreatmentDao();
        ExecutionListModel executionListModel = treatmentDao.getExecutionListModel(con,
                                                                                   executionListName,
                                                                                   repositoryId,
                                                                                   familyId);
        DataProcessContext context = new DataProcessContext();
        if (loadDefaultContext) {
            ContextDao contextDao = new ContextDao();
            DataProcessContext defaultContext
                  = contextDao.getDataProcessContext(con, String.valueOf(repositoryId));
            context.addContext(defaultContext);
            context.addContext(localContext);
        }
        else {
            context.addContext(localContext);
        }
        return proceedTreatmentList(con, repositoryId, executionListModel, context, null);
    }


    public static String getNotResolvableArguments(Connection con,
                                                   int repositoryId,
                                                   ExecutionListModel executionListModel,
                                                   DataProcessContext context) {
        SortedSet<String> parametersList = new TreeSet<String>();

        List<UserTreatment> userTreatmentList =
              UserTreatment.orderList(UserTreatment.buildUserTrtListWithPriority(executionListModel.getPriorityMap()));

        try {
            for (UserTreatment userTrt : userTreatmentList) {
                TreatmentModel trtModel = Repository.getTreatmentById(con, repositoryId, userTrt.getId());
                AbstractTreatment currentTrt = null;
                try {
                    currentTrt = TreatmentFactory.buildTreatment(con, trtModel, repositoryId,
                                                                 executionListModel);
                }
                catch (TreatmentException ex) {
                    parametersList.add(ex.getLocalizedMessage());
                }
                if (currentTrt != null) {
                    parametersList.addAll(currentTrt.getNotResolvableArguments(context));
                }
            }
        }
        catch (TreatmentException ex) {
            parametersList.add(ex.getLocalizedMessage());
        }
        List<String> list = new ArrayList<String>();
        list.addAll(parametersList);
        return new ListCodec().encode(list);
    }
}
