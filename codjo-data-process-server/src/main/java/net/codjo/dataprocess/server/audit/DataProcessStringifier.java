package net.codjo.dataprocess.server.audit;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.codec.ExecutionListModelCodec;
import net.codjo.dataprocess.common.message.DataProcessJobRequest;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.workflow.common.message.JobRequest;
import net.codjo.workflow.server.plugin.StringifierImpl;
/**
 *
 */
public class DataProcessStringifier extends StringifierImpl {
    public DataProcessStringifier() {
        super(DataProcessJobRequest.DATA_PROCESS_REQUEST_TYPE);
    }


    public String toString(JobRequest jobRequest) {
        String result = "";
        DataProcessJobRequest request = new DataProcessJobRequest(jobRequest);
        String dataProcessJobType = request.getDataProcessJobType();
        if (DataProcessConstants.EXECUTION_LIST_JOB_TYPE.equals(dataProcessJobType)) {
            try {
                ExecutionListModel executionListModel
                      = new ExecutionListModelCodec().decode(request.getExecutionListModel());
                result = String.format("%s/%s/%s",
                                       request.getRepositoryName(),
                                       request.getFamilyName(),
                                       executionListModel.getName());
            }
            catch (Exception ex) {
                Log.error(getClass(), "Erreur lors de la création du DataProcessStringifier", ex);
                result = String.format("%s/%s/#erreur décodage (xstream) de la liste de traitment#",
                                       request.getRepositoryName(),
                                       request.getFamilyName());
            }
        }
        else if (DataProcessConstants.BATCH_JOB_TYPE.equals(dataProcessJobType)) {
            result = "dataProcessJobType=" + DataProcessConstants.BATCH_JOB_TYPE;
        }
        return result;
    }
}
