package net.codjo.dataprocess.server.plugin;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.message.DataProcessJobRequest;
import net.codjo.dataprocess.server.kernel.TreatmentLauncher;
import net.codjo.workflow.common.protocol.JobProtocolParticipant;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
public interface DataProcessJobLauncher {
    void proceed(Connection con,
                 JobProtocolParticipant jobProtocolParticipant,
                 DataProcessJobRequest request,
                 TreatmentLauncher treatmentLauncher) throws TreatmentException, SQLException;
}
