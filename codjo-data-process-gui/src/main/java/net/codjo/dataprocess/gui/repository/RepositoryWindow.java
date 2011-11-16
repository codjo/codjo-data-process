package net.codjo.dataprocess.gui.repository;
import net.codjo.dataprocess.client.RepositoryClientHelper;
import net.codjo.dataprocess.client.TreatmentClientHelper;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.codec.TreatmentModelCodec;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.exception.RepositoryException;
import net.codjo.dataprocess.common.model.ArgList;
import net.codjo.dataprocess.common.model.ArgModel;
import net.codjo.dataprocess.common.model.ResultTable;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.gui.plugin.DataProcessGuiPlugin;
import net.codjo.dataprocess.gui.selector.RepositoryComboBox;
import net.codjo.dataprocess.gui.util.DocumentListenerAdapter;
import net.codjo.dataprocess.gui.util.GuiContextUtils;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.dataprocess.gui.util.TreatmentModelGuiAdapter;
import net.codjo.gui.toolkit.util.GuiUtil;
import net.codjo.gui.toolkit.util.Modal;
import net.codjo.gui.toolkit.waiting.WaitingPanel;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.LocalGuiContext;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import static net.codjo.dataprocess.common.DataProcessConstants.Command.IS_EXIST;
import static net.codjo.dataprocess.common.DataProcessConstants.Command.UPDATE;
/**
 *
 */
public class RepositoryWindow {
    private JPanel mainPanel;
    private JList guiTreatmentList;
    private JButton saveAllButton;
    private JButton quitButton;
    private RepositoryComboBox repositoryComboBox;
    private JButton newRepositoryButton;
    private JButton deleteRepositoryButton;
    private JButton copyRepositoryButton;
    private JButton renameRepositoryButton;
    private JButton deleteTreatmentButton;
    private JTextArea treatmentModelXmlArea;
    private JTextField resultTableTextField;
    private JTextArea targetTextArea;
    private JTextField nameTextField;
    private JTextField titleTextField;
    private JComboBox typeComboBox;
    private JCheckBox returnResultCheckbox;
    private JTextArea commentTextArea;
    private JButton newTreatmentButton;
    private ArgumentsTable argumentTable;
    private ArgumentsTable argumentTableRo;
    private JButton deleteArgumentButton;
    private JButton addArgumentButton;
    private JTextArea targetTextAreaRo;
    private JButton submitXmlContentButton;
    private JToggleButton alphaSortButton;
    private JTextField filterTextField;
    private JButton executeButton;
    private JTextField selectAllHandlerField;
    private JButton exportRepositoryButton;
    private JButton newTreatmentsXmlButton;
    private JLabel resultTableLabel;
    private JLabel selectAllHandlerLabel;
    private MutableGuiContext ctxt;
    private JInternalFrame frame;
    private JPopupMenu popupCopyTreatment = new JPopupMenu();
    private boolean ongoingChange;
    private boolean treatmentHasChanged;
    private WaitingPanel waitingPanel;
    private Map<String, Map<String, TreatmentModel>> trtModelLocalSave
          = new HashMap<String, Map<String, TreatmentModel>>();
    private DataProcessContext dataProcessContextExec;
    private TreatmentModelGuiActionUtils treatmentModelGuiActionUtils;
    private RepositoryGuiActionUtils repositoryGuiActionUtils;
    private TrtModelXmlAreaDocumentListener trtModelXmlAreaDocumentListener;


    public RepositoryWindow(MutableGuiContext ctxt,
                            DataProcessGuiPlugin dataProcessGuiPlugin,
                            final JInternalFrame frame) {
        this.ctxt = ctxt;
        this.frame = frame;

        trtModelXmlAreaDocumentListener = new TrtModelXmlAreaDocumentListener(submitXmlContentButton);
        treatmentModelGuiActionUtils = new TreatmentModelGuiActionUtils(this, ctxt, frame);
        repositoryGuiActionUtils = new RepositoryGuiActionUtils(this, dataProcessGuiPlugin, ctxt, frame);
        try {
            dataProcessContextExec = dataProcessGuiPlugin.getConfiguration().getDataProcessContext().clone();
        }
        catch (CloneNotSupportedException ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
        }

        initGui();
    }


    JList getGuiTreatmentList() {
        return guiTreatmentList;
    }


    JPanel getMainPanel() {
        return mainPanel;
    }


