package net.codjo.dataprocess.client;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.codec.ExecutionListModelCodec;
import net.codjo.dataprocess.common.codec.ListCodec;
import net.codjo.dataprocess.common.codec.MapXmlCodec;
import net.codjo.dataprocess.common.codec.TreatmentModelCodec;
import net.codjo.dataprocess.common.codec.UserTreatmentListCodec;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.common.model.UserTreatment;
import net.codjo.dataprocess.common.report.OperationReport;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.codjo.dataprocess.common.DataProcessConstants.Command.COPY;
import static net.codjo.dataprocess.common.DataProcessConstants.Command.READ;
/**
 *
 */
public class TreatmentClientHelper {

    private TreatmentClientHelper() {
    }


    public static List<ExecutionListDB> getExecListsByNameAndRepo(MutableGuiContext ctxt,
                                                                  String executionListName,
                                                                  int repositoryId)
          throws RequestException {
        List<ExecutionListDB> executionList = new ArrayList<ExecutionListDB>();
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> selectors = new HashMap<String, String>();
        selectors.put("executionListName", executionListName);
        selectors.put("repositoryId", Integer.toString(repositoryId));
        Result rs = sender.sendSqlCommand(ctxt, "selectExecListsByNameAndRepo", null, selectors);

        for (int i = 0; i < rs.getRowCount(); i++) {
            executionList.add(new ExecutionListDB(rs.getValue(i, FamilyClientHelper.EXECUTION_LIST_ID),
                                                  rs.getValue(i, FamilyClientHelper.REPOSITORY_ID),
                                                  rs.getValue(i, FamilyClientHelper.EXECUTION_LIST_NAME),
                                                  rs.getValue(i, FamilyClientHelper.REPOSITORY_NAME),
                                                  rs.getValue(i, FamilyClientHelper.FAMILY_ID),
                                                  rs.getValue(i, FamilyClientHelper.PRIORITY)));
        }

        return executionList;
    }


