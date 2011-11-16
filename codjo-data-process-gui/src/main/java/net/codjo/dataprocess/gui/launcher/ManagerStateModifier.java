package net.codjo.dataprocess.gui.launcher;
import net.codjo.dataprocess.client.DependencyClientHelper;
import net.codjo.dataprocess.client.ExecutionListDependency;
import net.codjo.dataprocess.client.TreatmentClientHelper;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.UserTreatment;
import net.codjo.dataprocess.gui.launcher.result.TreatmentStepGui;
import net.codjo.dataprocess.gui.plugin.DataProcessGuiPlugin;
import net.codjo.dataprocess.gui.repository.XmlTreatmentLogic;
import net.codjo.dataprocess.gui.util.GuiContextUtils;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.gui.toolkit.util.GuiUtil;
import net.codjo.gui.toolkit.util.Modal;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
/**
 *
 */
public class ManagerStateModifier {
    private LauncherWindow launcherWindow;
    private JPopupMenu popupMenu = new JPopupMenu();
    private MutableGuiContext ctxt;
    private DataProcessGuiPlugin dataProcessGuiPlugin;


    public ManagerStateModifier(MutableGuiContext ctxt,
                                LauncherWindow launcherWindow,
                                DataProcessGuiPlugin dataProcessGuiPlugin) {
        this.ctxt = ctxt;
        this.launcherWindow = launcherWindow;
        this.dataProcessGuiPlugin = dataProcessGuiPlugin;
        initPopup();
    }


    private void changeState(ExecutionListModel executionList, int newState) {
        try {
            TreatmentClientHelper.updateStatus(ctxt, executionList,
                                               dataProcessGuiPlugin.getConfiguration()
                                                     .getDataProcessContext(),
                                               newState);
            launcherWindow.getRequestTable().load();
            ((AbstractTableModel)launcherWindow.getRequestTable().getModel()).fireTableDataChanged();
        }
        catch (Exception ex) {
            GuiUtils.showErrorDialog(launcherWindow.getFrame(), getClass(), "Erreur interne", ex);
        }
    }


    private void tableMousePressed(MouseEvent ev) {
        if (SwingUtilities.isRightMouseButton(ev)) {
            int row = launcherWindow.getRequestTable().rowAtPoint(ev.getPoint());
            if (row != -1) {
                launcherWindow.getRequestTable().setRowSelectionInterval(row, row);
            }
            maybeShowPopup(ev);
        }
        launcherWindow.showExecListModelGuiResult(launcherWindow.getSelectedExecutionListModel());
    }


    private void initPopup() {
        popupMenu.add(new ShowDependencyAction(false));
        popupMenu.add(new ShowImplicationAction(false));
        popupMenu.addSeparator();
        popupMenu.add(new DesactivableAction("Forcer l'état à : FAIT", true) {
            public void actionPerformed(ActionEvent e) {
                changeState(launcherWindow.getSelectedExecutionListModel(), DataProcessConstants.DONE);
            }
        });
        popupMenu.add(new DesactivableAction("Forcer l'état à : A FAIRE", true) {
            public void actionPerformed(ActionEvent e) {
                changeState(launcherWindow.getSelectedExecutionListModel(), DataProcessConstants.TO_DO);
            }
        });
        popupMenu.add(new DesactivableAction("Forcer l'état à : ECHEC", true) {
            public void actionPerformed(ActionEvent e) {
                changeState(launcherWindow.getSelectedExecutionListModel(), DataProcessConstants.FAILED);
            }
        });

        if (ctxt.getUser().isInRole(dataProcessGuiPlugin.getConfiguration().getMaintenanceRoleName())) {
            popupMenu.addSeparator();
            popupMenu.add(new ShowTreatmentAction(true));
        }

        launcherWindow.getRequestTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent ev) {
                tableMousePressed(ev);
            }


