package net.codjo.dataprocess.gui.dependency;
import net.codjo.dataprocess.client.ExecutionListDependency;
import net.codjo.dataprocess.client.TreatmentClientHelper;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.gui.selector.RepositoryFamilyPanel;
import net.codjo.dataprocess.gui.util.ComboUpdateEventListener;
import net.codjo.dataprocess.gui.util.GuiContextUtils;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import static net.codjo.dataprocess.client.DependencyClientHelper.deleteDependency;
import static net.codjo.dataprocess.client.DependencyClientHelper.findDependency;
import static net.codjo.dataprocess.client.DependencyClientHelper.insertDependency;
/**
 *
 */
public class DependencyWindow {
    private JPanel topPanel;
    private JButton closeButton;
    private JList guiExecutionLists;
    private JList guiExecutionListsDep;
    private JButton removeDep;
    private JButton addDep;
    private JPanel mainPanel;
    private RepositoryFamilyPanel repositoryFamilyPanel;
    private JComboBox executionListComboBox;
    private JSplitPane mainSplitPane;
    private List<ExecutionListModel> executionLists = null;
    private MutableGuiContext ctxt;
    private JInternalFrame frame;


    public DependencyWindow(MutableGuiContext ctxt, JInternalFrame frame) {
        this.ctxt = ctxt;
        this.frame = frame;
        initGui();
        initExecutionLists();
        updateGuiExecutionListsDep();
    }


