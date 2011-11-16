/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.launcher;
import net.codjo.agent.AgentContainer;
import net.codjo.agent.ContainerFailureException;
import net.codjo.dataprocess.client.DependencyClientHelper;
import net.codjo.dataprocess.client.FamilyClientHelper;
import net.codjo.dataprocess.client.RepositoryClientHelper;
import net.codjo.dataprocess.client.TreatmentClientHelper;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.codec.ExecutionListModelCodec;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.message.DataProcessJobRequest;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.gui.launcher.result.TreatmentResultListener;
import net.codjo.dataprocess.gui.launcher.result.TreatmentStepGui;
import net.codjo.dataprocess.gui.util.ErrorDialog;
import net.codjo.dataprocess.gui.util.GuiContextUtils;
import net.codjo.dataprocess.gui.util.JukeBox;
import net.codjo.gui.toolkit.swing.SwingWorker;
import net.codjo.gui.toolkit.util.GuiUtil;
import net.codjo.gui.toolkit.util.Modal;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.base.GuiPlugin;
import net.codjo.mad.gui.framework.LocalGuiContext;
import net.codjo.plugin.batch.BatchException;
import net.codjo.workflow.common.schedule.ScheduleLauncher;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
/**
 *
 */
public class ExecutionListProgress extends SwingWorker implements PropertyChangeListener {
    private static final int DEFAULT_TIMEOUT = 1000 * 60 * 60 * 3;
    public static final String EXECUTIONLIST_EVENT = "EXECUTIONLIST_EVENT";
    public static final String INFO_EVENT = "INFO_EVENT";
    public static final String STOP_EVENT = "STOP_EVENT";
    private LocalGuiContext ctxt;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private ExecutionListWaitingWindow execProgressWin;
    private Row[] rows;
    private Map<Integer, ExecutionListModel> executionListsMap;
    private LauncherWindow launcherWindow;
    private boolean stop = false;
    private boolean hasSomeWarning = false;
    private DataProcessContext dataProcessContext;


    public ExecutionListProgress(LocalGuiContext ctxt,
                                 Row[] rows,
                                 Map<Integer, ExecutionListModel> executionListsMap,
                                 LauncherWindow launcherWindow) {
        this.ctxt = ctxt;
        this.rows = rows;
        this.launcherWindow = launcherWindow;
        this.executionListsMap = executionListsMap;

        dataProcessContext = launcherWindow.getDataProcessGuiPlugin().getConfiguration()
              .getDataProcessContext();
        execProgressWin = new ExecutionListWaitingWindow(propertyChangeSupport);
        propertyChangeSupport.addPropertyChangeListener(execProgressWin);
        propertyChangeSupport.addPropertyChangeListener(this);
    }


    private boolean verifyExecutionLists() {
        boolean error = false;
        StringBuilder errorHtml = new StringBuilder();
        try {
            for (Row row : rows) {
                ExecutionListModel executionListModel =
                      executionListsMap.get(new Integer(row.getFieldValue("executionListId")));
                if (!executionListModel.isEmpty()) {
                    List<String> errorList =
                          TreatmentClientHelper.getNotResolvableArguments(ctxt,
                                                                          getCurrentRepository(),
                                                                          executionListModel,
                                                                          dataProcessContext);
                    if (!errorList.isEmpty()) {
                        error = true;
                        errorHtml.append("<font color=\"#660000\"><b>La liste de traitements '");
                        errorHtml.append(executionListModel.getName());
                        errorHtml.append("' ne pourra pas être exécutée correctement :</b></font><UL>");
                        for (String anError : errorList) {
                            errorHtml.append("<LI>").append(anError).append("</LI>");
                        }
                        errorHtml.append("</UL>");
                    }
                }
            }
            if (error) {
                ExecutionListModel executionListModel = new ExecutionListModel();
                executionListModel.setName("liste-fantome");
                TreatmentStepGui treatmentStepGui =
                      launcherWindow.addResultTab("Problèmes détectés avant l'exécution", executionListModel);
                treatmentStepGui.setMode(TreatmentStepGui.HTML_MODE);
                treatmentStepGui.getReport().setText(errorHtml.toString());
            }
        }
        catch (Exception ex) {
            Log.error(getClass(), ex);
            launcherWindow.lockWindow(false);
            failure();
            ErrorDialog.show(launcherWindow.getFrame(), "Une erreur interne est survenue", ex);
        }
        return error;
    }


