/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.plugin;
import net.codjo.agent.DFService;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.message.DataProcessJobRequest;
import net.codjo.dataprocess.server.kernel.TreatmentLauncher;
import net.codjo.sql.server.ConnectionPool;
import net.codjo.sql.server.JdbcServiceUtil;
import net.codjo.workflow.common.message.JobException;
import net.codjo.workflow.common.message.JobRequest;
import net.codjo.workflow.common.protocol.JobProtocolParticipant;
import net.codjo.workflow.server.api.JobAgent;
import java.sql.Connection;
import java.sql.SQLException;

import static net.codjo.dataprocess.server.plugin.DataProcessServerPlugin.DATA_PROCESS_REQUEST_TYPE;
import static net.codjo.workflow.server.api.JobAgent.MODE.NOT_DELEGATE;
/**
 *
 */
public class DataProcessJobAgent extends JobAgent {
    public DataProcessJobAgent(JdbcServiceUtil jdbcServiceUtil) {
        this(jdbcServiceUtil, new DefaultDataProcessLauncherFactory(), new TreatmentLauncher(), NOT_DELEGATE);
    }


    public DataProcessJobAgent(JdbcServiceUtil jdbcServiceUtil,
                               DataProcessLauncherFactory launcherFactory,
                               TreatmentLauncher treatmentLauncher,
                               MODE mode) {
        super(new DataProcessParticipant(jdbcServiceUtil, launcherFactory, treatmentLauncher),
              new DFService.AgentDescription(getDescription()), mode);
    }


    private static DFService.ServiceDescription getDescription() {
        return new DFService.ServiceDescription(DATA_PROCESS_REQUEST_TYPE, "data-process-service");
    }


    private static class DataProcessParticipant extends JobProtocolParticipant {
        private final JdbcServiceUtil jdbcServiceUtil;
        private DataProcessLauncherFactory launcherFactory;
        private TreatmentLauncher treatmentLauncher;


        DataProcessParticipant(JdbcServiceUtil jdbcServiceUtil,
                               DataProcessLauncherFactory launcherFactory,
                               TreatmentLauncher treatmentLauncher) {
            this.jdbcServiceUtil = jdbcServiceUtil;
            this.launcherFactory = launcherFactory;
            this.treatmentLauncher = treatmentLauncher;
        }


        @Override
        protected void executeJob(JobRequest jobRequest) throws JobException {
            try {
                executeImpl(jobRequest);
            }
            catch (Exception ex) {
                throw new JobException("Erreur fatal", ex);
            }
        }


        private void executeImpl(JobRequest jobRequest)
              throws SQLException, TreatmentException, JobException {
            DataProcessJobRequest request = new DataProcessJobRequest(jobRequest);
            ConnectionPool conPool = jdbcServiceUtil.getConnectionPool(getAgent(), getRequestMessage());
            Connection con = conPool.getConnection();

            try {
                DataProcessJobLauncher jobLauncher = launcherFactory.build(request.getDataProcessJobType());
                jobLauncher.proceed(con, this, request, treatmentLauncher);
            }
            finally {
                con.close();
                conPool.releaseConnection(con);
            }
        }
    }

    public static interface DataProcessLauncherFactory {
        DataProcessJobLauncher build(String dataProcessJobType) throws JobException;
    }

    public static class DefaultDataProcessLauncherFactory implements DataProcessLauncherFactory {

        public DataProcessJobLauncher build(String dataProcessJobType) throws JobException {
            if (DataProcessConstants.EXECUTION_LIST_JOB_TYPE.equals(dataProcessJobType)) {
                return new DPExecutionListJobLauncher();
            }
            else if (DataProcessConstants.BATCH_JOB_TYPE.equals(dataProcessJobType)) {
                return new DPBatchJobLauncher();
            }
            else {
                throw new JobException(
                      "Type d'exécution du job data-process non définit ! (" + dataProcessJobType + ")");
            }
        }
    }
}