    public static String getConfigProperty(MutableGuiContext ctxt, String key) throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("key", key);
        Row row = sender.send(ctxt, arg, "getConfigProperty");
        return row.getFieldValue("result");
    }


    public static Map<String, String> getAllDefaultConfigProperty(MutableGuiContext ctxt)
          throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        Row row = sender.send(ctxt, arg, "getAllDefaultConfigProperty");
        return MapXmlCodec.decode(row.getFieldValue("result"));
    }


    public static void deleteExecutionLists(MutableGuiContext ctxt, int repositoryId, int familyId)
          throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("repositoryId", Integer.toString(repositoryId));
        arg.put("familyId", Integer.toString(familyId));
        sender.send(ctxt, arg, "cmdDeleteExecutionLists");
    }


    public static List<String> getAllTreatmentModelId(MutableGuiContext ctxt, String repositoryId)
          throws RequestException {
        List<String> treatmentModelIdList = new ArrayList<String>();
        Map<String, String> selectors = new HashMap<String, String>();
        selectors.put("repositoryId", repositoryId);
        HandlerCommandSender sender = new HandlerCommandSender();
        Result rs = sender.sendSqlCommand(ctxt, "selectAllTreatmentModelId", null, selectors);
        for (int i = 0; i < rs.getRowCount(); i++) {
            treatmentModelIdList.add(rs.getValue(i, "treatmentId"));
        }
        return treatmentModelIdList;
    }


    public static List<String> getAllUserTreatmentId(MutableGuiContext ctxt, String repositoryId)
          throws RequestException {
        List<String> userTreatmentIdList = new ArrayList<String>();
        Map<String, String> selectors = new HashMap<String, String>();
        selectors.put("repositoryId", repositoryId);
        HandlerCommandSender sender = new HandlerCommandSender();
        Result rs = sender.sendSqlCommand(ctxt, "selectAllUserTreatmentId", null, selectors);
        for (int i = 0; i < rs.getRowCount(); i++) {
            userTreatmentIdList.add(rs.getValue(i, "treatmentId"));
        }
        return userTreatmentIdList;
    }


    public static void copyExecutionListsFromRepoToRepo(MutableGuiContext ctxt,
                                                        int repositoryFrom,
                                                        int repositoryTo) throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("repositoryFrom", Integer.toString(repositoryFrom));
        arg.put("repositoryTo", Integer.toString(repositoryTo));
        sender.send(ctxt, arg, "copyExecutionListsFromRepoToRepo");
    }


    public static String manageTreatmentModel(MutableGuiContext ctxt,
                                              DataProcessConstants.Command command,
                                              String repositoryId,
                                              String treatmentContentXml)
          throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("command", command.toString());
        arg.put("repositoryId", repositoryId);
        arg.put("treatmentContentXml", treatmentContentXml);

        Row row = sender.send(ctxt, arg, "manageTreatmentModel");
        return row.getFieldValue("result");
    }


    public static String copyTreatmentModels(MutableGuiContext ctxt,
                                             String repositoryIdSource,
                                             String repositoryIdDest) throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("command", COPY.toString());
        arg.put("repositoryId", repositoryIdSource);
        arg.put("treatmentContentXml", repositoryIdDest);

        Row row = sender.send(ctxt, arg, "manageTreatmentModel");
        return row.getFieldValue("result");
    }


    public static String getTreatmentModel(MutableGuiContext ctxt, String repositoryId, String treatmentId)
          throws RequestException {
        TreatmentModel trtModelRequested = new TreatmentModel();
        trtModelRequested.setId(treatmentId);
        String trtModelRequestedXml = TreatmentModelCodec.encode(trtModelRequested);

        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("command", READ.toString());
        arg.put("repositoryId", repositoryId);
        arg.put("treatmentContentXml", trtModelRequestedXml);

        Row row = sender.send(ctxt, arg, "manageTreatmentModel");
        return row.getFieldValue("result");
    }


    public static List<UserTreatment> getAllTreatments(MutableGuiContext ctxt, int repositoryId)
          throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("repositoryId", Integer.toString(repositoryId));

        Row row = sender.send(ctxt, arg, "loadUserTreatmentList");
        String result = row.getFieldValue("result");
        return UserTreatmentListCodec.decode(result, true);
    }


    public static List<ExecutionListModel> getExecutionListModel(MutableGuiContext ctxt,
                                                                 int repositoryId,
                                                                 int familyId)
          throws RequestException {
        return getExecutionListModel(ctxt, repositoryId, familyId, null);
    }


    public static List<ExecutionListModel> getExecutionListModel(MutableGuiContext ctxt,
                                                                 int repositoryId,
                                                                 int familyId,
                                                                 String executionListName)
          throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("repositoryId", Integer.toString(repositoryId));
        arg.put("familyId", Integer.toString(familyId));
        if (executionListName != null) {
            arg.put("executionListName", executionListName);
        }
        Row row = sender.send(ctxt, arg, "treatmentStoreListLoader");
        String result = row.getFieldValue("result");

        ExecutionListModelCodec codec = new ExecutionListModelCodec();
        return codec.decodeList(result);
    }


    public static void reinitExecutionList(MutableGuiContext ctxt, String repositoryId)
          throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("repositoryId", repositoryId);
        sender.send(ctxt, arg, "reinitExecutionList");
    }


    public static void saveExecutionListModel(MutableGuiContext ctxt,
                                              int repositoryId,
                                              List<ExecutionListModel> list,
                                              int familyId) throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        ExecutionListModelCodec codec = new ExecutionListModelCodec();
        String trtExecModel = codec.encodeList(list);
        arg.put("executionListModel", trtExecModel);
        arg.put("repositoryId", Integer.toString(repositoryId));
        arg.put("familyId", Integer.toString(familyId));
        sender.send(ctxt, arg, "treatmentStoreListSaver");
    }


    public static List<String> getNotResolvableArguments(MutableGuiContext ctxt, int repositoryId,
                                                         ExecutionListModel executionListModel,
                                                         DataProcessContext context)
          throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        ExecutionListModelCodec codec = new ExecutionListModelCodec();
        String trtExecutionModelAsXml = codec.encode(executionListModel);

        arg.put("repositoryId", Integer.toString(repositoryId));
        arg.put("executionListModel", trtExecutionModelAsXml);
        arg.put("context", context.encode());

        Row row = sender.send(ctxt, arg, "getNotResolvableArguments");
        String result = row.getFieldValue("result");
        return new ListCodec().decode(result);
    }


    public static void updateStatus(MutableGuiContext ctxt, ExecutionListModel executionListModel,
                                    DataProcessContext context, int status)
          throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        ExecutionListModelCodec codec = new ExecutionListModelCodec();
        String trtExecutionModelAsXml = codec.encode(executionListModel);
        arg.put("executionListModel", trtExecutionModelAsXml);
        arg.put("status", Integer.toString(status));
        arg.put("context", context.encode());
        sender.send(ctxt, arg, "treatmentFillStatus");
    }


    public static OperationReport proceedExecutionList(MutableGuiContext ctxt,
                                                       String executionListName,
                                                       int repositoryId,
                                                       int familyId,
                                                       DataProcessContext context,
                                                       boolean loadDefaultContext,
                                                       long timeout)
          throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();

        arg.put("executionListName", executionListName);
        arg.put("repositoryId", Integer.toString(repositoryId));
        arg.put("familyId", Integer.toString(familyId));
        if (context != null) {
            arg.put("context", context.encode());
        }
        else {
            arg.put("context", "NULL");
        }
        arg.put("loadDefaultContext", String.valueOf(loadDefaultContext));

        Row row = sender.send(ctxt, arg, "executionListProcessor", timeout);
        String result = row.getFieldValue("result");
        return OperationReport.decode(result);
    }


    public static String proceedTreatment(MutableGuiContext ctxt,
                                          int repositoryId, String treatmentId,
                                          DataProcessContext context, long timeout)
          throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();

        arg.put("repositoryId", Integer.toString(repositoryId));
        arg.put("treatmentId", treatmentId);
        arg.put("context", context.encode());

        Row row = sender.send(ctxt, arg, "treatmentProcessor", timeout);
        return row.getFieldValue("result");
    }
}
