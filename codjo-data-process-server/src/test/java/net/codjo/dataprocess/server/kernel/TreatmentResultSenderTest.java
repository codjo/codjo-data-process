package net.codjo.dataprocess.server.kernel;
import net.codjo.agent.AclMessage;
import net.codjo.agent.AgentMock;
import net.codjo.agent.Aid;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.ArgList;
import net.codjo.dataprocess.common.model.ArgModel;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.workflow.common.message.JobAudit;
import net.codjo.workflow.common.message.JobRequest;
import net.codjo.workflow.common.protocol.JobProtocol;
import net.codjo.workflow.common.protocol.JobProtocolParticipant;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class TreatmentResultSenderTest {
    private AgentMock participantAgentMock;
    private JobProtocolParticipant jobProtocolParticipant;
    private JdbcFixture jdbc;


    @Before
    public void before() throws Exception {
        jdbc = new DatabaseFactory().createJdbcFixture();
        jdbc.doSetUp();
        participantAgentMock = new AgentMock();
        participantAgentMock.mockGetAID(new Aid("participantAgentMock"));
        jobProtocolParticipant = new JobProtocolParticipant() {
        };
        jobProtocolParticipant.setAgent(participantAgentMock);
    }


    @After
    public void after() {
        jdbc.doTearDown();
    }


    @Test
    public void sendMessage() throws Exception {
        AclMessage request = createRequest("requestId");
        jobProtocolParticipant.setRequestMessage(request);

        TreatmentResultSender sender = new TreatmentResultSender(jobProtocolParticipant);
        AbstractTreatment treatment = createTreatment();
        sender.sendMessage(treatment.getTreatmentModel(), new SQLException("mon erreur"));
        JobAudit jobAuditMessage = sender.getJobAuditMessage();

        assertThat(request.getConversationId(), equalTo(jobAuditMessage.getRequestId()));
        AclMessage sentMessage = participantAgentMock.getLastSentMessage();
        assertThat(request.getConversationId(), equalTo(sentMessage.getConversationId()));
        assertThat(request.getProtocol(), equalTo(sentMessage.getProtocol()));
        assertThat(AclMessage.Performative.INFORM, equalTo(sentMessage.getPerformative()));
        assertThat(jobAuditMessage.getType(), equalTo(((JobAudit)sentMessage.getContentObject()).getType()));
        assertThat(jobAuditMessage.getType(), equalTo(JobAudit.Type.MID));
        String encode = jobAuditMessage.getArguments().encode();

        assertThat(encode.contains("targetGuiClassName="), equalTo(true));
        assertThat(encode.contains("treatmentId=traitement 1"), equalTo(true));
        assertThat(encode.contains("status=ERROR"), equalTo(true));
        assertThat(encode.contains("targetGuiClassParameters="), equalTo(true));
        assertThat(encode.contains("error=mon erreur"), equalTo(true));
    }


    @Test
    public void sendInformationMessage() throws Exception {
        AclMessage request = createRequest("requestId");
        jobProtocolParticipant.setRequestMessage(request);

        TreatmentResultSender sender = new TreatmentResultSender(jobProtocolParticipant);
        AbstractTreatment treatment = createTreatment();
        sender.sendInformationMessage(treatment.getTreatmentModel(), "Mon message");
        JobAudit jobAuditMessage = sender.getJobAuditMessage();

        assertThat(request.getConversationId(), equalTo(jobAuditMessage.getRequestId()));
        AclMessage sentMessage = participantAgentMock.getLastSentMessage();
        assertThat(request.getConversationId(), equalTo(sentMessage.getConversationId()));
        assertThat(request.getProtocol(), equalTo(sentMessage.getProtocol()));
        assertThat(AclMessage.Performative.INFORM, equalTo(sentMessage.getPerformative()));
        assertThat(jobAuditMessage.getType(), equalTo(((JobAudit)sentMessage.getContentObject()).getType()));
        assertThat(jobAuditMessage.getType(), equalTo(JobAudit.Type.MID));
        String encode = jobAuditMessage.getArguments().encode();
        assertThat(encode.contains("info=Mon message"), equalTo(true));
        assertThat(encode.contains("targetGuiClassName="), equalTo(true));
        assertThat(encode.contains("treatmentId=traitement 1"), equalTo(true));
        assertThat(encode.contains("status=INFO"), equalTo(true));
        assertThat(encode.contains("targetGuiClassParameters="), equalTo(true));
    }


    @Test
    public void createMessage() throws Exception {
        TreatmentResultSender sender = new TreatmentResultSender(jobProtocolParticipant);
        AbstractTreatment treatment = createTreatment();

        JobAudit jobAudit = sender.createMessage(treatment.getTreatmentModel(), null, "");
        String encode = jobAudit.getArguments().encode();
        assertThat(encode.contains("info="), equalTo(true));
        assertThat(encode.contains("targetGuiClassName="), equalTo(true));
        assertThat(encode.contains("treatmentId=traitement 1"), equalTo(true));
        assertThat(encode.contains("status=INFO"), equalTo(true));
        assertThat(encode.contains("targetGuiClassParameters="), equalTo(true));
        assertThat(encode.contains("error="), equalTo(true));

        jobAudit = sender.createMessage(treatment.getTreatmentModel(), new SQLException("mon erreur"),
                                        "mon message");
        String encode1 = jobAudit.getArguments().encode();
        assertThat(encode1.contains("targetGuiClassName="), equalTo(true));
        assertThat(encode1.contains("treatmentId=traitement 1"), equalTo(true));
        assertThat(encode1.contains("status=ERROR"), equalTo(true));
        assertThat(encode1.contains("targetGuiClassParameters="), equalTo(true));
        assertThat(encode1.contains("error=mon erreur"), equalTo(true));

        jobAudit = sender.createMessage(treatment.getTreatmentModel(), null, "mon message");
        String encode2 = jobAudit.getArguments().encode();
        assertThat(encode2.contains("info=mon message"), equalTo(true));
        assertThat(encode2.contains("targetGuiClassName="), equalTo(true));
        assertThat(encode2.contains("treatmentId=traitement 1"), equalTo(true));
        assertThat(encode2.contains("status=INFO"), equalTo(true));
        assertThat(encode2.contains("targetGuiClassParameters="), equalTo(true));
        assertThat(encode2.contains("error="), equalTo(true));

        sender = new TreatmentResultSender(null);
        sender.sendInformationMessage(treatment.getTreatmentModel(), "mon message");
    }


    private static ArgList buildArgsForQuery() {
        ArgList argList = new ArgList();

        List<ArgModel> list = new ArrayList<ArgModel>();
        list.add(new ArgModel("CODE_PORTEFEUILLE", "$portfolioCode$", 1, Types.VARCHAR));
        list.add(new ArgModel("QUANTITE", "0.12345", 2, Types.NUMERIC));
        list.add(new ArgModel("CODE", "$code$", 3, Types.NUMERIC));

        argList.setArgs(list);
        return argList;
    }


    private AbstractTreatment createTreatment() throws TreatmentException {
        Connection con = jdbc.getConnection();
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setId("traitement 1");
        treatmentModel.setType(DataProcessConstants.SQL_QUERY_TYPE);
        ArgList argList = buildArgsForQuery();
        treatmentModel.setArguments(argList);

        ExecutionListModel executionListModel = new ExecutionListModel();
        return TreatmentFactory.buildTreatment(con, treatmentModel, 1, executionListModel);
    }


    private static AclMessage createRequest(String conversationId) {
        AclMessage request = new AclMessage(AclMessage.Performative.REQUEST);
        request.setConversationId(conversationId);
        request.setProtocol(JobProtocol.ID);
        JobRequest jobRequest = new JobRequest();
        jobRequest.setType("data-process");
        jobRequest.setId(conversationId);
        request.setContentObject(jobRequest);
        return request;
    }
}
