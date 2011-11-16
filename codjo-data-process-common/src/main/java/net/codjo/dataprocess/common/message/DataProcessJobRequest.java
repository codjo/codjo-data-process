package net.codjo.dataprocess.common.message;
import net.codjo.workflow.common.message.JobRequest;
import net.codjo.workflow.common.message.JobRequestWrapper;
/**
 *
 */
public class DataProcessJobRequest extends JobRequestWrapper {
    public static final String DATA_PROCESS_REQUEST_TYPE = "data-process";
    private static final String EXECUTION_LIST_MODEL = "executionListModel";
    private static final String REPOSITORY_ID = "repositoryId";
    private static final String REPOSITORY_NAME = "repositoryName";
    private static final String FAMILY_ID = "familyId";
    private static final String FAMILY_NAME = "familyName";
    private static final String CONTEXT = "context";
    private static final String DATA_PROCESS_JOB_TYPE = "dataProcessJobType";


    public DataProcessJobRequest() {
        this(new JobRequest());
    }


    public DataProcessJobRequest(JobRequest request) {
        super(DATA_PROCESS_REQUEST_TYPE, request);
    }


    public void setExecutionListModel(String executionListModelXml) {
        setArgument(EXECUTION_LIST_MODEL, executionListModelXml);
    }


    public String getExecutionListModel() {
        return getArgument(EXECUTION_LIST_MODEL);
    }


    public void setRepositoryId(String repositoryId) {
        setArgument(REPOSITORY_ID, repositoryId);
    }


    public String getRepositoryId() {
        return getArgument(REPOSITORY_ID);
    }


    public void setRepositoryName(String repositoryName) {
        setArgument(REPOSITORY_NAME, repositoryName);
    }


    public String getRepositoryName() {
        return getArgument(REPOSITORY_NAME);
    }


    public void setFamilyId(String familyId) {
        setArgument(FAMILY_ID, familyId);
    }


    public String getFamilyId() {
        return getArgument(FAMILY_ID);
    }


    public void setFamilyName(String familyName) {
        setArgument(FAMILY_NAME, familyName);
    }


    public String getFamilyName() {
        return getArgument(FAMILY_NAME);
    }


    public void setContext(String context) {
        setArgument(CONTEXT, context);
    }


    public String getContext() {
        return getArgument(CONTEXT);
    }


    public void setDataProcessJobType(String dataProcessJobType) {
        setArgument(DATA_PROCESS_JOB_TYPE, dataProcessJobType);
    }


    public String getDataProcessJobType() {
        return getArgument(DATA_PROCESS_JOB_TYPE);
    }
}
