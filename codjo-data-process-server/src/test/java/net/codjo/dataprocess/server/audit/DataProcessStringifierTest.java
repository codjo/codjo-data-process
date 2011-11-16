package net.codjo.dataprocess.server.audit;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.codec.ExecutionListModelCodec;
import net.codjo.dataprocess.common.message.DataProcessJobRequest;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class DataProcessStringifierTest {
    private DataProcessStringifier dataProcessStringifier = new DataProcessStringifier();


    @Test
    public void test_toString_EXECUTION_LIST_JOB_TYPE() throws Exception {
        DataProcessJobRequest dataProcessJobRequest = new DataProcessJobRequest();
        dataProcessJobRequest.setDataProcessJobType(DataProcessConstants.EXECUTION_LIST_JOB_TYPE);
        dataProcessJobRequest.setFamilyName("famille1");
        dataProcessJobRequest.setRepositoryName("HB2");
        ExecutionListModel executionListModel = new ExecutionListModel();
        executionListModel.setName("list1");
        dataProcessJobRequest.setExecutionListModel(new ExecutionListModelCodec().encode(executionListModel));
        assertThat("HB2/famille1/list1",
                   equalTo(dataProcessStringifier.toString(dataProcessJobRequest.toRequest())));
    }


    @Test
    public void test_toString_BATCH_JOB_TYPE() throws Exception {
        DataProcessJobRequest dataProcessJobRequest = new DataProcessJobRequest();
        dataProcessJobRequest.setDataProcessJobType(DataProcessConstants.BATCH_JOB_TYPE);
        dataProcessJobRequest.setFamilyName("famille1");
        dataProcessJobRequest.setRepositoryName("HB2");
        ExecutionListModel executionListModel = new ExecutionListModel();
        executionListModel.setName("list1");
        dataProcessJobRequest.setExecutionListModel(new ExecutionListModelCodec().encode(executionListModel));
        assertThat("dataProcessJobType=BATCH_JOB_TYPE",
                   equalTo(dataProcessStringifier.toString(dataProcessJobRequest.toRequest())));
    }
}
