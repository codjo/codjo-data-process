package net.codjo.dataprocess.server.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.Aid;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.codec.ExecutionListModelCodec;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.context.DataProcessContextCodec;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.message.DataProcessJobRequest;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.report.OperationReport;
import net.codjo.dataprocess.server.kernel.TreatmentLauncher;
import net.codjo.dataprocess.server.plugin.DataProcessJobAgent.DefaultDataProcessLauncherFactory;
import net.codjo.sql.server.JdbcServiceUtilMock;
import net.codjo.test.common.LogString;
import net.codjo.workflow.common.message.JobAudit.Status;
import net.codjo.workflow.common.message.JobAudit.Type;
import net.codjo.workflow.common.protocol.JobProtocol;
import net.codjo.workflow.common.protocol.JobProtocolParticipant;
import net.codjo.workflow.server.api.JobAgent.MODE;
import net.codjo.workflow.server.api.WorkflowTestCase;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/**
 *
 */
public class DataProcessJobAgentTest extends WorkflowTestCase {
    private LogString logString = new LogString();


    @Test
    public void test_delegate() throws Exception {
        story.record()
              .startAgent("dataprocess-job-agent",
                          new DataProcessJobAgent(new JdbcServiceUtilMock(new LogString()),
                                                  new DefaultDataProcessLauncherFactory(),
                                                  new TreatmentLauncherMock(),
                                                  MODE.DELEGATE));
        AclMessage requestMessage = new AclMessage(AclMessage.Performative.REQUEST);
        requestMessage.setConversationId("conversation-id");
        requestMessage.setProtocol(JobProtocol.ID);
        requestMessage.addReceiver(new Aid("dataprocess-job-agent"));

        DataProcessJobRequest request = new DataProcessJobRequest();
        request.setDataProcessJobType(DataProcessConstants.EXECUTION_LIST_JOB_TYPE);
        DataProcessContext context = new DataProcessContext();
        context.setProperty("periode", "200512");
        request.setContext(DataProcessContextCodec.encode(context));
        ExecutionListModel listModel = new ExecutionListModel();
        listModel.setName("listModel1");
        listModel.setFamilyId(1);
        listModel.setPriority(1);
        request.setExecutionListModel(new ExecutionListModelCodec().encode(listModel));
        request.setRepositoryId("1");

        requestMessage.setContentObject(request.toRequest());

        story.record().startTester("schedule-leader")
              .sendMessage(requestMessage)
              .then()
              .receiveMessage(containsAudit(Type.PRE, Status.OK))
              .then()
              .receiveMessage(containsAudit(Type.POST, Status.OK));
        story.execute();
        story.getAgentContainerFixture().waitForAgentDeath("dataprocess-job-agent");

        logString.assertContent("proceedTreatmentList(connection, 1, listModel1, {periode=200512})");
    }


    @Before
    public void before() throws Exception {
        story.doSetUp();
    }


    @After
    public void after() throws Exception {
        story.doTearDown();
    }


    class TreatmentLauncherMock extends TreatmentLauncher {
        @Override
        public OperationReport proceedTreatmentList(Connection con,
                                                    int repositoryId,
                                                    ExecutionListModel executionListModel,
                                                    DataProcessContext context,
                                                    JobProtocolParticipant jobProtocolParticipant)
              throws TreatmentException, SQLException {
            logString.call("proceedTreatmentList", "connection", repositoryId, executionListModel.getName(),
                           context);
            return new OperationReport();
        }
    }
}
