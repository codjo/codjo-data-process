package net.codjo.dataprocess.client;
import net.codjo.dataprocess.common.codec.ExecutionListParamExportCodec;
import net.codjo.dataprocess.common.exception.RepositoryException;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.ExecutionListParamExport;
import net.codjo.dataprocess.common.util.ExecListParamImportReport;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 */
public class ExecutionListClientHelper {
    private ExecutionListClientHelper() {
    }


    public static String executionListParamExport(MutableGuiContext ctxt, int repositoryId)
          throws RepositoryException, RequestException {
        String repositoryName = RepositoryClientHelper.getRepositoryName(ctxt,
                                                                         String.valueOf(repositoryId));
        Map<String, String> familyMap = FamilyClientHelper.getFamilyByRepositoryId(ctxt, repositoryId);
        List<ExecutionListModel> listModel = TreatmentClientHelper.getExecutionListModel(ctxt,
                                                                                         repositoryId,
                                                                                         0);
        ExecutionListParamExport exportModel = new ExecutionListParamExport(repositoryName,
                                                                            listModel,
                                                                            familyMap);
        return new ExecutionListParamExportCodec().encode(exportModel);
    }


    public static ExecListParamImportReport executionListParamImport(MutableGuiContext ctxt,
                                                                     String content,
                                                                     boolean createMissingFamily)
          throws RequestException {
        HandlerCommandSender sender = new HandlerCommandSender();
        Map<String, String> arg = new HashMap<String, String>();
        arg.put("content", content);
        arg.put("createMissingFamily", Boolean.toString(createMissingFamily));
        Row row = sender.send(ctxt, arg, "executionListParamImport");
        return ExecListParamImportReport.decode(row.getFieldValue("result"));
    }
}
