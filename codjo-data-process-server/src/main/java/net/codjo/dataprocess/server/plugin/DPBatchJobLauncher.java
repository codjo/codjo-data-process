package net.codjo.dataprocess.server.plugin;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.message.DataProcessJobRequest;
import net.codjo.dataprocess.server.kernel.TreatmentLauncher;
import net.codjo.workflow.common.protocol.JobProtocolParticipant;
import java.sql.Connection;
/**
 *
 */
public class DPBatchJobLauncher implements DataProcessJobLauncher {
    public void proceed(Connection con,
                        JobProtocolParticipant jobProtocolParticipant,
                        DataProcessJobRequest request,
                        TreatmentLauncher treatmentLauncher) {
        Log.debug(getClass(), ">>> Début d'exécution du job data-process (mode batch)");

        if (checkIfImportControlOk(con)) {
            ;
        }

        Log.debug(getClass(), ">>> Fin d'exécution du job data-process (mode batch)");
    }


    private boolean checkIfImportControlOk(Connection con) {
        return false;
    }
}