    public String getCurrentRepository() {
        return (String)repositoryComboBox.getSelectedItem();
    }


    public boolean isThereSomethingToSave() {
        return !trtModelLocalSave.isEmpty();
    }


    private void addTreatmentModelToSave(String repositoryId, TreatmentModel treatmentModel) {
        if (trtModelLocalSave.containsKey(repositoryId)) {
            Map<String, TreatmentModel> map = trtModelLocalSave.get(repositoryId);
            map.put(treatmentModel.getId(), treatmentModel);
        }
        else {
            Map<String, TreatmentModel> map = new HashMap<String, TreatmentModel>();
            map.put(treatmentModel.getId(), treatmentModel);
            trtModelLocalSave.put(repositoryId, map);
        }
    }


    private TreatmentModel getTreatmentModelToSave(String repositoryId, String treatmentId) {
        if (trtModelLocalSave.get(repositoryId) != null) {
            return trtModelLocalSave.get(repositoryId).get(treatmentId);
        }
        else {
            return null;
        }
    }


    private void removeTreatmentModelFromSave(String repositoryId, TreatmentModel treatmentModel) {
        if (trtModelLocalSave.containsKey(repositoryId)) {
            Map<String, TreatmentModel> map = trtModelLocalSave.get(repositoryId);
            map.remove(treatmentModel.getId());

            if (map.isEmpty()) {
                trtModelLocalSave.remove(repositoryId);
            }
        }
    }


    void actionSaveAll() {
        String selectedTreatmentId = (String)guiTreatmentList.getSelectedValue();
        Runnable runnable = new SaveAllRunnable(selectedTreatmentId);
        waitingPanel.exec(runnable);
    }


    private void actionSave(final TreatmentModel treatmentModel) {
        final String treatmentIdSelected = (String)guiTreatmentList.getSelectedValue();
        Runnable runnable = new Runnable() {
            public void run() {
                waitingPanel.setText("Sauvegarde en cours...");
                try {
                    TreatmentClientHelper.manageTreatmentModel(ctxt, UPDATE, getCurrentRepository(),
                                                               TreatmentModelCodec.encode(treatmentModel));
                    removeTreatmentModelFromSave(getCurrentRepository(), treatmentModel);
                    if (Log.isInfoEnabled()) {
                        Log.info(getClass(), String.format("Sauvegarde du traitement %s (repositoryId = %s)",
                                                           treatmentModel.getId(), getCurrentRepository()));
                    }

                    if (trtModelLocalSave.isEmpty()) {
                        saveAllButton.setEnabled(false);
                    }
                    initTreatmentList();
                    RepositoryClientHelper.reinitializeRepositoryCache(ctxt);
                    if (treatmentIdSelected != null) {
                        guiTreatmentList.setSelectedValue(treatmentIdSelected, true);
                    }
                }
                catch (Exception ex) {
                    GuiUtils.showErrorDialog(frame, getClass(), "Erreur de sauvegarde des données : ", ex);
                }
            }
        };
        waitingPanel.exec(runnable);
    }


    private void treatmentChangedAction() {
        if (!ongoingChange) {
            treatmentHasChanged = true;
            saveAllButton.setEnabled(true);
        }
    }


    private void initTypeComboBoxModel() {
        DefaultComboBoxModel model = (DefaultComboBoxModel)typeComboBox.getModel();
        model.addElement(TreatmentModelGuiAdapter.JAVA_CODE);
        model.addElement(TreatmentModelGuiAdapter.SQL);
        model.addElement(TreatmentModelGuiAdapter.STORED_PROCEDURE);
        model.addElement(TreatmentModelGuiAdapter.BEAN_SHELL);
    }


    private void createUIComponents() {
        argumentTable = new ArgumentsTable(0, 0);
        argumentTableRo = new ArgumentsTable(0, 0);
        repositoryComboBox = new RepositoryComboBox(new LocalGuiContext(ctxt));
        repositoryComboBox.loadData();
        try {
            repositoryComboBox.setSelectedItem(GuiContextUtils.getCurrentRepository(ctxt));
        }
        catch (Exception ex) {
            ;
        }
    }


