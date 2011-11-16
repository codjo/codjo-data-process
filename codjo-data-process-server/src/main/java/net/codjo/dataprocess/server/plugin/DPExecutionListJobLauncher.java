package net.codjo.dataprocess.server.plugin;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.codec.ExecutionListModelCodec;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.context.DataProcessContextCodec;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.message.DataProcessJobRequest;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.report.OperationReport;
import net.codjo.dataprocess.server.kernel.TreatmentLauncher;
import net.codjo.workflow.common.message.Arguments;
import net.codjo.workflow.common.message.JobAudit;
import net.codjo.workflow.common.protocol.JobProtocolParticipant;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
public class DPExecutionListJobLauncher implements DataProcessJobLauncher {
    private static final String OPERATION_REPORT_XML = "operationReportXml";


    public void proceed(Connection con,
                        JobProtocolParticipant jobProtocolParticipant,
                        DataProcessJobRequest request,
                        TreatmentLauncher treatmentLauncher)
          throws TreatmentException, SQLException {
        Log.debug(getClass(), ">>> Début exécution du job data-process (mode liste de traitements)");

        DataProcessContext context = DataProcessContextCodec.decode(request.getContext());
        ExecutionListModel executionListModel =
              new ExecutionListModelCodec().decode(request.getExecutionListModel());
        OperationReport report =
              treatmentLauncher.proceedTreatmentList(con,
                                                     Integer.parseInt(request.getRepositoryId()),
                                                     executionListModel,
                                                     context,
                                                     jobProtocolParticipant);

        JobAudit audit = new JobAudit(JobAudit.Type.MID);
        Arguments arguments = new Arguments();
        arguments.put(DataProcessConstants.OPERATION_REPORT, getResult(report));
        arguments.put(OPERATION_REPORT_XML, OperationReport.encode(report));
        audit.setArguments(arguments);
        jobProtocolParticipant.sendAudit(audit);

        Log.debug(getClass(), ">>> Fin d'exécution du job data-process (mode liste de traitements)");
    }


    private static String getResult(OperationReport report) {
        if (report.isError()) {
            return DataProcessConstants.TRT_WARNING;
        }
        else {
            return DataProcessConstants.TRT_OK;
        }
    }
}