    private void failure() {
        try {
            JukeBox.playFailureSound();
            execProgressWin.stop(true);
            launcherWindow.getRequestTable().load();
        }
        catch (Exception ex) {
            Log.error(getClass(), ex);
        }
    }


    @Override
    public Object construct() {
        try {
            boolean hasWarning = false;
            launcherWindow.lockWindow(true);

            boolean error = verifyExecutionLists();
            if (error) {
                int result = JOptionPane.showConfirmDialog(ctxt.getDesktopPane(),
                                                           "Il y aura des problèmes liés aux paramétrages lors de l'exécution.\n"
                                                           + "Voulez vous poursuivre l'exécution ?",
                                                           "Demande de confirmation",
                                                           JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.NO_OPTION) {
                    try {
                        launcherWindow.getFrame().setSelected(true);
                        launcherWindow.getRequestTable().load();
                    }
                    catch (PropertyVetoException e) {
                        ;
                    }
                    catch (RequestException ex) {
                        Log.error(getClass(), ex);
                    }
                    launcherWindow.lockWindow(false);
                    return rows;
                }
            }

            for (Row row : rows) {
                if (stop) {
                    break;
                }
                ExecutionListModel executionListModel =
                      executionListsMap.get(new Integer(row.getFieldValue("executionListId")));
                if (executionListModel.isEmpty()) {
                    execute(executionListModel);
                    int result =
                          JOptionPane.showConfirmDialog(ctxt.getDesktopPane(),
                                                        executionListModel.getName()
                                                        + "\n\nVoulez vous poursuivre l'exécution ?",
                                                        "Demande de confirmation",
                                                        JOptionPane.YES_NO_OPTION);

                    if (result == JOptionPane.NO_OPTION) {
                        TreatmentClientHelper.updateStatus(ctxt, executionListModel, dataProcessContext,
                                                           DataProcessConstants.FAILED);
                        hasSomeWarning = true;
                        break;
                    }
                }
                else {
                    hasWarning = execute(executionListModel);
                }
                if (hasWarning) {
                    hasSomeWarning = true;
                }
            }
            try {
                if (hasSomeWarning) {
                    JukeBox.playFailureSound();
                    execProgressWin.stop(true);
                }
                else {
                    JukeBox.playSuccessSound();
                    execProgressWin.stop(false);
                }
                launcherWindow.getRequestTable().load();
            }
            catch (RequestException ex) {
                Log.error(getClass(), ex);
            }
            catch (Exception e) {
                Log.error(getClass(), e);
            }
            launcherWindow.lockWindow(false);
        }
        catch (Exception ex) {
            Log.error(getClass(), ex);
            launcherWindow.lockWindow(false);
            failure();
            ErrorDialog.show(launcherWindow.getFrame(), "Une erreur interne est survenue", ex);
        }
        return rows;
    }


    @Override
    public void start() {
        super.start();
        ctxt.getDesktopPane().add(execProgressWin.getFrame());
        GuiUtil.centerWindow(execProgressWin.getFrame());
        Modal.applyModality(launcherWindow.getFrame(), execProgressWin.getFrame());
        execProgressWin.start();
    }


    @Override
    public void finished() {
        execProgressWin.stop(hasSomeWarning);
    }


    private int getCurrentRepository() {
        int currentRepository;
        if (ctxt.hasProperty(DataProcessConstants.CURRENT_REPOSITORY_PROP)) {
            currentRepository = Integer.parseInt(GuiContextUtils.getCurrentRepository(ctxt));
        }
        else {
            throw new IllegalArgumentException(
                  "L'identifiant du référentiel de traitement courant n'est pas dans le GuiContext !");
        }
        return currentRepository;
    }


