package net.codjo.dataprocess.common.message;
import net.codjo.dataprocess.common.DataProcessConstants;
import java.util.Date;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class DataProcessJobRequestTest {
    @Test
    public void content() {
        DataProcessJobRequest jobRequest = new DataProcessJobRequest();

        jobRequest.setContext("context");
        assertThat(jobRequest.getContext(), equalTo("context"));

        jobRequest.setDataProcessJobType(DataProcessConstants.EXECUTION_LIST_JOB_TYPE);
        assertThat(jobRequest.getDataProcessJobType(), equalTo(DataProcessConstants.EXECUTION_LIST_JOB_TYPE));

        jobRequest.setExecutionListModel("monExecutionListModel");
        assertThat(jobRequest.getExecutionListModel(), equalTo("monExecutionListModel"));

        jobRequest.setFamilyId("familyId");
        assertThat(jobRequest.getFamilyId(), equalTo("familyId"));

        jobRequest.setId("id");
        assertThat(jobRequest.getId(), equalTo("id"));

        jobRequest.setRepositoryName("HB2");
        assertThat(jobRequest.getRepositoryName(), equalTo("HB2"));

        jobRequest.setFamilyName("famille1");
        assertThat(jobRequest.getFamilyName(), equalTo("famille1"));

        Date date = new Date();
        jobRequest.setDate(date);
        assertThat(jobRequest.getDate(), equalTo(date));

        jobRequest.setInitiatorLogin("michel");
        assertThat(jobRequest.getInitiatorLogin(), equalTo("michel"));
    }
}
