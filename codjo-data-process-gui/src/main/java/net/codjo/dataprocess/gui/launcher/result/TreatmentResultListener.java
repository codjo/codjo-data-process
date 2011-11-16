/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.launcher.result;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.gui.launcher.result.table.ResultPanel;
import net.codjo.workflow.common.message.Arguments;
import net.codjo.workflow.common.message.JobAudit;
import net.codjo.workflow.common.message.JobEvent;
import net.codjo.workflow.common.subscribe.JobEventHandler;
import java.beans.PropertyChangeSupport;
/**
 *
 */
public class TreatmentResultListener extends JobEventHandler {
    private ResultPanel resultPanel;
    private PropertyChangeSupport propertyChangeListeners;
    private String operationReport;


    public TreatmentResultListener(PropertyChangeSupport propertyChangeListeners) {
        this.propertyChangeListeners = propertyChangeListeners;
    }


    public boolean hasWarning() {
        return DataProcessConstants.TRT_WARNING.equals(operationReport);
    }


    public void setResultPanel(ResultPanel resultPanel) {
        this.resultPanel = resultPanel;
    }


    @Override
    public boolean receive(JobEvent event) {
        if (event.isAudit()) {
            JobAudit audit = event.getAudit();
            if (audit.getType() == JobAudit.Type.MID) {
                Arguments auditArguments = audit.getArguments();
                operationReport = auditArguments.get(DataProcessConstants.OPERATION_REPORT);
                if (operationReport != null) {
                    return true;
                }
                resultPanel.updateTreatmentResult(auditArguments, propertyChangeListeners);
            }
        }
        return true;
    }
}
