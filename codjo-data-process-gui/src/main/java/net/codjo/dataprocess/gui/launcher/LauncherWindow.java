package net.codjo.dataprocess.gui.launcher;
import net.codjo.agent.UserId;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.gui.launcher.result.TreatmentResultListener;
import net.codjo.dataprocess.gui.launcher.result.TreatmentStepGui;
import net.codjo.dataprocess.gui.plugin.DataProcessGuiPlugin;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.request.RequestTable;
import javax.swing.JInternalFrame;
/**
 *
 */
public interface LauncherWindow {

    void lockWindow(boolean bb);


    int[] preProceed(ExecutionListModel executionListModel, TreatmentResultListener treatmentResultListener);


    void postProceed(boolean hasWarning, int[] selectedRows) throws RequestException;


    TreatmentStepGui getCurrentTreatmentStepGui();


    JInternalFrame getFrame();


    UserId getUserId();


    DataProcessGuiPlugin getDataProcessGuiPlugin();


    TreatmentStepGui addResultTab(String title, ExecutionListModel executionListModel);


    ExecutionListModel getSelectedExecutionListModel();


    RequestTable getRequestTable();


    void showExecListModelGuiResult(ExecutionListModel executionListModel);


    boolean isReadOnly();
}