    private void initGui() {
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        waitingPanel = new WaitingPanel();
        frame.setGlassPane(waitingPanel);
        initTypeComboBoxModel();
        createPopupOnGuiTreatmentList();
        try {
            initTreatmentList();
        }
        catch (RequestException ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
        }
        guiTreatmentList.setCellRenderer(new TreatmentListRenderer());
        enableDetail(false);
        clearDetail();
        createListeners();

        InputMap inputMap = mainPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
        mainPanel.getActionMap().put("cancel", new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                quitCommand();
            }
        });
    }


    private void createListeners() {
        nameTextField.getDocument().addDocumentListener(new TreatmentChangedDocumentListener());
        titleTextField.getDocument().addDocumentListener(new TreatmentChangedDocumentListener());
        resultTableTextField.getDocument().addDocumentListener(new TreatmentChangedDocumentListener());
        selectAllHandlerField.getDocument().addDocumentListener(new TreatmentChangedDocumentListener());
        commentTextArea.getDocument().addDocumentListener(new TreatmentChangedDocumentListener());
        targetTextArea.getDocument().addDocumentListener(new TreatmentChangedDocumentListener());
        returnResultCheckbox.addActionListener(new TreatmentChangedActionListener());
        typeComboBox.addActionListener(new TreatmentChangedActionListener());
        treatmentModelXmlArea.getDocument().removeDocumentListener(trtModelXmlAreaDocumentListener);
        treatmentModelXmlArea.getDocument().addDocumentListener(trtModelXmlAreaDocumentListener);
        filterTextField.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            protected void actionPerformed(DocumentEvent evt) {
                filterChanged();
            }
        });
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent event) {
                quitCommand();
            }
        });
        repositoryComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (repositoryComboBox.isLoading()) {
                        return;
                    }
                    repositoryComboBoxActionPerformed();
                }
                catch (RequestException ex) {
                    GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
                }
            }
        });
        repositoryComboBox.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                saveCurrentTreatmentModel();
            }
        });
        guiTreatmentList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                if (!evt.getValueIsAdjusting()) {
                    actionGuiTreatmentListSelectionned();
                }
            }
        });
        guiTreatmentList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                if (SwingUtilities.isRightMouseButton(event)) {
                    guiTreatmentList.setSelectedIndex(
                          guiTreatmentList.locationToIndex(new Point(event.getPoint())));
                }
                actionGuiTreatmentListSelectionned();
            }
        });
        submitXmlContentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    actionSubmitXmlContent();
                }
                catch (Exception ex) {
                    GuiUtils.showErrorDialog(frame, getClass(),
                                             "Une erreur est survenue lors de la validation du XML : ", ex);
                }
            }
        });
        typeComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                actionTypeChange();
            }
        });
        newRepositoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                saveCurrentTreatmentModel();
                repositoryGuiActionUtils.actionNewRepository();
            }
        });
        copyRepositoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (repositoryComboBox.getItemCount() != 0) {
                    saveCurrentTreatmentModel();
                    repositoryGuiActionUtils.actionCopyRepository(getCurrentRepository());
                }
            }
        });
        renameRepositoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (repositoryComboBox.getItemCount() != 0) {
                    saveCurrentTreatmentModel();
                    repositoryGuiActionUtils.actionRenameRepository(getCurrentRepository());
                }
            }
        });
        exportRepositoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (repositoryComboBox.getItemCount() != 0) {
                    try {
                        exportRepository();
                    }
                    catch (Exception ex) {
                        GuiUtils.showErrorDialog(frame,
                                                 getClass(),
                                                 "Erreur de sauvegarde des données du repository",
                                                 ex);
                    }
                }
            }
        });
        newTreatmentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                saveCurrentTreatmentModel();
                treatmentModelGuiActionUtils.actionNewTreatment(getCurrentRepository());
            }
        });
        newTreatmentsXmlButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                saveCurrentTreatmentModel();
                XmlTreatmentLogic xmlTreatmentLogic = new XmlTreatmentLogic(getCurrentRepository(),
                                                                            ctxt,
                                                                            false,
                                                                            "Ajout/Modification de traitements (format XML)");
                xmlTreatmentLogic.show();
                GuiUtil.centerWindow(xmlTreatmentLogic.getFrame());
                Modal.applyModality(frame, xmlTreatmentLogic.getFrame());
                try {
                    updateTreatmentList();
                }
                catch (RequestException ex) {
                    GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
                }
            }
        });
        deleteTreatmentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (getGuiTreatmentList().getSelectedIndex() != -1) {
                    treatmentModelGuiActionUtils.actionDeleteTreatment(getCurrentRepository());
                }
            }
        });
        deleteRepositoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (repositoryComboBox.getItemCount() > 0) {
                    saveCurrentTreatmentModel();
                    repositoryGuiActionUtils.actionDeleteRepository(getCurrentRepository());
                    try {
                        updateTreatmentList();
                    }
                    catch (RequestException ex) {
                        GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
                    }
                }
            }
        });
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                quitCommand();
            }
        });
        addArgumentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actionAddArgument();
            }
        });
        deleteArgumentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actionDeleteArgument();
            }
        });
        alphaSortButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                filterChanged();
            }
        });
        saveAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actionSaveAll();
            }
        });
        executeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (getGuiTreatmentList().getSelectedIndex() != -1) {
                    actionExecuteTreatment();
                }
            }
        });

        guiTreatmentList.addMouseListener(new MyMouseAdapter());
    }


    private void actionExecuteTreatment() {
        try {
            saveCurrentTreatmentModel();
            TreatmentModel treatmentModel = getCurrentTreatmentModel();
            if (saveAllButton.isEnabled()) {
                actionSave(treatmentModel);
            }
            ArgumentDialog argumentDialog = new ArgumentDialog(ctxt.getMainFrame(), dataProcessContextExec,
                                                               treatmentModel);
            GuiUtil.centerWindow(argumentDialog);
            argumentDialog.setVisible(true);
            if (argumentDialog.isCanceled()) {
                return;
            }
            TreatmentClientHelper.proceedTreatment(ctxt, Integer.parseInt(getCurrentRepository()),
                                                   treatmentModel.getId(), dataProcessContextExec,
                                                   argumentDialog.getTimeout());
        }
        catch (Exception ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Exécution du traitement en erreur", ex);
        }
    }


    private void actionSubmitXmlContent() {
        TreatmentModel treatmentModel = TreatmentModelCodec.decode(treatmentModelXmlArea.getText());
        if (!treatmentModel.getId().equals(nameTextField.getText())) {
            JOptionPane.showInternalMessageDialog(frame,
                                                  "L'identifiant (id) du traitement a été modifié.\n"
                                                  + "Il ne doit pas être modifié.",
                                                  "Validation impossible", JOptionPane.ERROR_MESSAGE);
            return;
        }
        treatmentModelToGui(treatmentModel);
        treatmentChangedAction();
        submitXmlContentButton.setEnabled(false);
    }


    private void actionTypeChange() {
        initArgumentTableModel();
    }


    private void actionDeleteArgument() {
        argumentTable.removeCurrentRow();
    }


    private void actionAddArgument() {
        argumentTable.addRow();
    }


    private void treatmentModelToGui(TreatmentModel treatmentModel) {
        ongoingChange = true;
        nameTextField.setText(treatmentModel.getId());
        typeComboBox.setSelectedItem(new TreatmentModelGuiAdapter(treatmentModel).getType());
        returnResultCheckbox.setSelected(treatmentModel.isReturnResult());
        titleTextField.setText(treatmentModel.getTitle());
        commentTextArea.setText(treatmentModel.getComment());
        commentTextArea.setCaretPosition(0);
        if (treatmentModel.getResultTable() != null) {
            resultTableTextField.setText(treatmentModel.getResultTable().getTable());
            selectAllHandlerField.setText(treatmentModel.getResultTable().getSelectAllHandler());
        }
        setTreatmentModelXmlAreaContent(TreatmentModelCodec.encode(treatmentModel));
        targetTextArea.setText(treatmentModel.getTarget());
        targetTextArea.setCaretPosition(0);
        targetTextAreaRo.setText(treatmentModel.getTarget());
        targetTextAreaRo.setCaretPosition(0);
        initArgumentTableModel();
        updateArgumentTable(treatmentModel);
        submitXmlContentButton.setEnabled(false);
        ongoingChange = false;
    }


    private TreatmentModel guiToTreatmentModel() {
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setId(nameTextField.getText());
        treatmentModel.setTarget(targetTextArea.getText());
        treatmentModel.setTitle(titleTextField.getText());
        treatmentModel.setResultTable(
              new ResultTable(resultTableTextField.getText(), selectAllHandlerField.getText()));
        new TreatmentModelGuiAdapter(treatmentModel).setType((String)typeComboBox.getSelectedItem(),
                                                             returnResultCheckbox.isSelected());
        treatmentModel.setComment(commentTextArea.getText());

        try {
            ArgList argList = argumentTable.getArglist();
            treatmentModel.setArguments(argList);
        }
        catch (Exception ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Erreur de saisie des arguments", ex);
        }
        return treatmentModel;
    }


    private void initArgumentTableModel() {
        String type = (String)typeComboBox.getSelectedItem();
        if (type.equals(TreatmentModelGuiAdapter.JAVA_CODE)
            || type.equals(TreatmentModelGuiAdapter.BEAN_SHELL)) {
            argumentTable.initTableModel(ArgumentsTable.JAVA_TYPE);
            argumentTableRo.initTableModel(ArgumentsTable.JAVA_TYPE);
        }
        else {
            argumentTable.initTableModel(ArgumentsTable.SQL_TYPE);
            argumentTable.initSqlFormatStuff();
            argumentTableRo.initTableModel(ArgumentsTable.SQL_TYPE);
            argumentTableRo.initSqlFormatStuff();
        }

        argumentTable.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent evt) {
                treatmentChangedAction();
            }
        });
    }


    private void updateArgumentTable(TreatmentModel treatmentModel) {
        int rowNumber = treatmentModel.getArguments().getArgs().size();

        DefaultTableModel argumentTableModel = (DefaultTableModel)argumentTable.getModel();
        DefaultTableModel argumentTableRoModel = (DefaultTableModel)argumentTableRo.getModel();
        argumentTable.removeAllRows();
        argumentTableRo.removeAllRows();

        if (rowNumber != 0) {
            List<ArgModel> arguments = treatmentModel.getArguments().getArgs();
            for (ArgModel argument : arguments) {
                Object[] objectsTable;
                if (argumentTable.isSqlType()) {
                    objectsTable =
                          new Object[]{
                                argument.getName(), argument.getValue(),
                                Integer.toString(argument.getPosition()),
                                argument.getType()
                          };
                }
                else {
                    objectsTable = new Object[]{argument.getName(), argument.getValue()};
                }
                argumentTableModel.addRow(objectsTable);
                argumentTableRoModel.addRow(objectsTable);
            }
        }
        argumentTable.setModel(argumentTableModel);
        argumentTableRo.setModel(argumentTableRoModel);
    }


    private void repositoryComboBoxActionPerformed() throws RequestException {
        saveCurrentTreatmentModel();
        initTreatmentList();
        clearDetail();
        enableDetail(false);
    }


    void updateTreatmentList() throws RequestException {
        initTreatmentList();
        clearDetail();
    }


    private void enableDetail(boolean enable) {
        typeComboBox.setEnabled(enable);
        returnResultCheckbox.setEnabled(enable);
        resultTableTextField.setEditable(enable);
        resultTableTextField.setEnabled(enable);
        selectAllHandlerField.setEditable(enable);
        selectAllHandlerField.setEnabled(enable);
        titleTextField.setEditable(enable);
        titleTextField.setEnabled(enable);
        targetTextArea.setEditable(enable);
        targetTextArea.setEnabled(enable);
        commentTextArea.setEditable(enable);
        commentTextArea.setEnabled(enable);
        addArgumentButton.setEnabled(enable);
        deleteArgumentButton.setEnabled(enable);
        treatmentModelXmlArea.setEditable(enable);
    }


    private void clearDetail() {
        ongoingChange = true;
        enableDetail(false);
        nameTextField.setText("");
        typeComboBox.setSelectedItem(TreatmentModelGuiAdapter.SQL);
        returnResultCheckbox.setSelected(false);
        titleTextField.setText("");
        resultTableTextField.setText("");
        selectAllHandlerField.setText("");
        targetTextArea.setText("");
        targetTextAreaRo.setText("");
        setTreatmentModelXmlAreaContent("");
        commentTextArea.setText("");
        argumentTable.initTableModel(ArgumentsTable.SQL_TYPE);
        argumentTable.initSqlFormatStuff();
        argumentTableRo.initTableModel(ArgumentsTable.SQL_TYPE);
        argumentTableRo.initSqlFormatStuff();
        ongoingChange = false;
    }


    private void setTreatmentModelXmlAreaContent(String content) {
        treatmentModelXmlArea.getDocument().removeDocumentListener(trtModelXmlAreaDocumentListener);
        treatmentModelXmlArea.setText(content);
        treatmentModelXmlArea.setCaretPosition(0);
        treatmentModelXmlArea.getDocument().addDocumentListener(trtModelXmlAreaDocumentListener);
    }


    private static class GuiTreatmentListModel extends DefaultListModel {
        private List<String> treatmentIdList;
        private List<String> userTreatmentIdList;


        private GuiTreatmentListModel(MutableGuiContext ctxt, String repositoryId) throws RequestException {
            treatmentIdList = TreatmentClientHelper.getAllTreatmentModelId(ctxt, repositoryId);
            userTreatmentIdList = TreatmentClientHelper.getAllUserTreatmentId(ctxt, repositoryId);
        }


        public List<String> getTreatmentIdList() {
            return treatmentIdList;
        }


        public List<String> getUserTreatmentIdList() {
            return userTreatmentIdList;
        }
    }


    private void filterChanged() {
        String repositoryId = getCurrentRepository();

        if (repositoryId != null) {
            GuiTreatmentListModel guiTreatmentListModel = (GuiTreatmentListModel)guiTreatmentList.getModel();
            guiTreatmentListModel.clear();

            List<String> treatmentIdList = guiTreatmentListModel.getTreatmentIdList();
            if (alphaSortButton.isSelected()) {
                String[] treatmentIdListAsArray = treatmentIdList.toArray(
                      new String[treatmentIdList.size()]);
                Arrays.sort(treatmentIdListAsArray,
                            new Comparator<String>() {
                                public int compare(String s1, String s2) {
                                    return s1.compareToIgnoreCase(s2);
                                }
                            });
                treatmentIdList = Arrays.asList(treatmentIdListAsArray);
            }
            String filter = filterTextField.getText().trim();
            for (String treatmentId : treatmentIdList) {
                if (filter.length() != 0) {
                    if (filter.startsWith("*")) {
                        if (treatmentId.toLowerCase().contains(filter.substring(1).toLowerCase())) {
                            guiTreatmentListModel.addElement(treatmentId);
                        }
                    }
                    else {
                        if (treatmentId.toLowerCase().startsWith(filter.toLowerCase())) {
                            guiTreatmentListModel.addElement(treatmentId);
                        }
                    }
                }
                else {
                    guiTreatmentListModel.addElement(treatmentId);
                }
            }
        }
    }


    void initTreatmentList() throws RequestException {
        filterTextField.setText("");
        String repositoryId = getCurrentRepository();

        if (repositoryId != null) {
            GuiTreatmentListModel guiTreatmentListModel = new GuiTreatmentListModel(ctxt, repositoryId);
            List<String> treatmentIdList = guiTreatmentListModel.getTreatmentIdList();
            if (alphaSortButton.isSelected()) {
                String[] treatmentIdListAsArray = treatmentIdList.toArray(new String[treatmentIdList.size()]);
                Arrays.sort(treatmentIdListAsArray, new Comparator<String>() {
                    public int compare(String s1, String s2) {
                        return s1.compareToIgnoreCase(s2);
                    }
                });
                treatmentIdList = Arrays.asList(treatmentIdListAsArray);
            }
            for (String treatmentId : treatmentIdList) {
                guiTreatmentListModel.addElement(treatmentId);
            }
            guiTreatmentList.setModel(guiTreatmentListModel);
        }
        else {
            guiTreatmentList.setModel(new DefaultListModel());
        }
    }


    private void exportRepository() throws IOException, RepositoryException, RequestException {
        if (!trtModelLocalSave.isEmpty() || treatmentHasChanged) {
            int result = JOptionPane.showInternalConfirmDialog(frame,
                                                               "Voulez vous enregistrer les modifications en cours ?",
                                                               "Demande de confirmation",
                                                               JOptionPane.YES_NO_CANCEL_OPTION,
                                                               JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
            if (result == JOptionPane.YES_OPTION) {
                actionSaveAll();
            }
        }
        filterTextField.setText("");
        String repositoryId = getCurrentRepository();

        BufferedWriter bufferedWriter = null;
        try {
            if (repositoryId != null) {
                String repositoryName = RepositoryClientHelper.getRepositoryName(ctxt, repositoryId);
                String repositoryPath = GuiUtils.showChooserForExport(repositoryName + ".xml",
                                                                      "Sauvegarde du repository "
                                                                      + repositoryName, "xml", "xml", frame);
                if (repositoryPath == null) {
                    return;
                }
                bufferedWriter = new BufferedWriter(new FileWriter(repositoryPath));
                bufferedWriter.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n<root>");
                List<String> treatmentIdList =
                      TreatmentClientHelper.getAllTreatmentModelId(ctxt, repositoryId);
                if (alphaSortButton.isSelected()) {
                    String[] treatmentIdListAsArray =
                          treatmentIdList.toArray(new String[treatmentIdList.size()]);
                    Arrays.sort(treatmentIdListAsArray, new Comparator<String>() {
                        public int compare(String s1, String s2) {
                            return s1.compareToIgnoreCase(s2);
                        }
                    });
                    treatmentIdList = Arrays.asList(treatmentIdListAsArray);
                }
                for (String treatmentId : treatmentIdList) {
                    String treatmentModelXml = TreatmentClientHelper.getTreatmentModel(ctxt,
                                                                                       repositoryId,
                                                                                       treatmentId);
                    bufferedWriter.write("\n" + treatmentModelXml);
                }
                bufferedWriter.write("\n</root>");
                JOptionPane.showMessageDialog(frame, String.format("Le repository %s a bien été exporté.",
                                                                   repositoryName),
                                              "Récapitulatif", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        finally {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        }
    }


    private void actionGuiTreatmentListSelectionned() {
        if (guiTreatmentList.getSelectedIndex() == -1) {
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    enableDetail(true);
                    saveCurrentTreatmentModel();
                    TreatmentModel treatmentModel = getCurrentTreatmentModel();
                    if (treatmentModel != null) {
                        treatmentModelToGui(treatmentModel);
                    }
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    ;
                }
            }
        });
    }


    private void saveCurrentTreatmentModel() {
        if (treatmentHasChanged && nameTextField.getText().trim().length() != 0) {
            addTreatmentModelToSave(getCurrentRepository(), guiToTreatmentModel());
            treatmentHasChanged = false;
        }
    }


    TreatmentModel getCurrentTreatmentModel() {
        String treatmentId = getCurrentTreatmentId();
        if (treatmentId == null) {
            return null;
        }
        TreatmentModel treatmentModel = getTreatmentModelToSave(getCurrentRepository(), treatmentId);
        if (treatmentModel == null) {
            try {
                String treatmentModelXml =
                      TreatmentClientHelper.getTreatmentModel(ctxt, getCurrentRepository(), treatmentId);
                treatmentModel = TreatmentModelCodec.decode(treatmentModelXml);
            }
            catch (RequestException ex) {
                GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
            }
        }
        return treatmentModel;
    }


    private String getCurrentTreatmentId() {
        int index = guiTreatmentList.getSelectedIndex();
        if (index != -1) {
            return (String)guiTreatmentList.getModel().getElementAt(index);
        }
        else {
            return null;
        }
    }


    private void quitCommand() {
        if (!trtModelLocalSave.isEmpty() || treatmentHasChanged) {
            int result = JOptionPane.showInternalConfirmDialog(frame,
                                                               "Voulez vous enregistrer les modifications ?",
                                                               "Demande de confirmation",
                                                               JOptionPane.YES_NO_CANCEL_OPTION,
                                                               JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
            if (result == JOptionPane.YES_OPTION) {
                actionSaveAll();
            }
        }
        frame.dispose();
    }


    void createPopupOnGuiTreatmentList() {
        popupCopyTreatment.removeAll();
        JMenuItem titleMenuItem = new JMenuItem("Copier vers le référentiel de traitement");
        titleMenuItem.setBackground(Color.darkGray);
        titleMenuItem.setForeground(Color.white);
        popupCopyTreatment.add(titleMenuItem);
        popupCopyTreatment.addSeparator();

        PopupMenuActionListener actionListener = new PopupMenuActionListener();

        for (int i = 0; i < repositoryComboBox.getItemCount(); i++) {
            String repoDestId = (String)repositoryComboBox.getModel().getElementAt(i);
            JMenuItem menuItem = new JMenuItem(repositoryComboBox.translateValue(repoDestId));
            menuItem.putClientProperty(PopupMenuActionListener.KEY, repoDestId);
            popupCopyTreatment.add(menuItem);
            menuItem.addActionListener(actionListener);
        }
    }


    boolean isTreatmentInRepository(TreatmentModel treatmentModel, String repositoryId)
          throws RequestException {
        return Boolean.valueOf(TreatmentClientHelper.manageTreatmentModel(ctxt, IS_EXIST,
                                                                          repositoryId,
                                                                          TreatmentModelCodec.encode(
                                                                                treatmentModel)));
    }


    void reloadRepositoryComboBox() throws RequestException {
        repositoryComboBox.loadData();
    }


    private class TreatmentListRenderer extends JLabel implements ListCellRenderer {
        private TreatmentListRenderer() {
            setOpaque(true);
        }


        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            String treatmentId = (String)value;

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            if (getTreatmentModelToSave(getCurrentRepository(), treatmentId) != null) {
                setForeground(Color.red);
            }
            if (guiTreatmentList.getModel() instanceof GuiTreatmentListModel) {
                GuiTreatmentListModel guiTreatmentListModel
                      = (GuiTreatmentListModel)guiTreatmentList.getModel();
                if (guiTreatmentListModel.getUserTreatmentIdList().contains(treatmentId)) {
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                else {
                    setFont(getFont().deriveFont(Font.PLAIN));
                }
            }
            setText(treatmentId);
            return this;
        }
    }

    private class MyMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent event) {
            maybeShowPopup(event);
        }


        @Override
        public void mouseReleased(MouseEvent event) {
            maybeShowPopup(event);
        }


        private void maybeShowPopup(MouseEvent evt) {
            if (evt.isPopupTrigger()) {
                if (guiTreatmentList.getSelectedIndex() != -1) {
                    popupCopyTreatment.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        }
    }

    private class TreatmentChangedActionListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            treatmentChangedAction();
        }
    }

    private class TreatmentChangedDocumentListener extends DocumentListenerAdapter {

        @Override
        protected void actionPerformed(DocumentEvent evt) {
            treatmentChangedAction();
        }
    }

    private class PopupMenuActionListener implements ActionListener {
        public static final String KEY = "repoDestId";


        public void actionPerformed(ActionEvent event) {
            try {
                String repoDestId = (String)((JMenuItem)event.getSource()).getClientProperty(KEY);
                if (getGuiTreatmentList().getSelectedIndex() != -1) {
                    treatmentModelGuiActionUtils.actionCopyTreatment(repoDestId, getCurrentRepository());
                }
            }
            catch (Exception ex) {
                GuiUtils.showErrorDialog(frame.getDesktopPane(), getClass(),
                                         "Erreur de copie de traitement : ", ex);
            }
        }
    }

    private class SaveAllRunnable implements Runnable {
        private String selectedTreatmentId;


        private SaveAllRunnable(String selectedTreatmentId) {
            this.selectedTreatmentId = selectedTreatmentId;
        }


        public void run() {
            waitingPanel.setText("Sauvegarde en cours...");
            try {
                saveCurrentTreatmentModel();
                for (Entry<String, Map<String, TreatmentModel>> entry : trtModelLocalSave.entrySet()) {
                    Map<String, TreatmentModel> map = entry.getValue();
                    for (Entry<String, TreatmentModel> entry1 : map.entrySet()) {
                        TreatmentModel treatmentModel = entry1.getValue();
                        TreatmentClientHelper.manageTreatmentModel(ctxt, UPDATE, entry.getKey(),
                                                                   TreatmentModelCodec.encode(treatmentModel));
                        if (Log.isInfoEnabled()) {
                            Log.info(getClass(),
                                     String.format("Sauvegarde du traitement %s (repositoryId = %s)",
                                                   treatmentModel.getId(), entry.getKey()));
                        }
                    }
                }
                trtModelLocalSave.clear();
                saveAllButton.setEnabled(false);
                initTreatmentList();
                RepositoryClientHelper.reinitializeRepositoryCache(ctxt);
                if (selectedTreatmentId != null) {
                    guiTreatmentList.setSelectedValue(selectedTreatmentId, true);
                }
            }
            catch (RequestException ex) {
                GuiUtils.showErrorDialog(frame, getClass(), "Erreur de sauvegarde des données : ", ex);
            }
        }
    }

    private static class TrtModelXmlAreaDocumentListener extends DocumentListenerAdapter {
        private JButton button;


        private TrtModelXmlAreaDocumentListener(JButton button) {
            this.button = button;
        }


        @Override
        protected void actionPerformed(DocumentEvent evt) {
            button.setEnabled(true);
        }
    }
}