            @Override
            public void mouseReleased(MouseEvent ev) {
                maybeShowPopup(ev);
            }
        });
    }


    private void maybeShowPopup(MouseEvent ev) {
        if (ev.isPopupTrigger()) {
            if (launcherWindow.isReadOnly()) {
                enableAction(false);
            }
            else {
                enableAction(true);
            }
            popupMenu.show(ev.getComponent(), ev.getX(), ev.getY());
        }
    }


    private void enableAction(boolean enable) {
        for (int i = 0; i < popupMenu.getComponentCount(); i++) {
            Component component = popupMenu.getComponent(i);
            if (component instanceof JMenuItem) {
                Action action = ((JMenuItem)component).getAction();
                if (action instanceof DesactivableAction && ((DesactivableAction)action).isDesactivable()) {
                    action.setEnabled(enable);
                }
            }
        }
    }


    private class ShowDependencyAction extends DesactivableAction {
        ShowDependencyAction(boolean desactivable) {
            super("Afficher les dépendances d'exécution", desactivable);
        }


        public void actionPerformed(ActionEvent arg0) {
            StringBuilder dependencyHtml = new StringBuilder();

            ExecutionListModel executionListModel = launcherWindow.getSelectedExecutionListModel();
            String executionListName = executionListModel.getName();
            TreatmentStepGui treatmentStepGui =
                  launcherWindow.addResultTab("Dépendances d'exécution de '" + executionListName
                                              + '\'', executionListModel);
            try {
                int repositoryId = Integer.parseInt(GuiContextUtils.getCurrentRepository(ctxt));
                ExecutionListDependency executionListPrinc =
                      DependencyClientHelper.findDependency(ctxt, repositoryId, executionListName);
                if (executionListPrinc != null) {
                    List<String> executionListPrincList = executionListPrinc.getExecutionList();
                    dependencyHtml.append("Dépendances d'exécution de <b>");
                    dependencyHtml.append(executionListName);
                    dependencyHtml.append("</b> :<UL>");
                    for (String anExecutionListPrinc : executionListPrincList) {
                        dependencyHtml.append("<LI>").append(anExecutionListPrinc).append("</LI>");
                    }
                    dependencyHtml.append("</UL>");
                    treatmentStepGui.setMode(TreatmentStepGui.HTML_MODE);
                    treatmentStepGui.getReport().setText(dependencyHtml.toString());
                }
                else {
                    treatmentStepGui.setMode(TreatmentStepGui.HTML_MODE);
                    treatmentStepGui.getReport().setText("<b>" + executionListName
                                                         + "</b> n'a pas de dépendances d'exécution.");
                }
            }
            catch (Exception ex) {
                GuiUtils.showErrorDialog(launcherWindow.getFrame(), getClass(),
                                         "Erreur interne", ex);
            }
        }
    }

    private class ShowImplicationAction extends DesactivableAction {
        ShowImplicationAction(boolean desactivable) {
            super("Afficher les listes de traitements impactées", desactivable);
        }


        public void actionPerformed(ActionEvent arg0) {
            StringBuilder dependencyHtml = new StringBuilder();
            ExecutionListModel executionListModel = launcherWindow.getSelectedExecutionListModel();
            String executionListName = executionListModel.getName();
            TreatmentStepGui treatmentStepGui =
                  launcherWindow.addResultTab("Listes de traitements impactées par '"
                                              + executionListName + '\'', executionListModel);
            try {
                int repositoryId = Integer.parseInt(GuiContextUtils.getCurrentRepository(ctxt));
                ExecutionListDependency executionListDep =
                      DependencyClientHelper.findImplication(ctxt, repositoryId, executionListName);
                if (executionListDep != null) {
                    List<String> executionListDepList = executionListDep.getExecutionList();
                    dependencyHtml.append("Listes de traitements impactées par <b>");
                    dependencyHtml.append(executionListName);
                    dependencyHtml.append("</b> :<UL>");
                    for (String anExecutionListDep : executionListDepList) {
                        dependencyHtml.append("<LI>").append(anExecutionListDep).append("</LI>");
                    }
                    dependencyHtml.append("</UL>");
                    treatmentStepGui.setMode(TreatmentStepGui.HTML_MODE);
                    treatmentStepGui.getReport().setText(dependencyHtml.toString());
                }
                else {
                    treatmentStepGui.setMode(TreatmentStepGui.HTML_MODE);
                    treatmentStepGui.getReport().setText("<b>" + executionListName
                                                         + "</b> n'impacte aucune liste de traitements.");
                }
            }
            catch (RequestException ex) {
                GuiUtils.showErrorDialog(launcherWindow.getFrame(), getClass(),
                                         "Erreur interne", ex);
            }
        }
    }

    private class ShowTreatmentAction extends DesactivableAction {
        ShowTreatmentAction(boolean desactivable) {
            super("Modifier les traitements (format XML)", desactivable);
        }


        public void actionPerformed(ActionEvent evt) {
            ExecutionListModel executionListModel = launcherWindow.getSelectedExecutionListModel();
            StringBuilder result = new StringBuilder(
                  "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n<root>");

            List<UserTreatment> sortedList = executionListModel.getSortedTreatmentList();
            String repositoryId = GuiContextUtils.getCurrentRepository(ctxt);
            for (UserTreatment usrTrt : sortedList) {
                try {
                    String treatmentModelXml = TreatmentClientHelper.getTreatmentModel(ctxt, repositoryId,
                                                                                       usrTrt.getId());
                    result.append(treatmentModelXml);
                }
                catch (RequestException ex) {
                    GuiUtils.showErrorDialog(launcherWindow.getFrame(),
                                             ManagerStateModifier.this.getClass(),
                                             "Erreur interne", ex);
                }
            }
            result.append("\n</root>");
            XmlTreatmentLogic xmlTreatmentLogic = new XmlTreatmentLogic(repositoryId, ctxt, true,
                                                                        "Modification de traitements (format XML)");
            xmlTreatmentLogic.show();
            xmlTreatmentLogic.setContent(result.toString());
            GuiUtil.centerWindow(xmlTreatmentLogic.getFrame());
            Modal.applyModality(launcherWindow.getFrame(), xmlTreatmentLogic.getFrame());
        }
    }

    private abstract static class DesactivableAction extends AbstractAction {
        private boolean desactivable;


        DesactivableAction(Object newValue, boolean desactivable) {
            this.desactivable = desactivable;
            putValue(NAME, newValue);
            putValue(SHORT_DESCRIPTION, newValue);
        }


        public boolean isDesactivable() {
            return desactivable;
        }
    }
}
