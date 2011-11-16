/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.kernel;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.codec.ListCodec;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.workflow.common.message.Arguments;
import net.codjo.workflow.common.message.JobAudit;
import net.codjo.workflow.common.protocol.JobProtocolParticipant;
/**
 *
 */
class TreatmentResultSender {
    private JobProtocolParticipant jobProtocolParticipant;
    private JobAudit jobAuditMessage;


    TreatmentResultSender(JobProtocolParticipant jobProtocolParticipant) {
        this.jobProtocolParticipant = jobProtocolParticipant;
    }


    public void sendMessage(TreatmentModel treatmentModel) {
        sendMessage(treatmentModel, null);
    }


    public void sendMessage(TreatmentModel treatmentModel, Exception exception) {
        sendAudit(createMessage(treatmentModel, exception, null));
    }


    private void sendAudit(final JobAudit jobAudit) {
        if (jobProtocolParticipant != null) {
            jobProtocolParticipant.sendAudit(jobAudit);
        }
        else {
            if (Log.isInfoEnabled()) {
                Log.info(getClass(), "sendAudit : " + jobAudit);
            }
        }
    }


    public void sendInformationMessage(TreatmentModel treatmentModel, String information) {
        sendAudit(createMessage(treatmentModel, null, information));
    }


    JobAudit createMessage(TreatmentModel treatmentModel, Exception exception, String information) {
        JobAudit audit = new JobAudit(JobAudit.Type.MID);
        Arguments arguments = new Arguments();
        arguments.put(DataProcessConstants.MESSAGE_PROP_TREATMENT_ID,
                      treatmentModel != null ? treatmentModel.getId() : "");

        if (treatmentModel != null) {
            arguments.put(DataProcessConstants.MESSAGE_PROP_GUI_CLASS_NAME,
                          treatmentModel.getTargetGuiClassName());
            arguments.put(DataProcessConstants.MESSAGE_PROP_GUI_CLASS_PARAM,
                          new ListCodec().encode(treatmentModel.getTargetGuiClassParameters()));
        }

        if (exception != null) {
            arguments.put(DataProcessConstants.MESSAGE_PROP_STATUS, DataProcessConstants.STATUS_ERROR);
            arguments.put(DataProcessConstants.MESSAGE_PROP_ERROR, exception.getLocalizedMessage());
        }
        else if (information != null) {
            arguments.put(DataProcessConstants.MESSAGE_PROP_STATUS, DataProcessConstants.STATUS_INFORMATION);
            arguments.put(DataProcessConstants.MESSAGE_INFORMATION, information);
            arguments.put(DataProcessConstants.MESSAGE_PROP_ERROR, "");
        }
        else {
            arguments.put(DataProcessConstants.MESSAGE_PROP_STATUS, DataProcessConstants.STATUS_NO_ERROR);
            arguments.put(DataProcessConstants.MESSAGE_PROP_ERROR, "");
        }
        audit.setArguments(arguments);
        jobAuditMessage = audit;
        return audit;
    }


    public JobAudit getJobAuditMessage() {
        return jobAuditMessage;
    }
}