    private void initGui() {
        repositoryFamilyPanel = new RepositoryFamilyPanel(ctxt, true, false);
        repositoryFamilyPanel.load();
        try {
            repositoryFamilyPanel.setSelectedRepositoryId(ctxt, GuiContextUtils.getCurrentRepository(ctxt));
        }
        catch (Exception ex) {
            ;
        }
        MyComboUpdateEventListener comboUpdateEventListener = new MyComboUpdateEventListener();
        repositoryFamilyPanel.addRepositoryEventListener(comboUpdateEventListener);
        repositoryFamilyPanel.addFamilyEventListener(comboUpdateEventListener);

        executionListComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                initExecutionLists();
                updateGuiExecutionListsDep();
                mainSplitPane.resetToPreferredSizes();
            }
        });

        removeDep.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                removeDependencies();
            }
        });
        addDep.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                newDependencies();
            }
        });

        topPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        topPanel.add(repositoryFamilyPanel);

        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                cancelButtonActionPerformed();
            }
        });

        InputMap inputMap = frame.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
        frame.getActionMap().put("cancel", new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                cancelButtonActionPerformed();
            }
        });

        executionListComboBox.setRenderer(new ExecutionListComboCellRenderer());
        guiExecutionLists.setCellRenderer(new TrtListCellRenderer());
        guiExecutionListsDep.setCellRenderer(new TrtListCellRenderer());
        executionListComboBox.setModel(getExecListComboBoxModel());
    }


    private int getSelectedRepositoryId() {
        return repositoryFamilyPanel.getSelectedRepositoryId();
    }


    private void removeDependencies() {
        if (guiExecutionListsDep.getSelectedIndex() >= 0) {
            Object[] executionListSelected = guiExecutionListsDep.getSelectedValues();
            ExecutionListModel trtDep = getSelectedComboBoxExecList();

            for (Object anExecutionListSelected : executionListSelected) {
                ExecutionListModel trtPrinc = (ExecutionListModel)anExecutionListSelected;
                try {
                    deleteDependency(ctxt, getSelectedRepositoryId(), trtPrinc.getName(), trtDep.getName());
                    updateGuiExecutionLists();
                    updateGuiExecutionListsDep();
                }
                catch (RequestException ex) {
                    GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
                }
            }
        }
    }


    private ExecutionListModel getSelectedComboBoxExecList() {
        return (ExecutionListModel)executionListComboBox.getSelectedItem();
    }


    private void newDependencies() {
        if (guiExecutionLists.getSelectedIndex() >= 0) {
            Object[] executionListSelected = guiExecutionLists.getSelectedValues();
            ExecutionListModel trtDep = getSelectedComboBoxExecList();

            for (Object anExecutionListSelected : executionListSelected) {
                ExecutionListModel trtPrinc = (ExecutionListModel)anExecutionListSelected;
                try {
                    insertDependency(ctxt, getSelectedRepositoryId(), trtPrinc.getName(), trtDep.getName());
                    ExecutionListDependency dep = findDependency(ctxt, getSelectedRepositoryId(),
                                                                 trtDep.getName());

                    if (dep.isCycle()) {
                        String depStr = "";
                        for (String executionList : dep.getExecutionList()) {
                            depStr = new StringBuilder().append(depStr).append(executionList).append('\n')
                                  .toString();
                        }
                        depStr = depStr.substring(0, depStr.length() - 2);

                        int result =
                              JOptionPane.showInternalConfirmDialog(ctxt.getDesktopPane(),
                                                                    "Des cycles ou des dépendances indirectes déjà existantes pourraient être créées.\n"
                                                                    + "Dépendances de '" + trtDep.getName()
                                                                    + "' après cet ajout : " + depStr + "\n\n"
                                                                    + "Voulez vous ajouter '"
                                                                    + trtPrinc.getName() + "' ?",
                                                                    "Confirmation d'ajout de dépendances",
                                                                    JOptionPane.YES_NO_OPTION);

                        if (result == JOptionPane.NO_OPTION) {
                            deleteDependency(ctxt, getSelectedRepositoryId(), trtPrinc.getName(),
                                             trtDep.getName());
                        }
                    }
                }
                catch (RequestException ex) {
                    GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
                }
            }
            updateGuiExecutionLists();
            updateGuiExecutionListsDep();
        }
    }


    private DefaultComboBoxModel getExecListComboBoxModel() {
        int repositoryIdSelected = getSelectedRepositoryId();
        int familyIdSelected = repositoryFamilyPanel.getSelectedFamilyId();
        List<ExecutionListModel> list = null;
        DefaultComboBoxModel comboModel = new DefaultComboBoxModel();

        try {
            list = TreatmentClientHelper.getExecutionListModel(ctxt, repositoryIdSelected, familyIdSelected);
        }
        catch (Exception ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
            frame.dispose();
        }
        if (list != null) {
            ExecutionListModel[] executionListModelArray = list.toArray(new ExecutionListModel[list.size()]);
            Arrays.sort(executionListModelArray, ExecutionListModel.getPriorityComparator());
            list = Arrays.asList(executionListModelArray);

            for (ExecutionListModel executionListModel : list) {
                comboModel.addElement(executionListModel);
            }
        }
        return comboModel;
    }


    private void cancelButtonActionPerformed() {
        frame.dispose();
    }


    private void initExecutionLists() {
        try {
            executionLists = TreatmentClientHelper.getExecutionListModel(ctxt,
                                                                         repositoryFamilyPanel.getSelectedRepositoryId(),
                                                                         repositoryFamilyPanel.getSelectedFamilyId());

            ExecutionListModel[] executionListsArray = executionLists
                  .toArray(new ExecutionListModel[executionLists.size()]);
            Arrays.sort(executionListsArray, ExecutionListModel.getPriorityComparator());
            executionLists = Arrays.asList(executionListsArray);
        }
        catch (Exception ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
        }

        updateGuiExecutionLists();
    }


    private void updateGuiExecutionLists() {
        DefaultListModel listModel = new DefaultListModel();
        ExecutionListModel selectedComboBoxTrt = getSelectedComboBoxExecList();

        if (selectedComboBoxTrt != null) {
            for (ExecutionListModel executionListModel : executionLists) {
                if (executionListModel.getId() != selectedComboBoxTrt.getId()) {
                    listModel.addElement(executionListModel);
                }
            }
            guiExecutionLists.setModel(listModel);
            guiExecutionLists.setSelectionModel(new DefaultListSelectionModel());
            guiExecutionLists.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }
        else {
            if (guiExecutionLists.getModel() != null) {
                if (guiExecutionLists.getModel().getSize() != 0) {
                    DefaultListModel trtListModelTemp = (DefaultListModel)guiExecutionLists.getModel();
                    trtListModelTemp.removeAllElements();
                }
            }
        }
    }


    private void updateGuiExecutionListsDep() {
        try {
            DefaultListModel guiExecutionListsDepModel = new DefaultListModel();
            guiExecutionListsDep.setModel(guiExecutionListsDepModel);
            guiExecutionListsDep.setSelectionModel(new DefaultListSelectionModel());
            guiExecutionListsDep.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

            if (!executionLists.isEmpty()) {
                DefaultListModel guiExecutionListsModel = (DefaultListModel)guiExecutionLists.getModel();
                ExecutionListModel trtComboBoxSelected = getSelectedComboBoxExecList();
                if (trtComboBoxSelected != null) {
                    ExecutionListDependency executionListComboBoxSelected =
                          findDependency(ctxt, getSelectedRepositoryId(), trtComboBoxSelected.getName());
                    if (executionListComboBoxSelected != null) {
                        List<String> executionListPrinc = executionListComboBoxSelected.getExecutionList();
                        for (String id : executionListPrinc) {
                            for (int i = 0; i < guiExecutionListsModel.size(); i++) {
                                ExecutionListModel usrTrt = (ExecutionListModel)guiExecutionListsModel
                                      .elementAt(i);
                                if (usrTrt.getName().equals(id)) {
                                    guiExecutionListsDepModel.addElement(usrTrt);
                                    guiExecutionListsModel.removeElement(usrTrt);
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (RequestException ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
        }
    }


    public JPanel getMainPanel() {
        return mainPanel;
    }


    private static class ExecutionListComboCellRenderer extends JLabel implements ListCellRenderer {
        private DefaultListCellRenderer renderer = new DefaultListCellRenderer();


        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            return renderer.getListCellRendererComponent(list, getRendererValue(value), index, isSelected,
                                                         cellHasFocus);
        }


        private static Object getRendererValue(Object value) {
            if (value != null) {
                ExecutionListModel trt = (ExecutionListModel)value;
                return trt.getPriority() + " - " + trt.getName();
            }
            return "";
        }
    }

    private static class TrtListCellRenderer extends JLabel implements ListCellRenderer {
        private DefaultListCellRenderer renderer = new DefaultListCellRenderer();


        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            return renderer.getListCellRendererComponent(list, getRendererValue(value), index, isSelected,
                                                         cellHasFocus);
        }


        private static Object getRendererValue(Object value) {
            ExecutionListModel trt = (ExecutionListModel)value;
            return trt.getPriority() + " - " + trt.getName();
        }
    }

    private class MyComboUpdateEventListener implements ComboUpdateEventListener {
        public void executeUpdate() {
            executionListComboBox.setModel(getExecListComboBoxModel());
            initExecutionLists();
            updateGuiExecutionListsDep();
            mainSplitPane.resetToPreferredSizes();
        }
    }
}