    private boolean execute(final ExecutionListModel executionListModel)
          throws TreatmentException, RequestException, ContainerFailureException, BatchException {
        StringBuilder dependencyHtml = new StringBuilder();
        AgentContainer container = ((AgentContainer)ctxt.getProperty(GuiPlugin.AGENT_CONTAINER_KEY));

        if (Log.isInfoEnabled()) {
            Log.info(getClass(), "-----> Début d'exécution de la liste de traitements : "
                                 + executionListModel.getName());
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                propertyChangeSupport.firePropertyChange(EXECUTIONLIST_EVENT, null, executionListModel);
            }
        });

        TreatmentResultListener treatmentResultListener = new TreatmentResultListener(propertyChangeSupport);

        int[] selectedRows = launcherWindow.preProceed(executionListModel, treatmentResultListener);
        boolean hasWarning;

        int repositoryId = getCurrentRepository();
        boolean isExecutable = DependencyClientHelper.isExecutable(ctxt, repositoryId,
                                                                   executionListModel.getName());
        if (isExecutable) {
            DataProcessJobRequest request = buildDataProcessJobRequest(executionListModel);
            ScheduleLauncher launcher = new ScheduleLauncher(launcherWindow.getUserId());
            launcher.getWorkflowConfiguration().setDefaultTimeout(DEFAULT_TIMEOUT);
            launcher.setJobEventHandler(treatmentResultListener);
            launcher.executeWorkflow(container, request.toRequest());
            hasWarning = treatmentResultListener.hasWarning();
            DependencyClientHelper.updateImplication(ctxt, repositoryId, executionListModel.getName(),
                                                     DataProcessConstants.TO_DO);
        }
        else {
            hasWarning = true;
            dependencyHtml.append("<font color=\"#660000\"><b>'");
            dependencyHtml.append(executionListModel.getName());
            dependencyHtml.append("' ne peut pas être exécuté !</b></font><br>Dépendances d'exécution :<UL>");
            List<String> executionListDependency =
                  DependencyClientHelper.findDependency(ctxt, repositoryId, executionListModel.getName())
                        .getExecutionList();
            for (String anExecutionListDependency : executionListDependency) {
                dependencyHtml.append("<LI>").append(anExecutionListDependency).append("</LI>");
            }
            dependencyHtml.append("</UL>");
            TreatmentClientHelper.updateStatus(ctxt, executionListModel, dataProcessContext,
                                               DataProcessConstants.FAILED_DEPENDENCY);
            TreatmentStepGui currentTreatmentStepGui = launcherWindow.getCurrentTreatmentStepGui();
            currentTreatmentStepGui.setMode(TreatmentStepGui.HTML_MODE);
            currentTreatmentStepGui.getReport().setText(dependencyHtml.toString());
        }

        launcherWindow.postProceed(hasWarning, selectedRows);
        if (Log.isInfoEnabled()) {
            Log.info(getClass(), "------> Fin d'exécution de la liste de traitements : "
                                 + executionListModel.getName());
        }
        return hasWarning;
    }


    private DataProcessJobRequest buildDataProcessJobRequest(ExecutionListModel executionListModel)
          throws TreatmentException {
        DataProcessJobRequest request = new DataProcessJobRequest();
        request.setDataProcessJobType(DataProcessConstants.EXECUTION_LIST_JOB_TYPE);
        request.setExecutionListModel(new ExecutionListModelCodec().encode(executionListModel));
        request.setRepositoryId(String.valueOf(getCurrentRepository()));
        request.setContext(dataProcessContext.encode());
        try {
            String repositoryName =
                  RepositoryClientHelper.getRepositoryName(ctxt, String.valueOf(getCurrentRepository()));
            request.setRepositoryName(repositoryName);
            Map<String, String> familyMap = FamilyClientHelper.getFamilyByRepositoryId(ctxt,
                                                                                       getCurrentRepository());
            request.setFamilyName(familyMap.get(String.valueOf(executionListModel.getFamilyId())));
        }
        catch (Exception ex) {
            Log.error(getClass(), ex);
        }
        return request;
    }


    public void propertyChange(PropertyChangeEvent evt) {
        String type = evt.getPropertyName();
        if (STOP_EVENT.equals(type)) {
            stop = true;
        }
    }
}
