package net.codjo.dataprocess.gui.param;
import net.codjo.dataprocess.client.TreatmentClientHelper;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.ExecutionListStoreHelper;
import net.codjo.dataprocess.gui.selector.RepositoryFamilyPanel;
import net.codjo.dataprocess.gui.util.ComboUpdateEventListener;
import net.codjo.dataprocess.gui.util.GuiContextUtils;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
/**
 *
 */
public class ExecutionListPriorityWindow {
    private JButton upPriorityButton;
    private JButton downPriorityButton;
    private JList guiExecutionLists;
    private JButton quitButton;
    private JButton saveButton;
    private JPanel topPanel;
    private JPanel mainPanel;
    private MutableGuiContext ctxt;
    private JInternalFrame frame;
    private RepositoryFamilyPanel repositoryFamilyPanel;
    private boolean modified = false;
    private Map<String, ExecutionListStoreHelper> mapOflistOfExecutionList;


    public ExecutionListPriorityWindow(MutableGuiContext ctxt, final JInternalFrame frame) {
        this.ctxt = ctxt;
        this.frame = frame;
        mapOflistOfExecutionList = new HashMap<String, ExecutionListStoreHelper>();
        repositoryFamilyPanel = new RepositoryFamilyPanel(ctxt, true, false);
        topPanel.setLayout(new BorderLayout());
        topPanel.add(repositoryFamilyPanel, BorderLayout.WEST);

        repositoryFamilyPanel.load();
        try {
            repositoryFamilyPanel.setSelectedRepositoryId(ctxt, GuiContextUtils.getCurrentRepository(ctxt));
        }
        catch (Exception ex) {
            ;
        }
        repositoryFamilyPanel.addRepositoryEventListener(new MyComboBoxUpdateEventListener());
        repositoryFamilyPanel.addFamilyEventListener(new MyComboBoxUpdateEventListener());
        saveButton.setEnabled(false);

        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent event) {
                quitCommand();
            }
        });
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                quitCommand();
            }
        });
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                save();
                hasChanged(false);
            }
        });
        downPriorityButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                downPriorityButtonActionPerformed();
            }
        });
        upPriorityButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                upPriorityButtonActionPerformed();
            }
        });

        guiExecutionLists.setModel(initExecutionListListModel());
        guiExecutionLists.setCellRenderer(new TrtListCellRenderer());

        InputMap inputMap = frame.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
        frame.getActionMap().put("cancel", new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                quitCommand();
            }
        });
    }


    public JPanel getMainPanel() {
        return mainPanel;
    }


    private static String buildKey(int repository, int family) {
        return repository + ":" + family;
    }


    private static int getRepo(String key) {
        return Integer.valueOf(key.split(":")[0]);
    }


    private static int getFamily(String key) {
        return Integer.valueOf(key.split(":")[1]);
    }


    private ExecutionListStoreHelper getExecutionListHome(int repository, int family) {
        ExecutionListStoreHelper executionListStoreHelper = mapOflistOfExecutionList.get(buildKey(repository,
                                                                                                  family));
        if (executionListStoreHelper == null) {
            executionListStoreHelper = new ExecutionListStoreHelper();
            mapOflistOfExecutionList.put(buildKey(repository, family), executionListStoreHelper);
            initExecutionLists(repository, family, executionListStoreHelper);
        }
        return executionListStoreHelper;
    }


    private void quitCommand() {
        if (modified) {
            int result = JOptionPane.showInternalConfirmDialog(frame, "Sauvegarder les changements ?",
                                                               "Demande de confirmation",
                                                               JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
            if (result == JOptionPane.YES_OPTION) {
                save();
            }
        }
        frame.dispose();
    }


    private void save() {
        try {
            for (String key : mapOflistOfExecutionList.keySet()) {
                saveExecutionLists(key);
            }
        }
        catch (RequestException ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Pb de stockage de la liste", ex);
        }
    }


    private void saveExecutionLists(String key) throws RequestException {
        List<ExecutionListModel> tempList = new ArrayList<ExecutionListModel>();
        for (ExecutionListModel executionListModel :
              getExecutionListHome(getRepo(key), getFamily(key)).getRepository()) {
            tempList.add(executionListModel);
        }
        TreatmentClientHelper.saveExecutionListModel(ctxt, getRepo(key), tempList, getFamily(key));
    }


    private void upPriorityButtonActionPerformed() {
        modifyPriority(true);
    }


    private void downPriorityButtonActionPerformed() {
        modifyPriority(false);
    }


    private void modifyPriority(boolean isUpPriority) {
        Object[] srcValues = guiExecutionLists.getSelectedValues();
        if (srcValues.length == 0) {
            return;
        }

        ExecutionListModel selectedMinValue = (ExecutionListModel)srcValues[0];
        ExecutionListModel selectedMaxValue = (ExecutionListModel)srcValues[srcValues.length - 1];

        if (isUpPriority) {
            if (selectedMinValue.getPriority() <= 1) {
                return;
            }
            getExecutionListModel(selectedMinValue.getPriority() - 1)
                  .setPriority(selectedMaxValue.getPriority());
            upOrDown(srcValues, -1);
        }
        else {
            if (selectedMaxValue.getPriority() >= getMaxExecutionListPriority()) {
                return;
            }
            getExecutionListModel(selectedMaxValue.getPriority() + 1)
                  .setPriority(selectedMinValue.getPriority());
            upOrDown(srcValues, 1);
        }

        guiExecutionLists.setModel(initExecutionListListModel());
        guiExecutionLists.setSelectedIndices(getIndices(srcValues));
        hasChanged(true);
    }


    private int getMaxExecutionListPriority() {
        int max = 0;
        for (ExecutionListModel executionListModel :
              getExecutionListHome(getRepositoryId(), getFamilyId()).getRepository()) {
            if (executionListModel.getPriority() > max) {
                max = executionListModel.getPriority();
            }
        }
        return max;
    }


    private int getRepositoryId() {
        return repositoryFamilyPanel.getSelectedRepositoryId();
    }


    private int getFamilyId() {
        return repositoryFamilyPanel.getSelectedFamilyId();
    }


    private static int[] getIndices(Object[] srcValues) {
        int[] indices = new int[srcValues.length];
        int ii = 0;
        for (Object ob : srcValues) {
            ExecutionListModel executionListModel = (ExecutionListModel)ob;
            indices[ii++] = executionListModel.getPriority() - 1;
        }
        return indices;
    }


    private ExecutionListModel getExecutionListModel(int priority) {
        for (ExecutionListModel executionListModel : getExecutionListHome(getRepositoryId(), getFamilyId())
              .getRepository()) {
            if (executionListModel.getPriority() == priority) {
                return executionListModel;
            }
        }
        return null;
    }


    private static void upOrDown(Object[] srcValues, int direction) {
        for (Object obj : srcValues) {
            ExecutionListModel executionListModel = (ExecutionListModel)obj;
            if (executionListModel.getPriority() + direction > 0) {
                executionListModel.setPriority(executionListModel.getPriority() + direction);
            }
        }
    }


    private void hasChanged(boolean bb) {
        this.modified = bb;
        saveButton.setEnabled(bb);
    }


    private DefaultListModel initExecutionListListModel() {
        List<ExecutionListModel> rep
              = getExecutionListHome(getRepositoryId(), getFamilyId()).getRepository();
        ExecutionListModel[] execLists = rep.toArray(new ExecutionListModel[rep.size()]);
        Arrays.sort(execLists, ExecutionListModel.getPriorityComparator());

        DefaultListModel execListListModel = new DefaultListModel();
        int priority = 1;
        for (ExecutionListModel execList : execLists) {
            execList.setPriority(priority);
            execListListModel.addElement(execList);
            priority++;
        }
        return execListListModel;
    }


    private void initExecutionLists(int repositoryIdSelected,
                                    int familyIdSelected,
                                    ExecutionListStoreHelper executionListStoreHelper) {
        try {
            List<ExecutionListModel> list = TreatmentClientHelper.getExecutionListModel(ctxt,
                                                                                        repositoryIdSelected,
                                                                                        familyIdSelected);
            if (list != null) {
                for (ExecutionListModel trtModel : list) {
                    try {
                        executionListStoreHelper.addExecutionList(trtModel);
                    }
                    catch (TreatmentException ex) {
                        GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
                    }
                }
            }
        }
        catch (Exception ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
            frame.setVisible(false);
            frame.dispose();
        }
    }


    private static class TrtListCellRenderer extends JLabel implements ListCellRenderer {
        private DefaultListCellRenderer renderer = new DefaultListCellRenderer();


        private TrtListCellRenderer() {
        }


        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            return renderer.getListCellRendererComponent(list, getRendererValue(value),
                                                         index, isSelected, cellHasFocus);
        }


        private static Object getRendererValue(Object value) {
            ExecutionListModel trtEx = (ExecutionListModel)value;
            return trtEx.getName();
        }
    }

    private class MyComboBoxUpdateEventListener implements ComboUpdateEventListener {
        public void executeUpdate() {
            guiExecutionLists.setModel(initExecutionListListModel());
        }
    }
}
