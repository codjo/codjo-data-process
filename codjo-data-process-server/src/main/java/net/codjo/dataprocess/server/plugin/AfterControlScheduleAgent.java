package net.codjo.dataprocess.server.plugin;
import net.codjo.control.server.plugin.ControlServerPlugin;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.message.DataProcessJobRequest;
import net.codjo.imports.common.message.ImportJobAuditArgument;
import net.codjo.workflow.common.message.JobRequest;
import net.codjo.workflow.common.message.ScheduleContract;
import net.codjo.workflow.server.api.ScheduleAgent;
/**
 *
 */
public class AfterControlScheduleAgent extends ScheduleAgent {
    public AfterControlScheduleAgent() {
        super(new LaunchDataProcessAfterImport());
    }


    private static class LaunchDataProcessAfterImport extends ScheduleAgent.AbstractHandler
          implements ImportJobAuditArgument {
        public boolean acceptContract(ScheduleContract contract) {
            JobRequest request = contract.getRequest();
            return ControlServerPlugin.CONTROL_REQUEST_TYPE.equals(request.getType());
        }


        public JobRequest createNextRequest(ScheduleContract contract) {
            DataProcessJobRequest request = new DataProcessJobRequest();
            request.setDataProcessJobType(DataProcessConstants.BATCH_JOB_TYPE);
            return request.toRequest();
        }
    }
}
